package jie.android.alexahelper.sdk

import kotlinx.serialization.json.*

class Builder {
    private val content: MutableMap<String, JsonElement> = linkedMapOf()

    fun put(key: String, element: JsonElement): JsonElement? = content.put(key, element)
    fun put(key: String, element: String): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Number): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Boolean): JsonElement? = content.put(key, JsonPrimitive(element))

    fun size(): Int = content.size
    fun build(): JsonObject = JsonObject(content)
}

open abstract class Base constructor(val type: String, val name: String, val version: Int = 1) {
    protected val resultBuilder: Builder = Builder()
    protected val dataBuilder: Builder = Builder()
    protected val extraBuilder: Builder = Builder()

    fun putResultField(key: String, element: JsonElement) = resultBuilder.put(key, element)
    fun putResultField(key: String, element: String) = resultBuilder.put(key, element)
    fun putResultField(key: String, element: Number) = resultBuilder.put(key, element)
    fun putResultField(key: String, element: Boolean) = resultBuilder.put(key, element)

    fun putDataField(key: String, element: JsonElement) = dataBuilder.put(key, element)
    fun putDataField(key: String, element: String) = dataBuilder.put(key, element)
    fun putDataField(key: String, element: Number) = dataBuilder.put(key, element)
    fun putDataField(key: String, element: Boolean) = dataBuilder.put(key, element)

    fun putExtraField(key: String, element: JsonElement) = extraBuilder.put(key, element)
    fun putExtraField(key: String, element: String) = extraBuilder.put(key, element)
    fun putExtraField(key: String, element: Number) = extraBuilder.put(key, element)
    fun putExtraField(key: String, element: Boolean) = extraBuilder.put(key, element)

    open fun build(): JsonObject =
        buildJsonObject {
            put("type", type)
            put("name", name)
            put("version", version)

            if (resultBuilder.size() >0)
                put("result", resultBuilder.build())
            if (dataBuilder.size() > 0)
                put("data", dataBuilder.build())
            if (extraBuilder.size() > 0)
                put("extra", extraBuilder.build())
        }

    override fun toString(): String {
        return "$type:$name($version)"
    }
}

class Action constructor(name: String, version: Int = 1)
    : Base("action", name, version) {
    }

class Result constructor(name: String, code: Int = 0, message: String? = null, version:Int = 1)
    : Base("result", name, version) {
    init {
        with(resultBuilder){
            put("code", code)
            if (message != null)
                put("message", message)
        }
    }
}

