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

internal fun onAlexaModeControllerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_MODE -> onSetMode(sdk, directive, parts)
        AlexaConst.NAME_ADJUST_MODE -> onAdjustMode(sdk, directive, parts)
        else -> Logger.w("unsupported AlexaModeController - $directive")
    }
}

fun onAdjustMode(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val token = directive.header.getString("correlationToken")!!
    val value = directive.payload!!.getInt("modeDelta")!!
    val id = directive.source.getJsonObject("directive")!!.getJsonObject("endpoint")!!.getString("endpointId")!!
    val endpointId = DeviceInfo.parseEndpointId(id)

    val action = ActionWrapper(SDKConst.ACTION_ENDPOINT_STATE_UPDATED).apply {
        val payload = buildJsonObject {
            put("token", token)
            put("endpointId", endpointId)
            put("items", buildJsonArray {
                add(buildJsonObject {
                    put("name", "modeDelta")
                    put("value", value)
                })
            })
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
            postResponse(sdk, token, endpointId, "mode", value)
        } else {
            postErrorResponse(sdk, token, endpointId, result.code, result.message)
        }
    }
}

private fun onSetMode(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val token = directive.header.getString("correlationToken")!!
    val value = directive.payload!!.getString("mode")!!
    val id = directive.source.getJsonObject("directive")!!.getJsonObject("endpoint")!!.getString("endpointId")!!
    val endpointId = DeviceInfo.parseEndpointId(id)

    val action = ActionWrapper(SDKConst.ACTION_ENDPOINT_STATE_UPDATED).apply {
        val payload = buildJsonObject {
            put("token", token)
            put("endpointId", endpointId)
            put("items", buildJsonArray {
                add(buildJsonObject {
                    put("name", "mode")
                    put("value", value)
                })
            })
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        if (result.code == SDKConst.RESULT_CODE_SUCCESS) {
            postResponse(sdk, token, endpointId, "mode", value)
        } else {
            postErrorResponse(sdk, token, endpointId, result.code, result.message)
        }
    }
}

private fun postResponse(sdk: SmartWatchSDK, token: String, endpointId:String, name: String, value: Int) {
    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_RESPONSE).apply {
        addHeader("payloadVersion", "3")
        addHeader("correlationToken", token)
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpointId))
        })
        setContext(buildJsonObject {
            put("properties", buildJsonArray {
                add(buildJsonObject {
                    put("namespace", "Alexa.ModeController")
                    put("name", name)
                    put("value", value)
                    put("timeOfSample", makeDate())
                    put("uncertaintyInMilliseconds", 0)
                })
            })
        })
    }.create()

    sdk.httpChannel.postEvent(event, null)
}

private fun postResponse(sdk: SmartWatchSDK, token: String, endpointId:String, name: String, value:String){
    val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_RESPONSE).apply {
        addHeader("payloadVersion", "3")
        addHeader("correlationToken", token)
        setEndpoint(buildJsonObject {
            put("endpointId", DeviceInfo.makeEndpointId(endpointId))
        })
        setContext(buildJsonObject {
            put("properties", buildJsonArray {
                add(buildJsonObject {
                    put("namespace", "Alexa.ModeController")
                    put("name", name)
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