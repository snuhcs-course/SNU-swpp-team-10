package com.example.calendy.view.messageview

data class RevisionLog(
    var added_success: Int=0,
    var added_fail: Int=0,
    var updated_success: Int=0,
    var updated_fail: Int=0,
    var deleted_success: Int=0,
    var deleted_fail: Int=0,
    var select_success: Int=0,
    var select_fail: Int=0,


) {
    fun serialize(): String {
        return "($added_success,$added_fail,$updated_success,$updated_fail,$deleted_success,$deleted_fail,$select_success,$select_fail)"
    }

    fun hasRevision(): Boolean {
        return added_success >0 || updated_success >0 || deleted_success >0 || select_success >0
    }

    fun hasFailures(): Boolean {
        return (added_success == 0 && added_fail >0) ||
                (updated_success == 0 && updated_fail >0) ||
                (deleted_success == 0 && deleted_fail >0) ||
                (select_success == 0 && select_fail >0)
    }

    fun getSuccessLog(): String {
        val sb = StringBuilder()
        if (added_success > 0) sb.append(" $added_success 개의 일정을 추가했어요.\n")
        if (updated_success > 0) sb.append(" $updated_success 개의 일정을 수정했어요.\n")
        if (deleted_success > 0) sb.append(" $deleted_success 개의 일정을 삭제했어요.\n")
        if (select_success > 0) sb.append(" $select_success 개의 일정을 발견했어요.\n")
        return sb.toString()
    }

}
fun deserialize(str: String): RevisionLog {
    val list = str
        .removeSurrounding(prefix = "(", suffix = ")")
        .split(",")

    return RevisionLog(
        added_success = list[0].toInt(),
        added_fail = list[1].toInt(),
        updated_success = list[2].toInt(),
        updated_fail = list[3].toInt(),
        deleted_success = list[4].toInt(),
        deleted_fail = list[5].toInt(),
        select_success = list[6].toInt(),
        select_fail = list[7].toInt(),
    )
}
