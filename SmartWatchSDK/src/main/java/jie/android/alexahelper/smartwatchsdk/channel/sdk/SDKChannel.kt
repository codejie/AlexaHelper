package jie.android.alexahelper.smartwatchsdk.channel.sdk

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.channel.alexa.onDirectiveParts
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.utils.SDKScheduler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch

sealed class SDKNotification constructor(val msg: Message, val data: Any? = null) {
    enum class Message {
        LOGIN_SUCCESS,
        DOWN_CHANNEL_BREAK
    }
}
data class ChannelData (val type: DataType, val data: Any? = null) {
    enum class DataType {
        DirectiveParts,
        Timer,
        Notification
    }
}

class SDKChannel constructor(private val sdk: SmartWatchSDK) {
    private val channel: Channel<ChannelData> = Channel<ChannelData>()

    private var job: Job? = null

    fun start() {
        job = CoroutineScope(Dispatchers.IO).launch {
            do {
                val data = channel.receive()
                Logger.d("sdk channel receive - ${data.toString()}")
                when (data.type) {
                    ChannelData.DataType.Timer -> onTimer(sdk, data.data as SDKScheduler.Timer)
                    ChannelData.DataType.DirectiveParts -> onDirectiveParts(sdk, data.data as List<DirectiveParser.Part>)
                    ChannelData.DataType.Notification -> onNotification(sdk, data.data as SDKNotification.Message)
                    else -> Logger.w("receive unknown type - ${data.type}")
                }
            } while (true)
        }
    }

    suspend fun send(data: ChannelData) {
        channel.send(data)
    }

    fun stop() {
        if (!(channel.isClosedForSend && channel.isClosedForReceive))
            channel.close()
        job?.cancel()
    }

    private fun onNotification(sdk: SmartWatchSDK, message: SDKNotification.Message) {
        Logger.d("onNotification - $message")
        when (message) {
            SDKNotification.Message.LOGIN_SUCCESS -> onNotificationLoginSuccess()
            SDKNotification.Message.DOWN_CHANNEL_BREAK -> onNotificationDownChannelBreak()
            else -> Logger.w("Unknown notification - $message")
        }
    }

    private fun onNotificationDownChannelBreak() {
        sdk.onDownChannelBreak()
    }

    private fun onNotificationLoginSuccess() {
        sdk.sdkScheduler.addTimer(SDKScheduler.Timer(55 *60 * 1000, true, SDKScheduler.TimerType.TOKEN_REFRESH))
//        RuntimeInfo.downChannelPingTimer = sdk.sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
    }

    private fun onTimer(sdk: SmartWatchSDK, timer: SDKScheduler.Timer) {
        Logger.d("onTimer - ${timer.param}")
        when (val type = timer.param!! as SDKScheduler.TimerType) {
            SDKScheduler.TimerType.DOWN_CHANNEL_PING -> onTimerDownChannelPing()
            SDKScheduler.TimerType.TOKEN_REFRESH -> onTimerTokenRefresh()
            SDKScheduler.TimerType.DOWN_CHANNEL_CREATE -> onTimerDownChannelCreate()
            else -> Logger.w("Unknown Timer type - $type")
        }
    }

    private fun onTimerDownChannelCreate() {
        sdk.onDownChannelBreak()
    }

    private fun onTimerTokenRefresh() {
        sdk.refreshToken()
    }

    private fun onTimerDownChannelPing() {
        sdk.pingDownChannel()
    }


}