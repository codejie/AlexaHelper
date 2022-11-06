package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onSpeechRecognizerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_STOP_CAPTURE -> onStopCapture(sdk, directive, parts)
        else -> Logger.w("unsupported SpeechRecognizer - $directive")
    }
}

private fun onStopCapture(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    try {
        val dialogId = directive.header!!.getString("dialogRequestId")
        val action = ActionWrapper(SDKConst.ACTION_ALEXA_SPEECH_STOP).apply {
            setPayload(buildJsonObject {

                put("dialogId", dialogId)
            })
        }.build()

        sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                Logger.d("$action.name result - $data")
            }
        })
    } catch (e: Exception) {
        Logger.w("onStopCapture exception - ${e.message}")
    }
}