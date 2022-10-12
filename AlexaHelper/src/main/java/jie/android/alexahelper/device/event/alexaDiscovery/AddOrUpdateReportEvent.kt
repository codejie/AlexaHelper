package jie.android.alexahelper.device.event.alexaDiscovery

import jie.android.alexahelper.alexa.API
import jie.android.alexahelper.alexa.Event
import jie.android.alexahelper.alexa.makeMessageId
import jie.android.alexahelper.device.EndpointInfo
import jie.android.alexahelper.device.RuntimeInfo
import kotlinx.serialization.json.*

class AddOrUpdateReportEvent : Event(API.NS_ALEXA_DISCOVERY, API.NAME_ADD_OR_UPDATE_REPORT) {
    init {
        setHeader("payloadVersion", "3")
        setHeader("eventCorrelationToken", makeMessageId())

        val scope: JsonObject = buildJsonObject {
            put("type","BearerToken")
            put("token", RuntimeInfo.accessToken)
        }
        setPayload("scope", scope)
        setPayload("endpoints", buildJsonArray {
            for (element in EndpointInfo.endpoints.values) {
                add(element)
            }
        })
    }
}