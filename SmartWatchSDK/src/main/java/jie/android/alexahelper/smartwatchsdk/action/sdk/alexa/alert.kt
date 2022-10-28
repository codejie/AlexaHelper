package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun alertStartAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload = action.getPayload()!!
    val token = payload.getString("token")!!
    val scheduledTime = payload.getString("scheduledTime")!!
//    val startTime = payload.getString("startTime")!!

    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_ALERT_STARTED).apply {
        addPayload("token", token)
        addPayload("scheduledTime", scheduledTime)
//        addPayload("eventTime", startTime)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("token", token)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }
}

internal fun alertEndAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload = action.getPayload()!!
    val token = payload.getString("token")!!
    val scheduledTime = payload.getString("scheduledTime")!!
//    val startTime = payload.getString("startTime")!!

    val event = EventBuilder(AlexaConst.NS_ALERTS, AlexaConst.NAME_ALERT_STOPPED).apply {
        addPayload("token", token)
        addPayload("scheduledTime", scheduledTime)
//        addPayload("eventTime", startTime)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("token", token)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }
}