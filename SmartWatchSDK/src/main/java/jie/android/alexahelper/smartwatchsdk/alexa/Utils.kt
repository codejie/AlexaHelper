package jie.android.alexahelper.smartwatchsdk.alexa

import java.util.*

object Utils {
    fun makeMessageId(): String {
        return UUID.randomUUID().toString()
    }
}