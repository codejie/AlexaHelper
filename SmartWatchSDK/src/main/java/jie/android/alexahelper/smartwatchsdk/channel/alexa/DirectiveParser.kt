package jie.android.alexahelper.smartwatchsdk.channel.alexa

import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import okhttp3.Response
import okio.BufferedSource
import java.util.*

open class DirectiveParser {
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
        override fun toString(): String {
            return "${super.toString()}\n${directive.toString()}"
        }
    }

    class OctetBufferPart(headers: Map<String, String>, val buffer: ByteArray)
        : Part(PartType.OCTET_BUFFER, headers) {
        }

    protected var boundary: String? = null
    protected var endBoundary: String? = null

    protected open fun parsePart(content: String): Part? {
        val pos = content.indexOf("\r\n\r\n")
        if (pos == -1) {
            return null
        }
        val headers: MutableMap<String, String> = hashMapOf<String, String>() // HashMap()
        val scanner = Scanner(content.substring(0, pos))
        while (scanner.hasNext()) {
            val pair = scanner.nextLine().split(": ").toTypedArray()
            headers[pair[0]] = pair[1]
        }
        return if (headers["Content-Type"]!!.contains("application/json")) {
            buildDirectivePart(headers, content.substring(pos + 4))
        } else if (headers["Content-Type"]!!.contains("application/octet-stream")) {
            buildOctetBufferPart(headers, content.substring(pos + 4).toByteArray())
        } else {
            null
        }
    }

    protected open fun buildDirectivePart(headers: Map<String, String>, content: String): DirectivePart? {
       Logger.v("parse directive content - \n$content")
        try {
            val directive: JsonObject = Json.parseToJsonElement(content) as JsonObject
            return DirectivePart(headers, directive)
        } catch (e: SerializationException) {
            Logger.w("parse directive failed - $content")
        }
        return null
    }

    protected open fun buildOctetBufferPart(headers: Map<String, String>, buffer: ByteArray): OctetBufferPart {
        return OctetBufferPart(
            headers,
            buffer
        )
    }
}

class DownChannelDirectiveParser() : DirectiveParser() {

    private val contentBuilder = StringBuilder()

    fun parseParts(input: String): List<Part>? {
        var pos = -1
        contentBuilder.append(input)
        if (boundary == null) {
            pos = input.indexOf("\r\n")
            if (pos != -1) {
                boundary = input.substring(0, pos + 2)
                endBoundary = "${input.substring(0, pos)}--"
                contentBuilder.delete(0, pos + 2)
            } else {
                return null
            }
        }
        val ret: MutableList<Part> = arrayListOf<Part>()
        pos = contentBuilder.toString().indexOf(boundary!!)
        while (pos != -1) {
            val part = parsePart(contentBuilder.substring(0, pos))
            if (part != null) {
                ret.add(part)
            }
            contentBuilder.delete(0, pos + boundary!!.length)
            pos = contentBuilder.toString().indexOf(boundary!!)
        }
        return ret
    }
}

fun ByteArray.findFirst(sequence: ByteArray, startFrom: Int = 0): Int {
//    if(sequence.isEmpty()) throw IllegalArgumentException("non-empty byte sequence is required")
//    if(startFrom < 0 ) throw IllegalArgumentException("startFrom must be non-negative")
    var matchOffset = 0
    var start = startFrom
    var offset = startFrom
    while( offset < size ) {
        if( this[offset] == sequence[matchOffset]) {
            if( matchOffset++ == 0 ) start = offset
            if( matchOffset == sequence.size ) return start
        }
        else
            matchOffset = 0
        offset++
    }
    return -1
}

class ResponseStreamDirectiveParser() : DirectiveParser() {

    companion object {
        const val CR: Byte = 0x0D
        const val LF: Byte = 0x0A
        fun bytesEqual(a: ByteArray, offset: Int, b: ByteArray, count: Int): Boolean {
            for (i in offset until count) {
                if (a[i] != b[i - offset]) {
                    return false
                }
            }
            return true
        }

    }

    fun parseParts(response: Response): List<Part> {

        val parts: MutableList<Part> = arrayListOf<Part>()
        var isHeader = false
        var isBody = false
        var isJson = false
        var headers: MutableMap<String, String> = hashMapOf()

        val source: BufferedSource = response!!.body!!.source()

        while (!source.exhausted()) {
            if (!isBody) {
                val line = source.readUtf8Line()
                if (line != null) {
                    if (boundary == null) {
                        boundary = line
                        endBoundary = "$boundary--"
                        isHeader = true
                    } else if (line == boundary) {
                        isHeader = true
                    } else if (line == endBoundary) {
                        boundary = null
                        endBoundary = null
                        isBody = false
                        isHeader = false
                    } else {
                        if (isHeader) {
                            if (line.isNotEmpty()) {
                                val tmp = line.split(": ").toTypedArray()
                                headers[tmp[0]] = tmp[1]
                            } else {
                                isHeader = false
                                isBody = true
                                if (headers["Content-Type"]!!.contains("application/json")) {
                                    isJson = true
                                } else if (headers["Content-Type"]!!.contains("application/octet-stream")) {
                                    isJson = false
                                } else {
                                    Logger.w("unknown type -${headers["Content-Type"]}")
                                }
                            }
                        }
                    }
                }
            } else {
                if (isJson) {
                    val l = source.readUtf8Line()
                    if (l != null) {
                        buildDirectivePart(headers, l)?.let { parts.add(it) }
                        headers = hashMapOf()
                        isJson = false
                        isBody = false
                    } else {
                        Logger.w("SOURCE read directive null")
                    }
                } else {
                    val boundaryBytes: ByteArray = boundary!!.toByteArray()
                    val cache = ByteArray(boundaryBytes.size)
                    val bigBuffer = ByteArray(256 * 1024)

                    val srcStream = source.inputStream()

                    var read = 0
                    var ch: Int
                    while ( srcStream.read().also { ch = it } != -1 ) {
                        bigBuffer[read] = ch.toByte()
                        if (bigBuffer[read] == CR) {
                            if (srcStream.read().also { ch = it } == -1) break
                            ++ read
                            bigBuffer[read] = ch.toByte()
                            if (bigBuffer[read] == LF) {
                                if (srcStream.read(cache, 0, boundaryBytes.size) < boundaryBytes.size) break;
                                if (bytesEqual(cache, 0, boundaryBytes, boundaryBytes.size)) {
                                    break
                                }
                                cache.copyInto(bigBuffer, read + 1, boundaryBytes.size)
                                read += boundaryBytes.size - 1
                            }
                        }
                        ++ read
                    }

                    val buffer: ByteArray = ByteArray(read)
                    bigBuffer.copyInto(buffer, 0, 0, read)

                    val part = buildOctetBufferPart(headers, buffer)
                    parts.add(part)

                    headers = hashMapOf()
                    isBody = false
                }
            }
        }

        response.close()

        Logger.d("source end.")
        return parts
    }

}

