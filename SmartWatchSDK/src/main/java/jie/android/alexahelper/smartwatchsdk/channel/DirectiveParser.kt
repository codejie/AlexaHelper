package jie.android.alexahelper.smartwatchsdk.channel

import kotlinx.serialization.json.JsonObject

abstract class DirectiveParser {
    enum class PartType {
        DIRECTIVE,
        OCTET_BUFFER
    }

    open class Part(val type: PartType, val headers: Map<String, String>) {
        override fun toString(): String {
            var headerString = ""
            for ((k, v) in headers) {
                headerString += "$k: $v"
            }
            return "Part ($type)\n$headerString"
        }
    }

    class DirectivePart(headers: Map<String, String>, val directive: JsonObject)
        : Part(PartType.DIRECTIVE, headers) {
    }

    class OctetBufferPart(headers: Map<String, String>, val buffer: ByteArray)
        : Part(PartType.OCTET_BUFFER, headers) {
        }
}