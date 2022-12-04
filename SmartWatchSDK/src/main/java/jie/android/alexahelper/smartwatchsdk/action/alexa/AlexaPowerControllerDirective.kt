package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint.makeDate
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onAlexaPowerControllerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_TURN_ON -> onTurnOn(sdk, directive, parts)
        AlexaConst.NAME_TURN_OFF -> onTurnOff(sdk, directive, parts)
        else -> Logger.w("unsupported AlexaPowerController - $directive")
    }
}

private fun onTurnOff(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val token = directive.header.getString("correlationToken")!!
    val id = directive.source.getJsonObject("directive")!!.getJsonObject("endpoint")!!.getString("endpointId")!!
    val endpointId = DeviceInfo.parseEndpointId(id)

    val action = ActionWrapper(SDKConst.ACTION_ENDPOINT_STATE_UPDATED).apply {
        val payload = buildJsonObject {
            put("token", token)
            put("endpointId", endpointId)
            put("items", buildJsonArray {
                add(buildJsonObject {
                    put("name", "powerState")
                    put("value", "OFF")
                })
            })
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
            postResponse(sdk, token, endpointId, "OFF")
        } else {
            postErrorResponse(sdk, token, endpointId, result.code, result.message)
        }
    }
}

private fun onTurnOn(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val token = directive.header.getString("correlationToken")!!
    val id = directive.source.getJsonObject("directive")!!.getJsonObject("endpoint")!!.getString("endpointId")!!
    val endpointId = DeviceInfo.parseEndpointId(id)

    val action = ActionWrapper(SDKConst.ACTION_ENDPOINT_STATE_UPDATED).apply {
        val payload = buildJsonObject {
            put("token", token)
            put("endpointId", endpointId)
            put("items", buildJsonArray {
                add(buildJsonObject {
                    put("name", "powerState")
                    put("value", "ON")
                })
            })
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
            postResponse(sdk, token, id, "ON")
        } else {
            postErrorResponse(sdk, token, id, result.code, result.message)
        }
    }
}

private fun postResponse(sdk: SmartWatchSDK, token: String, endpointId:String, value:String){
    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_RESPONSE).apply {
        addHeader("payloadVersion", "3")
        addHeader("correlationToken", token)
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpointId))
        })
        setContext(buildJsonObject {
            put("properties", buildJsonArray {
                add(buildJsonObject {
                    put("namespace", "Alexa.PowerController")
                    put("name", "powerState")
                    put("value", value)
                    put("timeOfSample", makeDate())
                    put("uncertaintyInMilliseconds", 0)
                })
            })
        })
    }.create()

    sdk.httpChannel.postEvent(event, null)
}

private fun postErrorResponse(sdk: SmartWatchSDK, token: String, endpointId: String, code: Int, message: String?) {

    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_ERROR_RESPONSE).apply {
        addHeader("payloadVersion", "3")
        addHeader("correlationToken", token)
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpointId))
        })
        addPayload("type", SDKConst.codeToAlexaEndpointErrorType(code))
        message?.let {
            addPayload("message", it)
        }
    }.create()

    sdk.httpChannel.postEvent(event, null)
}
