package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onSpeechSynthesizerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SPEAK -> onSpeak(sdk, directive, parts)
        else -> Logger.w("unsupported SpeechSynthesizer - $directive")
    }
}

private fun onSpeak(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    try {
        val action = ActionWrapper(SDKConst.ACTION_ALEXA_SPEECH_SPEAK).apply {
            val payload = buildJsonObject {
                put("dialogId", directive.header.getString("dialogRequestId"))
                put("format", directive.payload!!.getString("format"))
                put("token", directive.payload!!.getString("token"))
                put("playMode", directive.payload!!.getString("playBehavior"))

                val caption = directive.payload!!.getJsonObject("caption", false)
                caption?.also {
                    put("caption", buildJsonObject {
                        put("content", caption.getString("content"))
                        put("type", caption.getString("type"))
                    })
                }
            }

            setPayload(payload)
        }.build()

        val audio = directive.payload!!.getString("url")!!.split(":")
        val part = parts.find {
            (it.type == DirectiveParser.PartType.OCTET_BUFFERS) && (it.headers["Content-ID"] == "<${audio[1]}>")
        }

        if (part != null) {
            sdk.onActionListener.onAction(
                action.toString(),
                (part as DirectiveParser.OctetBuffersPart).buffer,
                object :
                    OnResultCallback {
                    override fun onResult(data: String, extra: Any?) {
                        Logger.d("$action.name result - $data")
                    }
                })
        } else {
            Logger.w("Can't find audio data - ${audio[1]}")
        }
    } catch (e: Exception) {
        Logger.w("onSpeak exception - ${e.message}")
    }
}
