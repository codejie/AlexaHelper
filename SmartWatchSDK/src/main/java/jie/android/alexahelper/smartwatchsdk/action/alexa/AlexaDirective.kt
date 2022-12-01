package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint.makeProperty
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.put

fun onAlexaDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_EVENT_PROCESSED -> onEventProcessed(sdk, directive, parts)
        AlexaConst.NAME_REPORT_STATE -> onReportState(sdk, directive, parts)
        else -> Logger.w("unsupported Alexa - ${directive.toString()}")
    }
}

private fun onEventProcessed(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}

private fun onReportState(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val token = directive.header!!.getString("correlationToken")!!
    val endpointId = directive.source.getJsonObject("directive")!!.getJsonObject("endpoint")!!.getString("endpointId")!!
//    val instance = directive.header.getString("instance", false)

    val id = DeviceInfo.parseEndpointId(endpointId)

//    val endpoint = DeviceInfo.endpoints[id]
//    if (endpoint == null) {
//        Logger.w("Can't find endpoint - $endpointId")
//        return
//    }

    val action = ActionWrapper(SDKConst.ACTION_ENDPOINT_STATE_EXPECTED).apply {
        setPayload(buildJsonObject {
            put("token", token)
            put("endpointId", id)
        })
    }

    sdk.toAction(action) { result ->
        val event = EventBuilder(AlexaConst.NS_ALEXA, AlexaConst.NAME_STATE_REPORT).apply {
            addHeader("payloadVersion", "3")
            addHeader("correlationToken", token)
            setEndpoint(buildJsonObject {
                put("endpointId", endpointId)
            })
            result.getPayload()!!.getJsonArray("items")?.let {
                setContext(buildJsonObject {
                    put("properties", buildJsonArray {
                        for (index in 0 until it.size) {
                            makeProperty(id, it[index].jsonObject)?.let { it1 -> add(it1) }
                        }
                    })
                })
            }
        }.create()

        sdk.httpChannel.postEvent(event, null)
    }
}
