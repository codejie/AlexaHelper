package jie.android.alexahelper.channel

import jie.android.alexahelper.AppDeviceCallback
import jie.android.alexahelper.InnerDeviceCallback
import jie.android.alexahelper.Message
import okhttp3.Response
import okio.BufferedSource
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread

class DownChannel (response: Response, deviceCallback: InnerDeviceCallback) {
    private val deviceCallback  = deviceCallback
    init {
        val source: BufferedSource = response.body!!.source()
        create(source)
    }

    private fun create(source: BufferedSource): Unit {
        thread (start = true) {
            while (!source.exhausted()) {
                val content: String = source.buffer.readString(source.buffer.size, StandardCharsets.UTF_8)
//                deviceCallback(Message.LOGIN, content)
            }
        }
    }
}