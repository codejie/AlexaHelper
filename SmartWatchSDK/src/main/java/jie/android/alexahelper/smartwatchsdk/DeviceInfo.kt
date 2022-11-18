package jie.android.alexahelper.smartwatchsdk

import jie.android.alexahelper.smartwatchsdk.config.configDevice
import jie.android.alexahelper.smartwatchsdk.config.configThings
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonArray
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonObject
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*

internal class ExtendInfo constructor(val name: String) {
    var serialNumber: String? = null
    var friendlyName: String? = null
    var description: String? = null
    var manufacturer: String? = null
    var model: String? = null
    var firmware: String? = null
    var software: String? = null
}

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

    val extendInfo: MutableMap<String, ExtendInfo> = mutableMapOf()

    init {
        loadProductInfo(this)
        loadExtendInfo(this, extendInfo)
    }

    fun makeEndPoints() = productToEndpointList(this)
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

internal class Endpoint constructor(val id: String, val type: String) {
    var loaded: Boolean = false
}

internal object DeviceInfo {

//    var inited: Boolean = false

    var isLogin: Boolean = false

    val productInfo: ProductInfo = ProductInfo()
    val stateInfo: StateInfo = StateInfo()

    val endpointInfo: MutableMap<String, Endpoint> = mutableMapOf()

    init {
        loadEndpointInfo(this.productInfo, this.endpointInfo)
    }
}


private fun loadProductInfo(productInfo: ProductInfo): Boolean {
    return try {
        val config = Json.parseToJsonElement(configDevice).jsonObject

        val confProduct = config.getJsonObject("product")!!
        productInfo.id = confProduct.getString("id")
        productInfo.clientId = confProduct.getString("clientId")
        productInfo.name = confProduct.getString("name")
        productInfo.manufacturer = confProduct.getString("manufacturer")
        productInfo.model = confProduct.getString("model")

        true
    } catch (e: Exception) {
        Logger.e("init Device Info failed - ${e.message}")
        false
    }
}

private fun loadExtendInfo(
    productInfo: ProductInfo,
    extends: MutableMap<String, ExtendInfo>,
): Boolean {
    return try {
        configThings.forEach { it ->
            val config = Json.parseToJsonElement(it).jsonObject
            val confExtend = config.getJsonObject("extend")!!
            val extend = ExtendInfo(confExtend.getString("name")!!)
            extend.model = confExtend.getString("model")!!
            extend.manufacturer = confExtend.getString("manufacturer")

            extends[extend.name] = extend
        }
        true
    } catch (e: Exception) {
        Logger.e("init Extend Info failed - ${e.message}")
        false
    }
}

private fun loadEndpointInfo(
    productInfo: ProductInfo,
    endpointInfo: MutableMap<String, Endpoint>
) {
    try {
        configThings.forEach { it ->
            val config = Json.parseToJsonElement(it).jsonObject

            val confEndpoint = config.getJsonObject("endpoint")!!
            val type = confEndpoint.getString("type")!!

            val confExtend = config.getJsonObject("extend")!!
            val name = confExtend.getString("name")!!

            val eid = "${productInfo.clientId}::${productInfo.id}::${productInfo.serialNumber}-$name"

            endpointInfo[eid] = Endpoint(eid, type)
        }
    } catch (e: Exception) {
        Logger.e("init Extend Info failed - ${e.message}")
    }
}

private fun productToEndpointList(productInfo: ProductInfo): JsonArray? {
    return try {
        buildJsonArray {
            // device
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

            // extends
            configThings.forEach { it ->
                val confExtend = Json.parseToJsonElement(it).jsonObject
                val tid = confExtend.getJsonObject("extend")!!.getString("name")
                val extend = productInfo.extendInfo[tid]
                if (extend != null) {
                    val eid = "${productInfo.clientId}::${productInfo.id}::${productInfo.serialNumber}-${extend.name}"
                    val data = buildJsonObject {
                        put("endpointId", eid)
                        put("manufacturerName", extend.name)
                        put("friendlyName", extend.friendlyName)
                        put("description", extend.description)

                        put("additionalAttributes", buildJsonObject {
                            put("manufacturer", extend.manufacturer)
                            put("model", extend.model)
                            put("serialNumber", extend.serialNumber)
                            put("firmwareVersion", extend.firmware)
                            put("softwareVersion", extend.software)
                        })

                        put("displayCategories", confExtend.getJsonArray("displayCategories")!!)
                        put("capabilities", confExtend.getJsonArray("capabilities")!!)
                    }
                    this.add(data)
                } else {
                    Logger.w("Can't find extend info - $tid")
                }
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