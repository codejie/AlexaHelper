package jie.android.alexahelper.smartwatchsdk

import jie.android.alexahelper.smartwatchsdk.config.configDevice
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonArray
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonObject
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*

internal class ProductInfo {
    var id: String? = null
    var clientId: String? = null
    var serialNumber: String? = null
    var name: String? = null
    var friendlyName: String? = null
    var description: String? = null
    var manufacturer: String? = null
    var model: String? = null
    var firmware: String? = null
    var software: String? = null

    init {
        loadProductInfo(this)
    }
}

internal class EndpointInfo {
    private val endpoints: MutableMap<String, JsonObject> = mutableMapOf()

//    init {
//        loadEndpointInfo(this.endpoints)
//    }

    fun load() = loadEndpointInfo(this.endpoints)
    fun makeList(): JsonArray = endpointsToArray(this.endpoints)
}

class StateInfo {
    class VolumeState {
        var volume: Int? = null
        var muted: Int? = null
    }

    class AlertsState {
        data class Alert (val token: String, val type: String, val scheduledTime: String) {
        }
        val allAlerts: ArrayList<Alert> = arrayListOf<Alert>()
        val activeAlerts: ArrayList<Alert> = arrayListOf<Alert>()
    }

    var volumeState: VolumeState? = null
    var alertsState: AlertsState? = null

    fun makeContext(): JsonObject = stateToContext(this)
}

internal object DeviceInfo {

//    var inited: Boolean = false

    var isLogin: Boolean = false

//    val endpoints: MutableMap<String, JsonObject> = mutableMapOf()
    val productInfo: ProductInfo = ProductInfo()
    val endpointInfo: EndpointInfo = EndpointInfo()

    val stateInfo: StateInfo = StateInfo()



    init {
//        inited = initInfo()
    }

//    object Product {
//        var id: String? = null
//        var clientId: String? = null
//        var serialNumber: String? = null
//        var name: String? = null
//        var friendlyName: String? = null
//        var description: String? = null
//    }
//
//    object Manufacturer {
//        var name: String? = null
//        var model: String? = null
//        var firmware: String? = null
//        var software: String? = null
//    }

//    object State {
//        var volumeState: JsonObject? = null
//        var allAlerts: JsonArray? = null
//    }

//    fun initEndpointInfo(): Boolean = loadEndpointInfo()
//    fun makeContext(): JsonObject = stateToContext()
}


private fun loadProductInfo(productInfo: ProductInfo): Boolean {
    return try {
        val config = Json.parseToJsonElement(configDevice).jsonObject

        val confProduct = config.getJsonObject("product")!!
        productInfo.id = confProduct.getString("id")
        productInfo.clientId = confProduct.getString("clientId")

        productInfo.manufacturer = confProduct.getString("manufacturer")
        productInfo.model = confProduct.getString("model")

        true
    } catch (e: Exception) {
        Logger.e("init Device Info failed - ${e.message}")
        false
    }
}
//
//private fun initInfo(): Boolean {
//    try {
//        val config = Json.parseToJsonElement(configDevice).jsonObject
//
//        val confProduct = config.getJsonObject("product")!!
//        DeviceInfo.Product.id = confProduct.getString("id")
//        DeviceInfo.Product.clientId = confProduct.getString("clientId")
//
//        val confManufacturer = config.getJsonObject("manufacturer")!!
//        DeviceInfo.Manufacturer.name = confManufacturer.getString("name")
//        DeviceInfo.Manufacturer.model = confManufacturer.getString("model")
//
//        return true
//    } catch (e: Exception) {
//        Logger.e("init Device Info failed - ${e.message}")
//        return false
//    }
//}

private fun loadEndpointInfo(endpoints: MutableMap<String, JsonObject>): Boolean {
    try {
        val config = Json.parseToJsonElement(configDevice).jsonObject // as JsonObject
//        val endpoints =

        val id = "${DeviceInfo.productInfo.clientId}::${DeviceInfo.productInfo.id}::${DeviceInfo.productInfo.serialNumber}"
        val data = buildJsonObject {
            put("endpointId", id)
            put("manufacturerName", DeviceInfo.productInfo.name)
            put("friendlyName", DeviceInfo.productInfo.friendlyName)
            put("description", DeviceInfo.productInfo.description)

            put("additionalAttributes", buildJsonObject {
                put("manufacturer", DeviceInfo.productInfo.manufacturer)
                put("model", DeviceInfo.productInfo.model)
                put("serialNumber", DeviceInfo.productInfo.serialNumber)
                put("firmwareVersion", DeviceInfo.productInfo.firmware)
                put("softwareVersion", DeviceInfo.productInfo.software)
            })

            put("registration", buildJsonObject {
                put("productId", DeviceInfo.productInfo.id)
                put("deviceSerialNumber", DeviceInfo.productInfo.serialNumber)
            })

            put("displayCategories", config.getJsonArray("displayCategories")!!)
            put("connections", config.getJsonArray("connections")!!)

            put("capabilities", config.getJsonArray("capabilities")!!)
        }

        endpoints[id] = data

        // load things info
        return loadThingInfo(endpoints)
    } catch (e: Exception) {
        Logger.e("init Endpoint Info failed - ${e.message}")
        return false
    }
}

private fun loadThingInfo(endpoints: MutableMap<String, JsonObject>): Boolean {
    return true
}

private fun endpointsToArray(endpoints: MutableMap<String, JsonObject>): JsonArray {
    return buildJsonArray {
        endpoints.values.forEach { it ->
            add(it)
        }
    }
}

private fun stateToContext(stateInfo: StateInfo): JsonObject {
    val properties = buildJsonArray {
        stateInfo.volumeState?.let {
            add(buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", "Speaker")
                    put("name", "VolumeState")
                })
                put("payload", buildJsonObject {
                    put("volume", it.volume)
                    put("muted", it.muted == 1)
                })
            })
        }

        stateInfo.alertsState?.let {
            add(buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", "Alerts")
                    put("name", "AlertsState")
                })
                put("payload", buildJsonObject {
                    put("allAlerts", buildJsonArray {
                        stateInfo.alertsState!!.allAlerts.forEach { it ->
                            add(buildJsonObject {
                                put("token", it.token)
                                put("type", it.type)
                                put("scheduledTime", it.scheduledTime)
                            })
                        }
                    })
                    put("activeAlerts", buildJsonArray {
                        stateInfo.alertsState!!.activeAlerts.forEach { it ->
                            add(buildJsonObject {
                                put("token", it.token)
                                put("type", it.type)
                                put("scheduledTime", it.scheduledTime)
                            })
                        }
                    })
                })
            })
        }
    }
    val ret = buildJsonObject {
        put("properties", properties)
    }

    return ret
}