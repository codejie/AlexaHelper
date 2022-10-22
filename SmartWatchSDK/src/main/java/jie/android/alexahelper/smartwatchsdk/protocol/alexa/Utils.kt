package jie.android.alexahelper.smartwatchsdk.protocol.alexa

import java.util.*

object Utils {
    fun makeMessageId(): String {
        return UUID.randomUUID().toString()
    }
}