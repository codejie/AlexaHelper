package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*

fun onAlertsDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_ALERT -> onSetAlert(sdk, directive, parts)
        AlexaConst.NAME_DELETE_ALERT -> onDeleteAlert(sdk, directive, parts)
        AlexaConst.NAME_DELETES_ALERT -> onDeleteAlerts(sdk, directive, parts)
        else -> Logger.w("unsupported Alert - $directive")
    }
}

private fun onDeleteAlerts(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val payload = directive.payload
    if (payload != null) {
        try {
            val tokens = payload.getJsonArray("tokens")!!
            val action = ActionWrapper(SDKConst.ACTION_ALEXA_ALERT_DELETED).apply {
                val payload = buildJsonObject {
                    put("tokens", tokens)
                }
                setPayload(payload)
            }

            sdk.toAction(action) { result ->
                if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
                    postDeleteAlertsSucceeded(sdk, result)
                } else {
                    postDeleteAlertsFailed(sdk, result)
                }
            }
        } catch (e: Exception) {
            Logger.w("DeleteAlert exception - ${e.message}")
        }
    }
}

private fun postDeleteAlertsFailed(sdk: SmartWatchSDK, result: ResultWrapper) {
    val tokens = result.getPayload()!!.getJsonArray("tokens")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_DELETE_ALERTS_FAILED).apply {
        addPayload("tokens", tokens)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

private fun postDeleteAlertsSucceeded(sdk: SmartWatchSDK, result: ResultWrapper) {
    val tokens = result.getPayload()!!.getJsonArray("tokens")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_DELETE_ALERTS_SUCCEEDED).apply {
        addPayload("tokens", tokens)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

private fun onDeleteAlert(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val payload = directive.payload
    if (payload != null) {
        try {
            val token = payload.getString("token")!!
            val action = ActionWrapper(SDKConst.ACTION_ALEXA_ALERT_DELETED).apply {
                val payload = buildJsonObject {
                    put("tokens", buildJsonArray {
                        add(token)
                    })
                }
                setPayload(payload)
            }

            sdk.toAction(action) { result ->
                if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
                    postDeleteAlertSucceeded(sdk, result)
                } else {
                    postDeleteAlertFailed(sdk, result)
                }
            }

        } catch (e: Exception) {
            Logger.w("DeleteAlert exception - ${e.message}")
        }
    }
}

private fun postDeleteAlertFailed(sdk: SmartWatchSDK, result: ResultWrapper) {
    val tokens = result.getPayload()!!.getJsonArray("tokens")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_DELETE_ALERT_FAILED).apply {
        addPayload("token", tokens[0].jsonPrimitive.content)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

private fun postDeleteAlertSucceeded(sdk: SmartWatchSDK, result: ResultWrapper) {
    val tokens = result.getPayload()!!.getJsonArray("tokens")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_DELETE_ALERT_SUCCEEDED).apply {
        addPayload("token", tokens[0].jsonPrimitive.content)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

private fun onSetAlert(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val payload = directive.payload
    if (payload != null) {
        try {
//            when (val type = payload.getString("type")) {
//                "TIMER" -> onSetTimer(payload, sdk, directive, parts)
//                "ALARM" -> onSetAlarm(payload, sdk, directive, parts)
//                "REMINDER" -> onSetReminder(payload, sdk, directive, parts)
//                else -> Logger.w("Unknown ALERT type - $type")
//            }
            val type = payload.getString("type", false)?: "ALERM"
            val dialogId = directive.header.getString("dialogRequestId",false)
            val token = payload.getString("token")
            val scheduledTime = payload.getString("scheduledTime")
            val loopCount = payload.getInt("loopCount", false) ?: 0
            val loopPause = payload.getInt("loopPause", false) ?: 0
            val label = payload.getString("label", false)
//            val startTime = payload.getString("originalTime")

            val action = ActionWrapper(SDKConst.ACTION_ALEXA_ALERT_ADDED).apply {
                val payload = buildJsonObject {
                    dialogId?.also { put("dialogId", dialogId) }
                    put("token", token)
                    put("type", type)
                    put("scheduledTime", scheduledTime)
                    put("loopCount", loopCount)
                    put("loopPause", loopPause)
                    label.also { put("label", label) }
//                    put("startTime", startTime)
                }

                setPayload(payload)
            }

            sdk.toAction(action) { result ->
                if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
                    postSetAlertSucceeded(sdk, result)
                } else {
                    postSetAlertFailed(sdk, result)
                }
            }

        } catch (e: Exception) {
            Logger.w("setAlert exception - ${e.message}")
        }
    } else {
        Logger.w("Alerts SetAlert missing - payload")
    }
}

private fun postSetAlertFailed(sdk: SmartWatchSDK, result: ResultWrapper) {
    val token = result.getPayload()!!.getString("token")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_SET_ALERT_FAILED).apply {
        addPayload("token", token)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

private fun postSetAlertSucceeded(sdk: SmartWatchSDK, result: ResultWrapper) {
    val token = result.getPayload()!!.getString("token")!!
    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_SET_ALERT_SUCCEEDED).apply {
        addPayload("token", token)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}