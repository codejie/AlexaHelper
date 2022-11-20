package jie.android.alexahelper.smartwatchsdk.protocol.alexa

import jie.android.alexahelper.smartwatchsdk.Endpoint
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Utils.makeMessageId
import kotlinx.serialization.json.*

internal class Builder {
    private val content: MutableMap<String, JsonElement> = linkedMapOf()

    fun put(key: String, element: JsonElement): JsonElement? = content.put(key, element)
    fun put(key: String, element: String): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Number): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Boolean): JsonElement? = content.put(key, JsonPrimitive(element))

    fun build(): JsonObject = JsonObject(content)
}

open class ProtocolBuilder (protected val type: String, protected val namespace: String, protected val name: String, messageId: String?) {
    private val headerBuilder: Builder = Builder()
    private var payloadBuilder: Builder? = null
//    private var contextBuilder: Builder? = null
    private var context: JsonElement? = null
    private var endpoint: JsonObject? = null

    init {
        headerBuilder.put("namespace", namespace)
        headerBuilder.put("name", name)
        if (messageId != null)
            headerBuilder.put("messageId", messageId)
    }

    fun addHeader(key: String, element: JsonElement): JsonElement ? = headerBuilder.put(key, element)
    fun addHeader(key: String, element: String): JsonElement ? = headerBuilder.put(key, element)
    fun addHeader(key: String, element: Number): JsonElement ? = headerBuilder.put(key, element)
    fun addHeader(key: String, element: Boolean): JsonElement ? = headerBuilder.put(key, element)

    fun addPayload(key: String, element: JsonElement): JsonElement? {
        if (payloadBuilder == null)
            payloadBuilder = Builder()
        return payloadBuilder!!.put(key, element)
    }
    fun addPayload(key: String, element: String): JsonElement? {
        if (payloadBuilder == null)
            payloadBuilder = Builder()
        return payloadBuilder!!.put(key, element)
    }
    fun addPayload(key: String, element: Number): JsonElement? {
        if (payloadBuilder == null)
            payloadBuilder = Builder()
        return payloadBuilder!!.put(key, element)
    }
    fun addPayload(key: String, element: Boolean): JsonElement? {
        if (payloadBuilder == null)
            payloadBuilder = Builder()
        return payloadBuilder!!.put(key, element)
    }

    fun setContext(context: JsonElement) {
        this.context = context
    }

    fun setEndpoint(endpoint: JsonObject) {
        this.endpoint = endpoint
    }

    fun create(): JsonObject =
        buildJsonObject {
            put(type, buildJsonObject {
                put("header", headerBuilder.build())
                payloadBuilder?.let {
                    put("payload", payloadBuilder!!.build())
                }
            })
            context?.let {
                put("context", context!!)
            }
            endpoint?.let {
                put("endpoint", endpoint!!)
            }
        }

    override fun toString(): String {
        return "$type { namespace = $namespace | name = $name }"
    }
}


open class EventBuilder (namespace: String, name: String, messageId: String? = makeMessageId()):
    ProtocolBuilder("event", namespace, name, messageId) {
}

open class DirectiveBuilder (namespace: String, name: String, messageId: String? = makeMessageId()):
    ProtocolBuilder("directive", namespace, name, messageId) {
}