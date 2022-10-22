package jie.android.alexahelper.smartwatchsdk.protocol.alexa

import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKException
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getJsonObject
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject

open class Protocol constructor(val type:String, val header: JsonObject, val payload: JsonObject? = null, val context: Any? = null) {
    val namespace: String = header.getString("namespace")!!
    val name: String = header.getString("name")!!

    override fun toString(): String {
        return "$type { namespace = $namespace | name = $name }"
    }
}

open class Event (header: JsonObject, payload: JsonObject? = null, context: Any? = null):
    Protocol("event", header, payload, context) {

    companion object {
        fun parse(json: JsonObject): Event? {
            return try {
                val event = json.getJsonObject("event")!!
                Event(
                    event.getJsonObject("header")!!,
                    event.getJsonObject("payload", false),
                    event.getJsonObject("context", false) as Any?
                )
            } catch (e: SDKException) {
                Logger.w("Event parse failed - ${e.message}")
                null
            }
        }
    }
}

open class Directive (header: JsonObject, payload: JsonObject? = null, context: Any? = null):
    Protocol("directive", header, payload, context) {

    companion object {
        fun parse(json: JsonObject): Directive? {
            return try {
                val directive = json.getJsonObject("directive")!!
                Directive(
                    directive.getJsonObject("header")!!,
                    directive.getJsonObject("payload", false),
                    directive.getJsonObject("context", false) as Any?
                )
            } catch (e: SDKException) {
                Logger.w("Directive parse failed - ${e.message}")
                null
            }
        }
    }
}
