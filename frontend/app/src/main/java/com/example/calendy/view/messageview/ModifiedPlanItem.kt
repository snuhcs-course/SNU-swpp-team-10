package com.example.calendy.view.messageview

import com.example.calendy.data.maindb.plan.Plan
import com.example.calendy.data.maindb.plan.Schedule
import com.example.calendy.data.maindb.plan.Todo
import com.example.calendy.utils.getTimeInfo
import java.util.Date

data class ModifiedPlanItem(
    val historyId: Int,
    val planBefore: Plan?,
    val planAfter: Plan?,
    val queryType: QueryType
){
    val titlePlan :Plan

    val beforeText: String
    val afterText: String

    var isValid :Boolean    // could find required plans

    init{
        var selectedTitlePlan =
        when (queryType) {
            QueryType.INSERT -> planAfter
            QueryType.UPDATE -> planAfter
            QueryType.DELETE -> planBefore
            QueryType.SELECT -> planAfter
            QueryType.NOT_FOUND -> planAfter
            QueryType.UNEXPECTED -> planAfter
        }

        // do validation checking with titlePlan
        // plan has been modified by other use cases
        if(selectedTitlePlan == null)
        {
            isValid = false

            titlePlan = Schedule(-1, "삭제된 기록", Date(), Date(), "")
            beforeText = ""
            afterText = ""
        }
        else {
            isValid = true

            titlePlan = selectedTitlePlan

            // compare two plans if it is update
            if (queryType==QueryType.UPDATE) {
                val sbBefore = StringBuilder()
                val sbAfter = StringBuilder()

                // title compare
                if (planBefore!!.title!=planAfter!!.title) {
                    sbBefore.append(planBefore.title)
                    sbBefore.append("\n")
                    sbAfter.append(planAfter.title)
                    sbAfter.append("\n")
                }

                // time compare
                val timeDifferent: Boolean = when (planAfter) {
                    is Schedule -> {
                        val scheduleBefore = planBefore as Schedule
                        scheduleBefore.startTime!=planAfter.startTime || scheduleBefore.endTime!=planAfter.endTime
                    }

                    is Todo     -> {
                        val todoBefore = planBefore as Todo
                        todoBefore.dueTime!=planAfter.dueTime

                    }
                }
                if (timeDifferent) {
                    sbBefore.append(planBefore.getTimeInfo())
                    sbBefore.append("\n")
                    sbAfter.append(planAfter.getTimeInfo())
                    sbAfter.append("\n")
                }

                // memo compare
                if (planBefore.memo!=planAfter.memo) {
                    sbBefore.append(planBefore.memo)
                    sbBefore.append("\n")

                    sbAfter.append(planAfter.memo)
                    sbAfter.append("\n")
                }

                // priority compare
                if (planBefore.priority!=planAfter.priority) {
                    sbBefore.append("중요도:")
                    sbBefore.append(planBefore.priority)
                    sbBefore.append("\n")

                    sbAfter.append("중요도:")
                    sbAfter.append(planAfter.priority)
                    sbAfter.append("\n")
                }

                when (planAfter) {
                    is Todo -> {
                        val todoBefore = planBefore as Todo
                        val todoAfter = planAfter as Todo
                        if (todoBefore.complete!=todoAfter.complete) {
                            sbBefore.append(if (todoBefore.complete) "완료" else "미완료")
                            sbBefore.append("\n")

                            sbAfter.append(if (todoAfter.complete) "완료" else "미완료")
                            sbAfter.append("\n")
                        }
                    }

                    else    -> {}
                }

                // etc
                if (planBefore.categoryId!=planAfter.categoryId || planBefore.repeatGroupId!=planAfter.repeatGroupId || planBefore.showInMonthlyView!=planAfter.showInMonthlyView || planBefore.isOverridden!=planAfter.isOverridden) {
                    sbBefore.append("기타 정보")
                    sbAfter.append("기타 정보")
                }

                beforeText = sbBefore.trim().toString()
                afterText = sbAfter.trim().toString()
            } else {
                beforeText = if (planBefore!=null) planBefore!!.getTimeInfo() else ""
                afterText = if (planAfter!=null) planAfter!!.getTimeInfo() else ""
            }
        }
    }



}
