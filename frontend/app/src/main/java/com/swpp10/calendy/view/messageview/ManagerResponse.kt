package com.swpp10.calendy.view.messageview

object ManagerResponse {
    const val NO_INTERNET = "인터넷에 연결되어 있지 않아요."
    const val PLEASE_WAIT = "AI_THINKING"
    const val ERROR = "매니저가 고장났어요."
    const val ERROR_SERVER = "AI Manager 서버가 응답하지 않아요. 서비스가 종료되었을 수 있어요."
    const val ERROR_SEMICOLON = "세미콜론(;) 때문일 수 있어요"
    const val BRIEFING_PLAN_PLEASE_WAIT = "제가 요약해드릴게요. 잠시만 기다려주세요."
    const val SUCCESS_REVISION = "말씀하신 요청을 반영했어요!"
    const val FAIL_REVISION_1 = "일부 요청에 대한 일정은 찾을 수 없었어요." // SUCCESS_REVISION 있을 때
    const val FAIL_REVISION_2 = "죄송해요. 요청에 대한 일정을 찾을 수 없었어요."     // SUCCESS_REVISION 없을 때
}

object ManagerHelp {
    const val HELP_USER = "도움말"
    val HELP_MESSAGE_LIST = listOf(
        "안녕하세요. 저는 당신의 일정을 관리해주는 매니저입니다.\n저는 일정을 추가하고, 수정하고, 삭제할 수 있어요.",
        "저에게 할 일을 말해보세요. 예를 들어, '내일 6시부터 7시까지 저녁 약속을 추가해줘' 라고 말해보세요.",
        "변경: '내일 약속을 토요일로 수정해줘' 라고 말해보세요.",
        "삭제: '내일 저녁 약속을 삭제해줘' 라고 말해보세요.",
        "저는 일정을 요약해줄 수 있어요. '내일 일정을 요약해줘' 라고 말해보세요.",
    )
}
