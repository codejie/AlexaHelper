package jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.makeDate
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

object PowerController {
    fun syncState(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
        val endpointId = action.getPayload()!!.getString("endpointId")!!
        val value = action.getPayload()!!.getString("value")!!

        postChangeReport(sdk, action, endpointId, value, callback)
    }
}

private fun postChangeReport(sdk: SmartWatchSDK, action: ActionWrapper, endpointId: String, value: String, callback: ActionResultCallback) {
    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_CHANGE_REPORT).apply {
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpointId))
        })
        addPayload("change", buildJsonObject {
            put("cause", buildJsonObject { put("type", "PHYSICAL_INTERACTION") })
        })

        val name = action.getPayload()!!.getString("name", false)?: "powerState"
        val timeOfSample = action.getPayload()!!.getString("timeOfSample", false)?: makeDate()
        val intervalInMilliseconds = action.getPayload()!!.getInt("intervalInMilliseconds", false)?: 0
        setContext(buildJsonObject {
            put("properties", buildJsonArray {
                add(buildJsonObject {
                    put("namespace", "Alexa.PowerController")
                    put("name", name)
                    put("value", value)
                    put("timeOfSample", timeOfSample)
                    put("uncertaintyInMilliseconds", intervalInMilliseconds)
                })
            })
        })
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        )
        callback(result)
    }
}