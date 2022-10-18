package jie.android.alexahelper.smartwatchsdk.channel

import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.coroutines.*
import okhttp3.Response
import okio.BufferedSource
import java.nio.charset.StandardCharsets

class DownChannel () {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private var job: Job? = null

    fun start(response: Response, callback: DownChannelCallback): Unit {
        stop()

        val source: BufferedSource = response.body!!.source()
        job = scope.launch(Dispatchers.IO) {
            try {
                while (!source.exhausted()) {
                    val content: String =
                        source.buffer.readString(source.buffer.size, StandardCharsets.UTF_8)
                    scope.launch(Dispatchers.Main) {
//                callback(Message.LOGIN, content)
                    }
                }
            } catch (e: Exception) {
                Logger.w("Down Channel fail - ${e.message}")
            } finally {
                response.close()
            }
        }
    }

    fun stop(): Unit {
        job?.cancel()
    }

}