package com.swpp10.calendy.utils

import com.swpp10.calendy.data.maindb.plan.PlanType
import com.swpp10.calendy.data.maindb.plan.Plan
import com.swpp10.calendy.data.maindb.plan.Schedule
import com.swpp10.calendy.data.maindb.plan.Todo
import com.prolificinteractive.materialcalendarview.CalendarDay
import java.util.Hashtable


fun Plan.getPlanType(): PlanType {
    return when (this) {
        is Schedule -> PlanType.SCHEDULE
        is Todo     -> PlanType.TODO
    }
}
fun Plan.getInfoText(): String {
    return when (this) {
        is Schedule -> this.getInfoText()
        is Todo     -> this.getInfoText()
    }
}
fun Plan.getTimeInfo(): String {
    return when (this) {
        is Schedule -> this.getTimeInfo()
        is Todo     -> this.getTimeInfo()
    }
}

fun Plan.getPriorityString():String{
    when(priority){
        0 -> return "⭐"
        1 -> return "⭐⭐"
        2 -> return "⭐⭐⭐"
        3 -> return "⭐⭐⭐⭐"
        4 -> return "⭐⭐⭐⭐⭐"
        else -> return "⭐"
    }
}

// Schedule
fun Schedule.getTimeInfo():String{

    return if(!startTime.equalDay(endTime)){
        "${startTime.toDateString(true)} ~ ${endTime.toDateString(true)}"
    }
    else{
        if(startTime.isStartOfDay() && endTime.isEndOfDay())
            "${startTime.toDateString(true)} 종일"
        else if(startTime.isAm() == endTime.isAm())
            "${startTime.toDateTimeString(true)} ~ ${endTime.toTimeString(hour12 = true, showAmPm = false)}"
        else
            "${startTime.toDateTimeString(true)} ~ ${endTime.toTimeString(hour12 = true, showAmPm = true)}"
    }
}

fun Schedule.getInfoText(): String {
    val info=StringBuilder()
    // time info
    if(getDiffBetweenDates(startTime, endTime) + (if (!endTime.isZeroTime()) 1 else 0) >= 2){
        // long day plan
        info.append(startTime.toDateString(false))
        info.append(" ~ ")
        info.append(endTime.toDateString(false))
    }
    else{
        info.append(startTime.toTimeString(hour12 = true, showAmPm = true))
        info.append(" ~ ")
        if(startTime.isAm() == endTime.isAm())
            info.append(endTime.toTimeString(hour12 = true, showAmPm = false))
        else
            info.append(endTime.toTimeString(hour12 = true, showAmPm = true))
    }
    // memo info
    if(memo.isNotEmpty()){
        info.append(" $memo")
    }
    return info.toString()
}
// Tod0
fun Todo.getTimeInfo():String{
    return if(dueTime.isEndOfDay())
            dueTime.toDateString(true)
        else
            dueTime.toDateTimeString(true)
}

fun Todo.getInfoText():String{
    val info=StringBuilder()
    // time info
    if (dueTime.isEndOfDay())
        info.append("")
    else
        info.append(dueTime.toTimeString(hour12 = true, showAmPm = true))

    // memo info
    if(memo.isNotEmpty()){
        info.append(" $memo")
    }
    return info.toString()
}

