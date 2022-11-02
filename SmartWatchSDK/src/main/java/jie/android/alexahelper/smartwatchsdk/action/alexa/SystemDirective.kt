package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onSystemDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_TIME_ZONE -> onSetTimeZone(sdk, directive, parts)
        AlexaConst.NAME_SET_LOCALES -> onSetLocales(sdk, directive, parts)
        AlexaConst.NAME_REPORT_STATE -> onReportState(sdk, directive, parts)
        else  -> Logger.w("unsupported System - $directive")
    }
}

fun onReportState(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val action = ActionWrapper(SDKConst.ACTION_ALEXA_SETTING_EXPECT).build()
    sdk.onActionListener.onAction(action.toString(), null, object: OnResultCallback {
        override fun onResult(data: String, extra: Any?) {
            val result = ResultWrapper.parse(data, extra)
            val timeZone = result.getPayload()!!.getString("timeZone")!!
            val locales = result.getPayload()!!.getJsonArray("locales")!!

            stateReport(sdk, timeZone, locales)
        }
    })
}

private fun stateReport(sdk: SmartWatchSDK, timeZone: String, locales: JsonArray) {
    val event = EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_STATE_REPORT).apply {
        val states = buildJsonArray {
            buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", AlexaConst.NS_SYSTEM)
                    put("name", AlexaConst.NAME_TIME_ZONE_REPORT)
                })
                put("payload", buildJsonObject {
                    put("timeZone", timeZone)
                })
            }

            buildJsonObject {
                put("header", buildJsonObject {
                    put("namespace", AlexaConst.NS_SYSTEM)
                    put("name", AlexaConst.NAME_LOCALES_REPORT)
                })
                put("payload", buildJsonObject {
                    put("locales", locales)
                })
            }
        }

        addPayload("states", states)
    }.create()

    sdk.httpChannel.postEvent(event, null)
}

private fun onSetLocales(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val locales = directive.payload?.getJsonArray("locales")
    if (locales != null) {
        val action = ActionWrapper(SDKConst.ACTION_ALEXA_LOCALES_UPDATED).apply {
            val payload = buildJsonObject {
                put("locales", locales)
            }
            setPayload(payload)
        }.build()

        sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                try {
                    val result = ResultWrapper.parse(data, extra)
                    val ret = result.getPayload()!!.getJsonArray("locales")!!
                    localesReport(sdk, ret)
                } catch (e: Exception) {
                    Logger.w("${SDKConst.ACTION_ALEXA_LOCALES_UPDATED} Result exception - ${e.message}")
                }
            }
        })
    } else {
        Logger.w("$directive missing filed - locales")
    }
}

private fun localesReport(sdk: SmartWatchSDK, locales: JsonArray) {
    val event = EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_LOCALES_REPORT).apply {
        addPayload("locales", locales)
    }.create()

    sdk.httpChannel.postEvent(event, null)
}


private fun onSetTimeZone(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val timezone = directive.payload?.getString("timeZone", false)
    if (timezone != null) {
        val action = ActionWrapper(SDKConst.ACTION_ALEXA_TIME_ZONE_UPDATED).apply {
            val payload = buildJsonObject {
                put("timeZone", timezone)
            }
            setPayload(payload)
        }.build()

        sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
            override fun onResult(data: String, extra: Any?) {
                try {
                    val result = ResultWrapper.parse(data, extra)
                    val timeZone = result.getPayload()!!.getString("timeZone")!!
                    timeZoneReport(sdk, timeZone)
                } catch (e: Exception) {
                    Logger.w("${SDKConst.ACTION_ALEXA_TIME_ZONE_UPDATED} Result exception - ${e.message}")
                }
            }
        })

    } else {
        Logger.w("$directive missing filed - timeZone")
    }
}

private fun timeZoneReport(sdk: SmartWatchSDK, timezone: String) {
    val event = EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_TIME_ZONE_REPORT).apply {
        addPayload("timeZone", timezone)
    }.create()

    sdk.httpChannel.postEvent(event, null)
}

