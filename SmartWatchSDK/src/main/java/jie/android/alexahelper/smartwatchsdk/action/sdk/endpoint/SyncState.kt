package jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.Endpoint
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.*

internal fun syncStateAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    val endpointId = action.getPayload()!!.getString("endpointId")!!
    val changed = action.getPayload()!!.getJsonArray("changed")
    val unchanged = action.getPayload()!!.getJsonArray("unchanged")

    val endpoint = DeviceInfo.endpoints[endpointId]
    if (endpoint != null) {
        postChangeReport(sdk, action, endpoint, changed, unchanged, callback)
    } else {
        resultCallback(action, null, SDKConst.RESULT_CODE_ENDPOINT_NOT_FOUND, SDKConst.RESULT_MESSAGE_ENDPOINT_NOT_FOUND, callback)
    }
}

private fun resultCallback(action: ActionWrapper, endpoint: Endpoint?, code: Int, message: String?, callback: ActionResultCallback) {
    val result = ResultWrapper(action.name, code, message).apply {
        endpoint?.let {
            setPayload(buildJsonObject {
                put("endpointId", endpoint.id)
            })
        }
    }
    callback(result)
}

private fun postChangeReport(sdk: SmartWatchSDK, action: ActionWrapper, endpoint: Endpoint, changed: JsonArray?, unchanged: JsonArray?, callback: ActionResultCallback) {
    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_CHANGE_REPORT).apply {
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpoint.id))
        })
        addPayload("change", buildJsonObject {
            put("cause", buildJsonObject { put("type", "PHYSICAL_INTERACTION") })
        })
        changed?.let {
            addPayload("properties", buildJsonArray {
                for (index in 0 until  changed.size) {
                    makeProperty(endpoint, changed[index].jsonObject)?.let { add(it) }
                }
            })
        }

    }
}

private fun makeProperty(endpoint: Endpoint, item: JsonObject): JsonObject? {
    val name = item.getString("name")
    val property = endpoint.properties?.get(name)
    property?.let {
        return buildJsonObject {

        }
    }
    return null
}
