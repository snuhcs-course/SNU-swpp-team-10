package com.example.calendy.ui.theme

import androidx.compose.ui.graphics.Color
import com.example.calendy.R
import com.example.calendy.data.maindb.plan.Plan

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val Blue1 = Color(0xFFE5EEFF)
val Blue2 = Color(0xFFB2CDFF)
val Blue3 = Color(0xFF80ACFF)
val Blue4 = Color(0xFF337AFF)
val Blue5 = Color(0xFF0058FF)

val Light_Gray = Color(0xFF737373)
val Light_Green = Color(0xFFF4BC574)

fun PriorityColor(priority: Int): Int {
    // Not working properly with other methods
    return when (priority) {
        1 -> 0xFFE5EEFF.toInt()
        2 -> 0xFFB2CDFF.toInt()
        3 -> 0xFF80ACFF.toInt()
        4 -> 0xFF337AFF.toInt()
        5 -> 0xFF0058FF.toInt()
        else -> 0xFFE5EEFF.toInt()
    }
}

fun Plan.getColor():Color{
    return when(priority){
        1 -> Blue1
        2 -> Blue2
        3 -> Blue3
        4 -> Blue4
        5 -> Blue5
        else -> Blue1
    }
}