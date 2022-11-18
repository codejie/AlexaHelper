package jie.android.alexahelper.smartwatchsdk.channel.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.sdk.ChannelData
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKNotification
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.coroutines.*
import okhttp3.Response
import okio.BufferedSource
import java.nio.charset.StandardCharsets

class DownChannel constructor(val sdk: SmartWatchSDK) {
//    private val scope = CoroutineScope(Dispatchers.IO)
    private var job: Job? = null

    fun start(response: Response): Unit {
//        stop()

        job = CoroutineScope(Dispatchers.IO).launch {
            val parser = DownChannelDirectiveParser()
            val source: BufferedSource = response.body!!.source()

            try {
                while (!source.exhausted()) {
                    val content: String = source.buffer.readString(source.buffer.size, StandardCharsets.UTF_8)
//                    Logger.d("down channel received - $content")
                    val parts = parser.parseParts(content)
                    parts?.also {
//                        for (part in it) {
//                            Logger.d("recv - ${part.toString()}")
//                        }

                        sdk.sdkChannel.send(ChannelData(ChannelData.DataType.DirectiveParts, parts))
                    }
                }
            } catch (e: Exception) {
                Logger.w("Down Channel fail - ${e.message}")
            } finally {
                response.close()
                sdk.sdkChannel.send(ChannelData(ChannelData.DataType.Notification, SDKNotification.Message.DOWN_CHANNEL_BREAK))
            }
        }
    }

    fun stop(): Unit {
        job?.cancel()
    }
}