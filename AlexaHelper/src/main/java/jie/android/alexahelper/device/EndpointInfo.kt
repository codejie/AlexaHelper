package jie.android.alexahelper.device

import jie.android.alexahelper.device.EndpointInfo.add
import kotlinx.serialization.json.*

internal object EndpointInfo {
    public val endpoints: MutableMap<String, JsonObject> = mutableMapOf()

    init {
        // self
        val id = "${ProductInfo.clientId}::${ProductInfo.id}::${ProductInfo.serialNumber}"
        add(id, buildJsonObject {
            put("endpointId", id)
            put("manufacturerName", "TouchTech")
            put("friendlyName", "TouchAce")
            put("displayCategories", buildJsonArray {
                add("SPEAKER")
                add("MUSIC_SYSTEM")
            })
            put("description", "TouchAlexa Self")

            put("registration", buildJsonObject {
                put("productId", ProductInfo.id)
                put("deviceSerialNumber", ProductInfo.serialNumber)
            })

            put("connections", buildJsonArray {
                add(buildJsonObject {
                    put("type", "TCP_IP")
                    put("value", "127.0.0.1")
                })
            })

            put("additionalAttributes", buildJsonObject {
                put("manufacturer", "manufacturer")
                put("model", "Touch-Model-1")
                put("serialNumber", ProductInfo.serialNumber)
                put("firmwareVersion", "k01")
                put("softwareVersion", "20221010")
            })

            put("capabilities", buildJsonArray {  })
        })
    }

    internal fun add(id: String, json: JsonObject): JsonObject? = endpoints.put(id, json)
    internal fun add(id: String, json: String): JsonObject? = add(id, Json.parseToJsonElement(json) as JsonObject)
}