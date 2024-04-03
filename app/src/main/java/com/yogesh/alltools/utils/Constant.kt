package com.yogesh.alltools.utils

import android.util.Log

object Constant {
    val SOS_FlashLight = "SOS_FlashLight"
    val TODO_List = "TODO List"
    val Notes = "Notes"
    val Calculator = "Calculator"
    val QR_Scanner = "QR Scanner"
    val StopWatch = "StopWatch"
    val Screen_Flash = "Screen Flash"
    val Voice_Recorder = "Voice Recorder"
    val Image_To_PDF = "Image To PDF"
    val Age_Calculator = "Age Calculator"
    val Currency_Converter = "Currency\n Converter"

    fun mlog(data: Any) {
        Log.d("mydata", data.toString())
    }
}

sealed class Screen(val route: String) {
    data object Note : Screen("Note")
    data object EditNote : Screen("EditNote")
}

fun String.withArgs(vararg args: String): String =
    apply {
        buildString {
            args.forEach {
                append("/$it")
            }
        }
    }




