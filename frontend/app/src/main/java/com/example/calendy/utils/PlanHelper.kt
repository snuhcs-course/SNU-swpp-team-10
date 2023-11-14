package com.example.calendy.utils

import com.example.calendy.data.PlanType
import com.example.calendy.data.plan.Plan
import com.example.calendy.data.plan.Schedule
import com.example.calendy.data.plan.Todo


fun Plan.getPlanType(): Int {
    return when (this) {
        is Schedule -> PlanType.SCHEDULE
        is Todo -> PlanType.TODO
    }
}

// Schedule
fun Schedule.getInfoText(): String {
    val info=StringBuilder()
    // time info
    if(!startTime.equalDay(endTime)){
        // long day plan
        info.append(startTime.toDateTimeString())
        info.append(" - ")
        info.append(endTime.toDateTimeString())
    }
    else{
        info.append(startTime.toTimeString(hour12 = true, showAmPm = true))
        info.append(" - ")
        if(startTime.isAm() == endTime.isAm())
            info.append(endTime.toTimeString(hour12 = true, showAmPm = false))
        else
            info.append(endTime.toTimeString(hour12 = true, showAmPm = true))
    }

    // memo info
    if(memo.isNotEmpty()){
        info.append(memo)
    }
    return info.toString()
}

fun Todo.getInfoText():String{
    val info=StringBuilder()
    // time info
    info.append(dueTime.toTimeString(hour12 = true, showAmPm = true))

    // memo info
    if(memo.isNotEmpty()){
        info.append(memo)
    }
    return info.toString()
}