package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.alexa.Event
import jie.android.alexahelper.smartwatchsdk.channel.HttpChannel
import jie.android.alexahelper.smartwatchsdk.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.sdk.getBoolean
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun setDNDAction(sdk: SmartWatchSDK, action: ActionWrapper) {

    val enabled: Boolean = action.getPayload()!!.getBoolean("enabled")!!

    val event: JsonObject = Event(
        AlexaConst.NS_ALEXA_DO_NOT_DISTURB,
        AlexaConst.NAME_DO_NOT_DISTURB_CHANGED).apply {
            setPayload("enabled", enabled)
    }.create()

    HttpChannel.postEvents(event) { success, reason, response ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("enabled", enabled)
            }
        } .build()

        action.callback?.onResult(result.toString())
    }
}