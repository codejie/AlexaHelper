package jie.android.alexahelper.smartwatchsdk.sdk

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

interface OnResultCallback {
    fun onResult(data: String, extra: Any? = null)
}

interface OnActionListener {
    fun onAction(data: String, extra: Any?, callback: OnResultCallback)
}

class Builder {
    private val content: MutableMap<String, JsonElement> = linkedMapOf()

    fun put(key: String, element: JsonElement): JsonElement? = content.put(key, element)
    fun put(key: String, element: String): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Number): JsonElement? = content.put(key, JsonPrimitive(element))
    fun put(key: String, element: Boolean): JsonElement? = content.put(key, JsonPrimitive(element))

    fun size(): Int = content.size
    fun build(): JsonObject = JsonObject(content)
}

open class SDK constructor(var type: String, var name: String?, var version: Int) {
    protected val dataBuilder: Builder = Builder()
    var data: JsonObject? = null
    var extra: Any? = null

    init {
        dataBuilder.put("type", type)
        if (name != null) {
            dataBuilder.put("name", name!!)
        }
        dataBuilder.put("version", version)
    }

    fun setPayload(payload: JsonObject) {
        dataBuilder.put("payload", payload)
    }

    fun getPayload(checked: Boolean = true): JsonObject? {
        val ret = data?.get("payload") as JsonObject?
        if (ret == null && checked)
            throw SDKException(SDKConst.RESULT_CODE_INVALID_FORMAT, SDKConst.RESULT_MESSAGE_INVALID_FORMAT)
        return ret
    }

    fun build(): JsonObject {
        data = dataBuilder.build()
        return data!!
    }
}

class ActionWrapper(name: String?, version: Int = 1) : SDK("action", name, version) {
    companion object {
        public fun parse(data: String, extra: Any?, callback: OnResultCallback): ActionWrapper {
            try {
                val ret: ActionWrapper = ActionWrapper()
                ret.data = Json.parseToJsonElement(data) as JsonObject

                ret.type = ret.data?.get("type").toString()
                ret.name = ret.data?.get("name").toString()
                ret.version = ret.data?.get("version").toString().toInt()

                if (ret.name == null) {
                    throw SDKException(SDKConst.RESULT_CODE_INVALID_FORMAT, SDKConst.RESULT_MESSAGE_INVALID_FORMAT)
                }

                ret.extra = extra
                ret.callback = callback

                return ret
            } catch (e: SerializationException) {
                throw e
            }
        }
    }

    constructor(): this(null, 1) { }
    var callback: OnResultCallback? = null
}

class ResultWrapper(name: String?, var code: Int, var message: String? = null, version: Int = 1): SDK("result", name, version) {
    companion object {
        fun parse(data: String, extra: Any?): ResultWrapper {
            try {
                val ret: ResultWrapper = ResultWrapper()
                ret.data = Json.parseToJsonElement(data) as JsonObject

                ret.type = ret.data?.get("type").toString()
                ret.name = ret.data?.get("name").toString()
                ret.version = ret.data?.get("version").toString().toInt()

                if (ret.name == null) {
                    throw SDKException(SDKConst.RESULT_CODE_INVALID_FORMAT, SDKConst.RESULT_MESSAGE_INVALID_FORMAT)
                }

                ret.extra = extra

                return ret
            } catch (e: SerializationException) {
                throw e
            }
        }
    }

    constructor(): this(null, 0, null, 1) { }

    init {
        dataBuilder.put("code", code)
        if (message != null) {
            dataBuilder.put("message", message!!)
        }
    }
}

fun JsonObject.getString(key: String, checked: Boolean = true): String? {
    val ret = this[key]?.toString()
    if (ret == null && checked)
        throw SDKException(
            SDKConst.RESULT_CODE_MISSING_FIELD,
            "${SDKConst.RESULT_MESSAGE_MISSING_FIELD} - $key")
    return ret
}

fun JsonObject.getInt(key: String, checked: Boolean = true): Int? {
    val ret = this[key]?.toString()?.toInt()
    if (ret == null && checked)
        throw SDKException(
            SDKConst.RESULT_CODE_MISSING_FIELD,
            "${SDKConst.RESULT_MESSAGE_MISSING_FIELD} - $key"
        )
    return ret
}

fun JsonObject.getBoolean(key: String, checked: Boolean = true): Boolean? {
    val ret = this[key]?.toString()?.toBoolean()
    if (ret == null && checked)
        throw SDKException(
            SDKConst.RESULT_CODE_MISSING_FIELD,
            "${SDKConst.RESULT_MESSAGE_MISSING_FIELD} - $key"
        )
    return ret
}
fun JsonObject.getJsonObject(key: String, checked: Boolean = true): JsonObject? {
    val ret = this[key]?.jsonObject
    if (ret == null && checked)
        throw SDKException(
            SDKConst.RESULT_CODE_MISSING_FIELD,
            "${SDKConst.RESULT_MESSAGE_MISSING_FIELD} - $key"
        )
    return ret
}
