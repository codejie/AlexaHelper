package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun setDNDAction(sdk: SmartWatchSDK, action: ActionWrapper) {

    val enabled: Boolean = action.getPayload()!!.getBoolean("enabled")!!

    val event: JsonObject = EventBuilder(
        AlexaConst.NS_ALEXA_DO_NOT_DISTURB,
        AlexaConst.NAME_DO_NOT_DISTURB_CHANGED).apply {
            addPayload("enabled", enabled)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("enabled", enabled)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }
}

fun setTimeZoneAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val timezone = action.getPayload()!!.getString("timeZone")!!
    val event = EventBuilder(
        AlexaConst.NS_SYSTEM,
        AlexaConst.NAME_TIME_ZONE_CHANGED).apply {
            addPayload("timeZone", timezone)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("timeZone", timezone)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }
}

fun setLocalesAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val locales = action.getPayload()!!.getJsonArray("locales")!!
    val event = EventBuilder(
        AlexaConst.NS_SYSTEM,
        AlexaConst.NAME_LOCALES_CHANGED).apply {
        addPayload("locales", locales)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("locales", locales)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }
}