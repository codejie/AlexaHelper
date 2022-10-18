package jie.android.alexahelper.smartwatchsdk

import kotlinx.serialization.json.*

internal object EndpointInfo {
    val endpoints: MutableMap<String, JsonObject> = mutableMapOf()

    init {
        // self
        val id = "${DeviceInfo.Product.clientId}::${DeviceInfo.Product.id}::${DeviceInfo.Product.serialNumber}"
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
                put("productId", DeviceInfo.Product.id)
                put("deviceSerialNumber", DeviceInfo.Product.serialNumber)
            })

            put("connections", buildJsonArray {
                add(buildJsonObject {
                    put("type", "TCP_IP")
                    put("value", "127.0.0.1")
                })
            })

            put("additionalAttributes", buildJsonObject {
                put("manufacturer", DeviceInfo.Manufacturer.name)
                put("model", DeviceInfo.Manufacturer.model)
                put("serialNumber", DeviceInfo.Product.serialNumber)
                put("firmwareVersion", DeviceInfo.Manufacturer.firmware)
                put("softwareVersion", DeviceInfo.Manufacturer.software)
            })

            put("capabilities", buildJsonArray {  })
        })
    }

    fun add(id: String, json: JsonObject): JsonObject? = endpoints.put(id, json)
    fun add(id: String, json: String): JsonObject? = add(id, Json.parseToJsonElement(json) as JsonObject)
}