package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onSpeechRecognizerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_STOP_CAPTURE -> onStopCapture(sdk, directive, parts)
        AlexaConst.NAME_EXPECT_SPEECH -> onExpectSpeech(sdk, directive, parts)
        else -> Logger.w("unsupported SpeechRecognizer - $directive")
    }
}

private fun onExpectSpeech(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    try {
        val dialogId = directive.header!!.getString("dialogRequestId")
        val timeout = directive.payload!!.getInt("timeoutInMilliseconds")
        val initiator  = directive.payload!!.getJsonObject("initiator", false)
        var initiatorType: String? = null
        var initiatorToken: String? = null
        initiator?.let {
            initiatorType = initiator.getString("type", false)
            initiatorToken = initiator.getJsonObject("payload", false)?.getString("token")
        }

        val action = ActionWrapper(SDKConst.ACTION_ALEXA_SPEECH_EXPECT).apply {
            val payload = buildJsonObject {
                put("dialogId", dialogId)
                initiatorType?.let { put("initiatorType", initiatorType) }
                initiatorToken?.let { put("initiatorToken", initiatorToken) }
                put("timeout", timeout)
            }

            setPayload(payload)
        }.build()

        sdk.onActionListener.onAction(action.toString(), null, object: OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                Logger.d("$action.name result - $data")
            }
        })
    } catch (e: Exception) {
        Logger.w("onExpectSpeech exception - ${e.message}")
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