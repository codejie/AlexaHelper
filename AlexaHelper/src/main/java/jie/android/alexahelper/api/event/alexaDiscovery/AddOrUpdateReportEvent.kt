package jie.android.alexahelper.api.event.alexaDiscovery

import jie.android.alexahelper.api.API
import jie.android.alexahelper.api.Event
import jie.android.alexahelper.api.makeMessageId
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
    }
}