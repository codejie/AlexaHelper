package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onAlertDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_ALERT -> onSetAlert(sdk, directive, parts)
        else -> Logger.w("unsupported Alert - $directive")
    }
}

private fun onSetAlert(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val payload = directive.payload;
    if (payload != null) {
        try {
            when (val type = payload.getString("type")) {
                "TIMER" -> onSetTimer(payload, sdk, directive, parts)
                "ALARM" -> onSetAlarm(payload, sdk, directive, parts)
                "REMINDER" -> onSetReminder(payload, sdk, directive, parts)
                else -> Logger.w("Unknown ALERT type - $type")
            }
        } catch (e: Exception) {
            Logger.w("setAlert exception - ${e.message}")
        }
    } else {
        Logger.w("Alerts SetAlert missing - payload")
    }
}

private fun onSetReminder(payload: JsonObject, sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}

private fun onSetAlarm(payload: JsonObject, sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val dialogId = directive.header.getString("dialogRequestId",false)
    val token = payload.getString("token")
    val scheduledTime = payload.getString("scheduledTime")
    val loopCount = payload.getInt("loopCount", false) ?: 0
    val loopPause = payload.getInt("loopPause", false) ?: 0
    val label = payload.getString("label", false)
    val startTime = payload.getString("originalTime")

    val action = ActionWrapper(SDKConst.ACTION_ALEXA_ALARM_ADDED).apply {
        val payload = buildJsonObject {
            dialogId?.also { put("dialogId", dialogId) }
            put("token", token)
            put("scheduledTime", scheduledTime)
            put("loopCount", loopCount)
            put("loopPause", loopPause)
            label.also { put("label", label) }
            put("startTime", startTime)
        }

        setPayload(payload)
    }.build()

    sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
        override fun onResult(data: String, extra: Any?) {
            val result = ResultWrapper.parse(data, extra);
            if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
                postSetAlertSucceeded(result, extra)
            } else {
                postSetAlertFailed(result, extra)
            }
        }
    })
}

private fun onSetTimer(payload: JsonObject, sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}


private fun postSetAlertFailed(result: ResultWrapper, extra: Any?) {

}

private fun postSetAlertSucceeded(result: ResultWrapper, extra: Any?) {
    TODO("Not yet implemented")
}