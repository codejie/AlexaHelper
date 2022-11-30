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
        resultCallback(action, endpointId, SDKConst.RESULT_CODE_ENDPOINT_NOT_FOUND, SDKConst.RESULT_MESSAGE_ENDPOINT_NOT_FOUND, callback)
    }
}

private fun resultCallback(action: ActionWrapper, endpointId: String, code: Int, message: String?, callback: ActionResultCallback) {
    val result = ResultWrapper(action.name, code, message).apply {
        setPayload(buildJsonObject {
            put("endpointId", endpointId)
        })
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
                for (index in 0 until  it.size) {
                    makeProperty(endpoint, it[index].jsonObject)?.let { it1 -> add(it1) }
                }
            })
        }
        unchanged?.let {
            setContext(buildJsonObject {
                put("properties", buildJsonArray {
                    for (index in 0 until unchanged.size) {
                        makeProperty(endpoint, unchanged[index].jsonObject)?.let { it1 -> add(it1) }
                    }
                })
            })
        }
    }.create()

    sdk.httpChannel.postEvent(event){ success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        )
        callback(result)
    }
}

