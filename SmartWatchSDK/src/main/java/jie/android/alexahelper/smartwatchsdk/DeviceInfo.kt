package jie.android.alexahelper.smartwatchsdk

import jie.android.alexahelper.smartwatchsdk.config.configDevice
import jie.android.alexahelper.smartwatchsdk.config.configEndpoints
import jie.android.alexahelper.smartwatchsdk.config.defineEndpoints
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
}
internal data class Endpoint constructor(val id: String, val define: String) {
    var types: ArrayList<String>? = null

    var serialNumber: String? = null
    var friendlyName: String? = null
    var description: String? = null
    var manufacturer: String? = null
    var model: String? = null
    var firmware: String? = null
    var software: String? = null
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

    val productInfo: ProductInfo = ProductInfo()
    val stateInfo: StateInfo = StateInfo()

    val endpoints: MutableMap<String, Endpoint> = mutableMapOf()

    fun parseDeviceSetting(payload: JsonObject): Boolean = loadDeviceInfo(this, payload)
    fun makeEndpointList(): JsonArray? = productToEndpointList(this)

    fun parseEndpointId(id: String): String {
        val index = id.lastIndexOf('-')
        return if (index != -1) {
            id.substring(index + 1)
        } else {
            id
        }
    }

    fun makeEndpointId(id: String): String {
        return "${productInfo.clientId}::${productInfo.id}::${productInfo.serialNumber}-$id"
    }
}

private fun loadDeviceInfo(deviceInfo: DeviceInfo, payload: JsonObject): Boolean {
    return try {
        loadProductInfo(deviceInfo.productInfo, payload)
        loadEndpointInfo(deviceInfo.productInfo, deviceInfo.endpoints, payload)
        true
    } catch (e: Exception) {
        Logger.e("init Device Info failed - ${e.message}")
        false
    }
}

private fun loadProductInfo(productInfo: ProductInfo, payload: JsonObject) {
    val config = Json.parseToJsonElement(configDevice).jsonObject

    val confProduct = config.getJsonObject("product")!!
    productInfo.id = confProduct.getString("id")
    productInfo.clientId = confProduct.getString("clientId")
    productInfo.name = confProduct.getString("name")
    productInfo.manufacturer = confProduct.getString("manufacturer")
    productInfo.model = confProduct.getString("model")

    val payloadProduct = payload.getJsonObject("product")!!
    productInfo.serialNumber = payloadProduct.getString("serialNumber")
    productInfo.friendlyName = payloadProduct.getString("friendlyName", false)
    productInfo.description = payloadProduct.getString("description", false)
}

private fun loadEndpointInfo(productInfo: ProductInfo, endpoints: MutableMap<String, Endpoint>, payload: JsonObject) {
    val payloadEndpoint = payload.getJsonArray("endpoints", false)

    payloadEndpoint?.let {
        for (index in 0 until payloadEndpoint.size) {
            val item = payloadEndpoint[index] as JsonObject
            val id = item.getString("id")!!
            val confEndpoint = indexOfEndpointConfig(id)
            if (confEndpoint != null){
                val define = confEndpoint.getString("define")!!
                val defEndpoint = indexOfEndpointDefine(define)
                if (defEndpoint != null) {
                    val endpoint = Endpoint(id, define)
                    endpoint.types = arrayListOf()
                    defEndpoint.getJsonArray("types")!!.forEach { it ->
                        endpoint.types!!.add(it.toString())
                    }

                    endpoint.serialNumber = item.getString("serialNumber")
                    endpoint.friendlyName = item.getString("friendlyName")
                    endpoint.description = item.getString("description")

                    endpoint.model = confEndpoint.getString("model")
                    endpoint.manufacturer = confEndpoint.getString("manufacturer")

                    val endpointId =
                        "${productInfo.clientId}::${productInfo.id}::${productInfo.serialNumber}-$id"
                    endpoints[endpointId] = endpoint
                } else {
                    Logger.w("can't find endpoint define - $define")
                }
            } else {
                Logger.w("can't find endpoint - $id")
            }
        }
    }
}

fun indexOfEndpointDefine(define: String): JsonObject? {
    val config = Json.parseToJsonElement(defineEndpoints).jsonArray
    val index = config.indexOfFirst { item -> item.jsonObject.getString("id") == define }
    return (if (index != -1) config[index].jsonObject else null)
}

private fun indexOfEndpointConfig(id: String): JsonObject? {
    val config = Json.parseToJsonElement(configEndpoints).jsonArray
    val index = config.indexOfFirst { item -> item.jsonObject.getString("id") == id }
    return (if (index != -1) config[index].jsonObject else null)
}

private fun productToEndpointList(deviceInfo: DeviceInfo): JsonArray? {
    return try {
        buildJsonArray {
            // product
            val productInfo = deviceInfo.productInfo
            val config = Json.parseToJsonElement(configDevice).jsonObject
            val product = buildJsonObject {
                val id = "${productInfo.clientId}::${productInfo.id}::${productInfo.serialNumber}"
                put("endpointId", id)
                put("manufacturerName", productInfo.name)
                put("friendlyName", productInfo.friendlyName)
                put("description", productInfo.description)

                put("additionalAttributes", buildJsonObject {
                    put("manufacturer", productInfo.manufacturer)
                    put("model", productInfo.model)
                    put("serialNumber", productInfo.serialNumber)
                    put("firmwareVersion", productInfo.firmware)
                    put("softwareVersion", productInfo.software)
                })

                put("registration", buildJsonObject {
                    put("productId", productInfo.id)
                    put("deviceSerialNumber", productInfo.serialNumber)
                })

                put("displayCategories", config.getJsonArray("displayCategories")!!)
                put("connections", config.getJsonArray("connections")!!)
                put("capabilities", config.getJsonArray("capabilities")!!)
            }
            this.add(product)

            // endpoints
            val endpoints = deviceInfo.endpoints
            endpoints.forEach { (k, v) ->
                val confEndpoint = indexOfEndpointDefine(v.define)!!

                val data = buildJsonObject {
                    put("endpointId", k)
                    put("manufacturerName", v.manufacturer)
                    put("friendlyName", v.friendlyName)
                    put("description", v.description)

                    put("additionalAttributes", buildJsonObject {
                        put("manufacturer", v.manufacturer)
                        put("model", v.model)
                        put("serialNumber", v.serialNumber)
                        put("firmwareVersion", v.firmware)
                        put("softwareVersion", v.software)
                    })

                    put("displayCategories", confEndpoint.getJsonArray("displayCategories")!!)
                    put("capabilities", confEndpoint.getJsonArray("capabilities")!!)
                }
                this.add(data)
            }
        }
    } catch (e: Exception) {
        Logger.w("make endpoint list - ${e.message}")
        null
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