package jie.android.alexahelper.smartwatchsdk

import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import kotlinx.serialization.json.*
import java.util.*

internal object EndpointInfo {
    val endpoints: MutableMap<String, JsonObject> = mutableMapOf()

    init {
        // self
        loadSelf(this)
    }

    fun add(id: String, json: JsonObject): JsonObject? = endpoints.put(id, json)
    fun add(id: String, json: String): JsonObject? = add(id, Json.parseToJsonElement(json) as JsonObject)
}

private fun loadSelf(endpointInfo: EndpointInfo) {
    val id = "${DeviceInfo.Product.clientId}::${DeviceInfo.Product.id}::${DeviceInfo.Product.serialNumber}"
    val data = buildJsonObject {
        put("endpointId", id)
        put("manufacturerName", "TouchTech")
        put("friendlyName", "TouchAce")
        put("displayCategories", buildJsonArray {
            add("MUSIC_SYSTEM")
            add("SPEAKER")
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

        put("capabilities", buildJsonArray {
            add(Capability.makeAlexa())
            add(Capability.makeAlexaDoNotDisturb())
            add(Capability.makeNotifications())
            add(Capability.makeAlerts())
            add(Capability.makeSystem())
            add(Capability.makeSpeedRecognizer())
            add(Capability.makeAlexaApiGateway())
            add(Capability.makeSpeechSynthesizer())
            add(Capability.makeAudioPlayer())
            add(Capability.makeTemplateRuntime())
        })
    }

    endpointInfo.add(id, data)
    endpointInfo.add(thingSpot.getString("endpointId")!!, thingSpot)
}

object Capability {
    private val SupportedLocales = listOf(
        "de-DE",
        "en-AU",
        "en-CA",
        "en-GB",
        "en-IN",
        "en-US",
        "es-ES",
        "es-MX",
        "es-US",
        "fr-CA",
        "fr-FR",
        "hi-IN",
        "it-IT",
        "ja-JP",
        "pt-BR",
        "ar-SA"
    )
    private val SupportedLocaleCombinations = listOf(
        listOf("en-US", "es-US"),
        listOf("es-US", "en-US"),
        listOf("en-US", "fr-FR"),
        listOf("fr-FR", "en-US"),
        listOf("en-US", "de-DE"),
        listOf("de-DE", "en-US"),
        listOf("en-US", "ja-JP"),
        listOf("ja-JP", "en-US"),
        listOf("en-US", "it-IT"),
        listOf("it-IT", "en-US"),
        listOf("en-US", "es-ES"),
        listOf("es-ES", "en-US"),
        listOf("en-IN", "hi-IN"),
        listOf("hi-IN", "en-IN"),
        listOf("fr-CA", "en-CA"),
        listOf("en-CA", "fr-CA")
    )


    fun makeAlexa(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Alexa")
            put("version", "3")
        }
    }

    fun makeAlexaApiGateway(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Alexa.ApiGateway")
            put("version", "1.0")
        }
    }

    fun makeNotifications(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Notifications")
            put("version", "1.0")
        }
    }

    fun makeAlexaDoNotDisturb(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Alexa.DoNotDisturb")
            put("version", "1.0")
        }
    }

    fun makeAlerts(): JsonObject {
        val configurations = buildJsonObject {
            put("maximumAlerts", buildJsonObject {
                put("overall", 10)
                put("alarms", 5)
                put("timers", 5)
            })
        }

        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Alerts")
            put("version", "1.5")
            put("configurations", configurations)
        }
    }

    fun makeSystem(): JsonObject {
        val configurations = buildJsonObject {
            put("locales", buildJsonArray {
                for (item in SupportedLocales) {
                    add(item)
                }
            })
            put("localeCombinations", buildJsonArray {
                for (item in SupportedLocaleCombinations) {
                    add(buildJsonArray { for (i in item) add(i) })
                }
            })
        }
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "System")
            put("version", "2.1")
            put("configurations", configurations)
        }
    }

    fun makeSpeedRecognizer(): JsonObject {
        val configurations = buildJsonObject {
            put("wakeWords", buildJsonArray {
                add(buildJsonObject {
                    put("scopes", buildJsonArray { add("DEFAULT") })
                    put("values", buildJsonArray { add("ALEXA") })
                })
            })
        }

        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "SpeedRecognizer")
            put("version", "2.3")
            put("configurations", configurations)
        }
    }

    fun makeSpeechSynthesizer(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "SpeechSynthesizer")
            put("version", "1.3")
        }
    }

    fun makeAudioPlayer(): JsonObject {
        val configurations = buildJsonObject {
            put("fingerprint", buildJsonObject {
                put("package", "android.media.MediaPlayer")
                put("buildType", "DEBUG")
                put("versionNumber", "1.0")
            })
        }

        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "AudioPlayer")
            put("version", "1.4")
            put("configurations", configurations)
        }
    }

    fun makeTemplateRuntime(): JsonObject {
        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "TemplateRuntime")
            put("version", "1.2")
        }
    }

    fun makeAlexaPowerController(): JsonObject {
        val properties = buildJsonObject {
            put("supported", buildJsonArray { add(buildJsonObject { put("name", "powerState") }) })
            put("proactivelyReported", false)
            put("retrievable", false)
        }

        return buildJsonObject {
            put("type", "AlexaInterface")
            put("interface", "Alexa.PowerController")
            put("version", "3")
            put("properties", properties)
        }
    }
}

private val thingSpot = buildJsonObject {
    put("endpointId", "${DeviceInfo.Product.clientId}::${DeviceInfo.Product.id}::${DeviceInfo.Product.serialNumber}-Light-Spot")
    put("manufacturerName", "TouchTech")
    put("friendlyName", "Spot")
    put("displayCategories", buildJsonArray {
        add("LIGHT")
    })
    put("description", "TouchAlexa LightSpot")

    put("additionalAttributes", buildJsonObject {
        put("manufacturer", DeviceInfo.Manufacturer.name)
        put("model", "Light-Sport-1")
        put("serialNumber", "1")
        put("firmwareVersion", "1.0")
        put("softwareVersion", "20221020")
        put("customIdentifier", "spot-01")
    })

    put("capabilities", buildJsonArray {
        add(Capability.makeAlexa())
        add(Capability.makeAlexaPowerController())
    })
}
