package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onAlexaDoNotDisturbDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_DO_NOT_DISTURB -> onSetDoNotDisturb(sdk, directive, parts)
        else -> Logger.w("unsupported Alexa.DoNotDisturb - $directive")
    }
}

private fun onSetDoNotDisturb(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val enabled = directive.payload?.getBoolean("enabled", false)
    if (enabled != null) {
        val action = ActionWrapper(SDKConst.ACTION_ALEXA_DND_CHANGED).apply {
            val payload = buildJsonObject {
                put("enabled", enabled)
            }
            setPayload(payload)
        }.build()

        sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                try {
                    val result = ResultWrapper.parse(data, extra)
                    val enabled = result.getPayload()!!.getBoolean("enabled")!!
                    reportDoNotDisturb(sdk, enabled)
                } catch (e: Exception) {
                    Logger.w("${SDKConst.ACTION_ALEXA_DND_CHANGED} Result exception - ${e.message}")
                }
            }
        })
    } else {
        Logger.w("$directive missing filed - enabled")
    }
}

private fun reportDoNotDisturb(sdk: SmartWatchSDK, enabled: Boolean) {
    val event = EventBuilder(AlexaConst.NS_ALEXA_DO_NOT_DISTURB, AlexaConst.NAME_REPORT_DO_NOT_DISTURB).apply {
        addPayload("enabled", enabled)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

