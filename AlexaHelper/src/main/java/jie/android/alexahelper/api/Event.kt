package jie.android.alexahelper.api

import kotlinx.serialization.json.*
//
//internal class EventBuilder constructor(namespace: String, name: String, messageId: String?) {
//    private val content: MutableMap<String, JsonElement> = linkedMapOf()
//
//    init {
//        content["namespace"] = JsonPrimitive(namespace)
//        content["name"] = JsonPrimitive(name)
//        if (messageId != null) {
//            content["messageId"] = JsonPrimitive(messageId)
//        }
//
//    }
//
//    fun put(key: String, element: JsonElement): JsonElement? = content.put(key, element)
//    fun put(key: String, element: String): JsonElement? = content.put(key, JsonPrimitive(element))
//    fun put(key: String, element: Number): JsonElement? = content.put(key, JsonPrimitive(element))
//    fun put(key: String, element: Boolean): JsonElement? = content.put(key, JsonPrimitive(element))
//
//    fun build(): JsonObject = JsonObject(content)
//}
//
//open class Event constructor(namespace: String, name: String, messageId: String? = makeMessageId())
//    : Protocol("Event", namespace, name, messageId) {
//        private val builder: EventBuilder = EventBuilder(namespace, name, messageId)
//
//        fun set(key: String, element: JsonElement): JsonElement? = builder.put(key, element)
//        fun set(key: String, element: String): JsonElement? = builder.put(key, JsonPrimitive(element))
//        fun set(key: String, element: Number): JsonElement? = builder.put(key, JsonPrimitive(element))
//        fun set(key: String, element: Boolean): JsonElement? = builder.put(key, JsonPrimitive(element))
//
//        fun create(): JsonObject = builder.build()
//    }