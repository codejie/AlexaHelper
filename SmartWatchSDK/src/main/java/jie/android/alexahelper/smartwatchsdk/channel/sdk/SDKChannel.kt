package jie.android.alexahelper.smartwatchsdk.channel.sdk

import jie.android.alexahelper.smartwatchsdk.SDKNotification
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
                    ChannelData.DataType.Notification -> onNotification(data.data as SDKNotification)
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

    private fun onNotification(notification: SDKNotification) {
        sdk.onNotification(notification)
    }

    private fun onTimer(timer: SDKScheduler.Timer) {
        sdk.onTimer(timer)
    }

    private fun onDirectiveParts(directiveParts: List<DirectiveParser.Part>) {
        for (part in directiveParts) {
            if (part.type == DirectiveParser.PartType.DIRECTIVE) {
                val directive = Directive.parse((part as DirectiveParser.DirectivePart).directive)
                when (directive?.namespace) {
                    AlexaConst.NS_ALEXA -> onAlexaDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_NOTIFICATIONS -> onNotificationsDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_ALEXA_DO_NOT_DISTURB -> onAlexaDoNotDisturbDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SPEECH_SYNTHESIZER -> onSpeechSynthesizerDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_ALEXA_API_GATEWAY -> onAlexaApiGatewayDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SYSTEM -> onSystemDirective(sdk, directive, directiveParts)
                    else -> Logger.w("unsupported - ${directive.toString()}")
                }
            }
        }
    }


}