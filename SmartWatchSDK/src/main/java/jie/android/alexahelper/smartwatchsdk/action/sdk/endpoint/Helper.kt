package jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.Endpoint
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getInt
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

internal fun makeDate(): String {
    val current = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
    return current.format(formatter)
}

internal fun makeProperty(endpointId: String, item: JsonObject): JsonObject? {
    val instance = item.getString("instance", false)
    val name = item.getString("name")!!
    val property = DeviceInfo.getEndpointProperty(endpointId, instance, name)
    property?.let {
        return buildJsonObject {
            put("namespace", property.intf)
            property.instance?.let { it1 -> put("instance", it1) }
            put("name", name)
            item["value"]?.let { it1 -> put("value", it1) }
            put("timeOfSample", item.getString("timeOfSample", false)?: makeDate())
            put("uncertaintyInMilliseconds", item.getInt("intervalInMilliseconds", false)?:0)
        }
    }
    return null
}