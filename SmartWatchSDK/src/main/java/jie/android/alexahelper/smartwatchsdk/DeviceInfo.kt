package jie.android.alexahelper.smartwatchsdk

import jie.android.alexahelper.smartwatchsdk.config.configDevice
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonArray
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonObject
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*

internal object DeviceInfo {

    var inited: Boolean = false
    val endpoints: MutableMap<String, JsonObject> = mutableMapOf()

    init {
        inited = initInfo()
    }

    object Product {
        var id: String? = null
        var clientId: String? = null
        var serialNumber: String? = null
        var name: String? = null
        var friendlyName: String? = null
        var description: String? = null
    }

    object Manufacturer {
        var name: String? = null
        var model: String? = null
        var firmware: String? = null
        var software: String? = null
    }

    object State {
        var volumeState: JsonObject? = null
        var allAlerts: JsonArray? = null
    }

    fun initEndpointInfo(): Boolean = loadEndpointInfo()
    fun makeContext(): JsonObject = stateToContext()
}

private fun initInfo(): Boolean {
    try {
        val config = Json.parseToJsonElement(configDevice).jsonObject

        val confProduct = config.getJsonObject("product")!!
        DeviceInfo.Product.id = confProduct.getString("id")
        DeviceInfo.Product.clientId = confProduct.getString("clientId")

        val confManufacturer = config.getJsonObject("manufacturer")!!
        DeviceInfo.Manufacturer.name = confManufacturer.getString("name")
        DeviceInfo.Manufacturer.model = confManufacturer.getString("model")

        return true
    } catch (e: Exception) {
        Logger.e("init Device Info failed - ${e.message}")
        return false
    }
}

private fun loadEndpointInfo(): Boolean {
    try {
        val config = Json.parseToJsonElement(configDevice).jsonObject // as JsonObject
//        val endpoints =

        val id = "${DeviceInfo.Product.clientId}::${DeviceInfo.Product.id}::${DeviceInfo.Product.serialNumber}"
        val data = buildJsonObject {
            put("endpointId", id)
            put("manufacturerName", DeviceInfo.Product.name)
            put("friendlyName", DeviceInfo.Product.friendlyName)
            put("description", DeviceInfo.Product.description)

            put("additionalAttributes", buildJsonObject {
                put("manufacturer", DeviceInfo.Manufacturer.name)
                put("model", DeviceInfo.Manufacturer.model)
                put("serialNumber", DeviceInfo.Product.serialNumber)
                put("firmwareVersion", DeviceInfo.Manufacturer.firmware)
                put("softwareVersion", DeviceInfo.Manufacturer.software)
            })

            put("registration", buildJsonObject {
                put("productId", DeviceInfo.Product.id)
                put("deviceSerialNumber", DeviceInfo.Product.serialNumber)
            })

            put("displayCategories", config.getJsonArray("displayCategories")!!)
            put("connections", config.getJsonArray("connections")!!)

            put("capabilities", config.getJsonArray("capabilities")!!)
        }

        DeviceInfo.endpoints[id] = data

        // load things info
        return loadThingInfo()
    } catch (e: Exception) {
        Logger.e("init Endpoint Info failed - ${e.message}")
        return false
    }
}

private fun loadThingInfo(): Boolean {
    return true
}

private fun stateToContext(): JsonObject {
    val properties = buildJsonArray {
        DeviceInfo.State.volumeState?.let {
            addJsonObject { buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", "Speaker")
                    put("name", "VolumeState")
                })
                put("payload", it)
            } }
        }

        DeviceInfo.State.allAlerts?.let {
            addJsonObject { buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", "Alerts")
                    put("name", "AlertsState")
                })
                put("payload", buildJsonObject {
                    put("allAlerts", it)
                })
            } }
        }
    }
    val ret = buildJsonObject {
        put("properties", properties)
    }

    return ret
}