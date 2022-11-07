package jie.android.alexahelper.smartwatchsdk.channel.sdk

import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.alexa.*
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
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
                    ChannelData.DataType.Timer -> onTimer(data.data as SDKScheduler.Timer)
                    ChannelData.DataType.DirectiveParts -> onDirectiveParts(data.data as List<DirectiveParser.Part>)
                    ChannelData.DataType.Notification -> onNotification(data.data as SDKNotification.Message)
                    else -> Logger.w("receive unknown type - ${data.type}")
                }
            } while (true)
        }
    }

    suspend fun send(data: ChannelData) {
        channel.send(data)
    }

    fun stop() {
        channel.close()
        job?.cancel()
    }

    private fun onNotification(message: SDKNotification.Message) {
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

    private fun onTimer(timer: SDKScheduler.Timer) {
        when (val type = timer.param!! as SDKScheduler.TimerType) {
            SDKScheduler.TimerType.DOWN_CHANNEL_PING -> onTimerDownChannelPing()
            SDKScheduler.TimerType.TOKEN_REFRESH -> onTimerTokenRefresh()
            else -> Logger.w("Unknown Timer type - $type")
        }
    }

    private fun onTimerTokenRefresh() {
        sdk.refreshToken()
    }

    private fun onTimerDownChannelPing() {
        sdk.pingDownChannel()
    }

    private fun onDirectiveParts(directiveParts: List<DirectiveParser.Part>) {
        try {
            for (part in directiveParts) {
                if (part.type == DirectiveParser.PartType.DIRECTIVE) {
                    val directive = Directive.parse((part as DirectiveParser.DirectivePart).directive)
                    when (directive?.namespace) {
                        AlexaConst.NS_ALEXA -> onAlexaDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_NOTIFICATIONS -> onNotificationsDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_ALEXA_DO_NOT_DISTURB -> onAlexaDoNotDisturbDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_SPEECH_RECOGNIZER -> onSpeechRecognizerDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_SPEECH_SYNTHESIZER -> onSpeechSynthesizerDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_ALEXA_API_GATEWAY -> onAlexaApiGatewayDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_SYSTEM -> onSystemDirective(sdk, directive, directiveParts)
                        AlexaConst.NS_ALERTS -> onAlertsDirective(sdk, directive, directiveParts)
                        else -> Logger.w("unsupported - ${directive.toString()}")
                    }
                }
            }
        } catch (e: Exception) {
            Logger.w("alexa directive exception - ${e.message}")
        }
    }


}