package com.example.calendy.view.messageview

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.sqlite.db.SimpleSQLiteQuery
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.calendy.data.maindb.category.Category
import com.example.calendy.data.maindb.category.ICategoryRepository
import com.example.calendy.data.maindb.history.IHistoryRepository
import com.example.calendy.data.maindb.history.ManagerHistory
import com.example.calendy.data.maindb.message.IMessageRepository
import com.example.calendy.data.maindb.message.Message
import com.example.calendy.data.maindb.plan.IPlanRepository
import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.data.network.CalendyServerApi
import com.example.calendy.data.network.MessageBody
import com.example.calendy.data.rawsqldb.RawSqlDatabase
import com.example.calendy.utils.DateHelper.toLocalTimeString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.util.Date

class SendMessageWorker(
    appContext: Context,
    workerParams: WorkerParameters,
    private val calendyServerApi: CalendyServerApi,
    private val messageRepository: IMessageRepository,
    private val planRepository: IPlanRepository,
    private val historyRepository: IHistoryRepository,
    private val categoryRepository: ICategoryRepository,
    private val rawSqlDatabase: RawSqlDatabase
) : CoroutineWorker(appContext, workerParams) {

    /**
     * Add Message into DB and return it
     */
    private suspend fun addManagerMessage(managerContent: String, userMessageId: Int): Message {
        val newMessage = Message(
            sentTime = Date(),
            messageFromManager = true,
            content = managerContent,
            userMessageId = userMessageId
        )
        val messageId: Int = messageRepository.insert(newMessage)
        return newMessage.copy(id = messageId)
    }

    override suspend fun doWork(): Result {
        val requestMessage = inputData.getString("requestMessage") ?: ""
        val userMessageId = inputData.getInt("userMessageId", 0)

        // Delete all previous manager messages (Retry)
        messageRepository.getResponseMessageGroup(userMessageId = userMessageId).forEach {
            messageRepository.delete(it)
        }

        if (!isNetworkAvailable()) {
            addManagerMessage(
                managerContent = ManagerResponse.NO_INTERNET, userMessageId = userMessageId
            )
            return Result.failure()
        }


        val isBriefing = requestMessage.contains("요약") || requestMessage.contains("브리핑")
        val briefingPlanList = mutableListOf<Plan>()

        val managerReadyingMessage = addManagerMessage(
            managerContent = ManagerResponse.PLEASE_WAIT, userMessageId = userMessageId
        )

        //region 모든 DB 정보 string 으로 변환하기
        val allCategories = categoryRepository.getCategoriesStream().first()
        // ex) (1, 과제), (2, 운동), ..., (5, 약속)
        val allCategoriesPrompt = allCategories.joinToString(", ") {
            "(${it.id},${it.title})"
        }

        val t = planRepository.getAllPlansStream().first()
        val allSchedules = t.filterIsInstance<Schedule>()
        val allSchedulesPrompt = allSchedules.joinToString(", ") {
            "(${it.id},${it.title})"
        }

        val allTodos = t.filterIsInstance<Todo>()
        val allTodosPrompt = allTodos.joinToString(", ") {
            "(${it.id},${it.title})"
        }
        //endregion

        try {
            withContext(Dispatchers.IO) {
                val resultFromServer = calendyServerApi.sendMessageToServer(
                    MessageBody(
                        message = requestMessage,
                        time = Date().toLocalTimeString(),
                        category = allCategoriesPrompt,
                        schedule = allSchedulesPrompt,
                        todo = allTodosPrompt
                    )
                )

                // handle result
                // TODO: memo에 ; 가 들어가면, GPT가 SQL Injection이나 버그를 유발하는 SQL Query를 반환한다.
                val queries = resultFromServer.trim('"').split(";").dropLast(1)
                val gptMessage = addManagerMessage(
                    managerContent = ManagerResponse.PLEASE_WAIT, userMessageId = userMessageId
                )
                for (query in queries) {
                    val selectedPlanList =
                        sqlExecute(query.trim(), gptMessage) ?: emptyList()
                    briefingPlanList.addAll(selectedPlanList)
                }
            }
        } catch (e: Throwable) {
            Log.e("GUN Message Worker", e.stackTraceToString())
            // This may be server error. Because sqlExecute has error handling
            addManagerMessage(
                managerContent = ManagerResponse.ERROR, userMessageId = userMessageId
            ) // TODO: 유저에게 어떻게 설명해야 하지?

            return Result.failure()
        } finally {
            messageRepository.delete(managerReadyingMessage)

            if (isBriefing) {
                sendBriefing(briefingPlanList, allCategories, userMessageId = userMessageId)
            }
        }

        return Result.success()
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)==true
    }

    // should update information to gptMessage
    private suspend fun sqlExecute(gptQuery: String, gptMessage: Message): List<Plan>? {


        // TODO: Refactor Me
        Log.d("GUN Worker", "Query Start: $gptQuery")
        val startsWith = gptQuery.takeWhile { it!=' ' }.uppercase()
        val queryType = when (startsWith) {
            "INSERT"       -> QueryType.INSERT
            "UPDATE"       -> QueryType.UPDATE
            "DELETE"       -> QueryType.DELETE
            "SELECT"       -> QueryType.SELECT
            "NO_SUCH_PLAN" -> QueryType.NOT_FOUND
            else           -> QueryType.UNEXPECTED
        }


        // if isSchedule is false, should query tod0 db
        val isSchedule = gptQuery.split(" ").run {
            // there is "SCHEDULE" at second or third word
            // INSERT INTO table, UPDATE table, DELETE table, DELETE FROM table
            // SELECT * FROM table
            listOf(this.getOrNull(1), this.getOrNull(2), this.getOrNull(3)).any {
                it?.startsWith("SCHEDULE", ignoreCase = true) ?: false
            }
        }
        val queryTable = if (isSchedule) "schedule" else "todo"


        fun getAffectedPlansFromGptQuery(): List<Plan> {
            // SELECT table where ... 로 교체
            // ex) UPDATE table SET ... WHERE ...
            val whereStartsAt = gptQuery.indexOf(" WHERE ", ignoreCase = true)
            val whereString = if (whereStartsAt!=-1) {
                gptQuery.substring(startIndex = whereStartsAt).trim()
            } else "" // if WHERE is not present, use empty string

            // Calendy DB의 DAO.rawQuery 로 UPDATE에 영향을 받는 planList 받기
            val calendySelectQuery = SimpleSQLiteQuery(
                "SELECT * FROM $queryTable $whereString",
            )

            return when (isSchedule) {
                true  -> planRepository.getSchedulesViaQuery(calendySelectQuery)
                false -> planRepository.getTodosViaQuery(calendySelectQuery)
            }
        }

        suspend fun insertHistory(
            gptMessage: Message,
            isSchedule: Boolean,
            queryType: QueryType,
            currentId: Int? = null,
            savedId: Int? = null
        ) {
            if (isSchedule) {
                historyRepository.insertHistory(
                    ManagerHistory(
                        messageId = gptMessage.id,
                        isSchedule = isSchedule,
                        revisionType = queryType,
                        currentScheduleId = currentId,
                        savedScheduleId = savedId
                    )
                )

            } else {
                historyRepository.insertHistory(
                    ManagerHistory(
                        messageId = gptMessage.id,
                        isSchedule = isSchedule,
                        revisionType = queryType,
                        currentTodoId = currentId,
                        savedTodoId = savedId
                    )
                )
            }


        }

        suspend fun updateGptResponseMessage(
            gptMessage: Message, planListSize: Int, queryType: QueryType, isSchedule: Boolean
        ) {
            val log : RevisionLog = if(gptMessage.content == ManagerResponse.PLEASE_WAIT) RevisionLog() else deserialize(gptMessage.content)


            when(queryType){
                QueryType.INSERT     ->
                    if(planListSize==0) log.added_fail++
                    else log.added_success+=planListSize
                QueryType.UPDATE     ->
                    if(planListSize==0) log.updated_fail++
                    else log.updated_success+=planListSize
                QueryType.DELETE     ->
                    if(planListSize==0) log.deleted_fail++
                    else log.deleted_success+=planListSize
                QueryType.SELECT     ->
                    if(planListSize==0) log.select_fail++
                    else log.select_success+=planListSize
                QueryType.NOT_FOUND  ->
                    log.select_fail++
                QueryType.UNEXPECTED ->
                    log.select_fail++
            }

            val messageString = log.serialize()
            val hasRevision = true  //always true. messages will be mapped in ui level

//            var messageString: String
//            var hasRevision = if(addTextMode) gptMessage.hasRevision else true
//            if (QueryType.NOT_FOUND==queryType) {
//                hasRevision = hasRevision || false
//                messageString = "찾으시는 플랜이 없어요"
//            } else if (planListSize==0) {
//                val planType = if (isSchedule) "일정" else "할 일"
//                messageString = when (queryType) {
//                    QueryType.INSERT                                     -> "말씀하신 ${planType}을 추가하지 못했어요."
//                    QueryType.UPDATE, QueryType.DELETE, QueryType.SELECT -> "말씀하신 ${planType}을 찾지 못했어요."
//                    else                                                 -> "죄송해요. 잘 이해하지 못했어요. "
//                }
//                hasRevision = hasRevision || false
//
//            } else {
//                messageString = "AI 매니저가 "
//                if (isSchedule) messageString += "일정 "
//                else messageString += "할 일 "
//                when (queryType) {
//                    QueryType.INSERT -> messageString += "${planListSize}개를 추가했어요"
//                    QueryType.UPDATE -> messageString += "${planListSize}개를 수정했어요"
//                    QueryType.DELETE -> messageString += "${planListSize}개를 삭제했어요"
//                    QueryType.SELECT -> messageString += "${planListSize}개를 발견했어요"
////                    QueryType.NOT_FOUND -> {messageString = "찾으시는 플랜이 없어요"
////                        hasRevision=false
////                    }
//                    else             -> {
//                        messageString = "죄송해요. 잘 이해하지 못했어요."
//                        hasRevision = hasRevision || false
//                    }
//                }
//                if (planListSize <= 0) hasRevision = hasRevision || false
//
//            }
            gptMessage.content = messageString
            gptMessage.hasRevision = hasRevision
            messageRepository.update(gptMessage)
        }

        // 우선 기존 data를 모두 삭제해둔다.
        rawSqlDatabase.deleteAll()

        var selectedPlanList: List<Plan>? = null
        try {
            val planListSize: Int
            when (queryType) {
                QueryType.INSERT    -> {
                    // RawSqlDB에 INSERT sqlQuery 실행
                    rawSqlDatabase.execSql(gptQuery)
                    // RawSqlDB에서 Select All
                    val planList = rawSqlDatabase.getAllPlans()

                    for (plan in planList) {
                        // 그 결과를 MainDB에 삽입
                        val newPlanId = planRepository.insert(plan)

                        // Manager가 변경한 사항을 기록

                        insertHistory(gptMessage, isSchedule, queryType, currentId = newPlanId)
                    }

                    //initialize planListSize for updating message

                    planListSize = planList.size
                }

                QueryType.UPDATE    -> {
                    val originalPlanList = getAffectedPlansFromGptQuery()

                    // RawSqlDB에 복사
                    for (plan in originalPlanList) {
                        val originalPlanId = plan.id

                        // Saved Plan에 변경되기 전의 plan 저장하기
                        val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                        // 결과를 Empty DB에 삽입. 이때 plan의 id가 유지된다.
                        rawSqlDatabase.insertFromPlan(plan)

                        // Manager가 변경할 예정인 사항을 기록
                        insertHistory(
                            gptMessage,
                            isSchedule,
                            QueryType.UPDATE,
                            currentId = originalPlanId,
                            savedId = savedPlanId
                        )
                    }

                    // RawSqlDB에 update sqlQuery 실행
                    rawSqlDatabase.execSql(gptQuery)
                    // RawSqlDB에 Select All
                    val planList = rawSqlDatabase.getAllPlans()
                    // 그 결과를 MainDB에 반영하기
                    for (plan in planList) {
                        planRepository.update(plan)
                    }

                    //initialize planListSize for updating message
                    planListSize = planList.size

                }

                QueryType.DELETE    -> {
                    val originalPlanList = getAffectedPlansFromGptQuery()

                    // MainDB에서 삭제
                    for (plan in originalPlanList) {
                        // Saved Plan에 변경되기 전의 plan 저장하기
                        val savedPlanId = historyRepository.insertSavedPlanFromPlan(plan).toInt()

                        // 결과를 Calendy DB에서 삭제
                        planRepository.delete(plan)

                        // Manager가 변경한 사항을 기록
                        insertHistory(
                            gptMessage, isSchedule, QueryType.DELETE, savedId = savedPlanId
                        )
                    }

                    //initialize planListSize for updating message
                    planListSize = originalPlanList.size
                }

                QueryType.SELECT    -> {
                    // SELECT 문에 의해 영향을 받는 planList 받기
                    val planList = getAffectedPlansFromGptQuery()

                    for (plan in planList) {
                        insertHistory(gptMessage, isSchedule, QueryType.SELECT, currentId = plan.id)
                    }

                    //initialize planListSize for updating message
                    planListSize = planList.size

                    selectedPlanList = planList
                }

                QueryType.NOT_FOUND -> {
                    planListSize = 0;

                }

                else                -> {
                    // if invalid request, say sorry
                    planListSize = -1;

                }

            }
            // Message DB에 Message 넣어주기
            updateGptResponseMessage(gptMessage, planListSize, queryType, isSchedule)
        } catch (e: Throwable) {
            // Catching all throwable may not be good
            Log.e("GUN Message Worker", e.stackTraceToString())
            messageRepository.update(
                gptMessage.copy(content = "으악! 에러다!", hasRevision = false)
            )
        }

        // TODO: Refactor Me
        // Return Select Query for Briefing
        return selectedPlanList
    }

    private suspend fun sendBriefing(
        briefingPlanList: List<Plan>, allCategories: List<Category>, userMessageId: Int
    ) {
        val briefingReadyingMessage = addManagerMessage(
            managerContent = ManagerResponse.BRIEFING_PLAN_PLEASE_WAIT,
            userMessageId = userMessageId
        )

        try {
            val allPlans = briefingPlanList.joinToString {
                it.toSummary { categoryId ->
                    allCategories.find { category -> category.id==categoryId }?.title ?: "None"
                }
            }
            val briefingResult = calendyServerApi.sendBriefingRequestToServer(allPlans)
            addManagerMessage(managerContent = briefingResult, userMessageId = userMessageId)
        } catch (e: Throwable) {
            Log.e("GUN Message Worker - Briefing", e.stackTraceToString())
            addManagerMessage(
                managerContent = ManagerResponse.ERROR, userMessageId = userMessageId
            ) // TODO: 유저에게 어떻게 설명해야 하지?
        } finally {
            messageRepository.delete(briefingReadyingMessage)
        }
    }

}

