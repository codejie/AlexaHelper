package jie.android.alexahelper.utils

import okhttp3.*
import okio.Buffer
import java.nio.charset.StandardCharsets

class MyLoggingInterceptor(var level: Level = Level.BODY): Interceptor {
    enum class Level {
        NONE,
        BASIC,
        HEADERS,
        BODY
    }

    private fun log(msg: String): Unit {
        Logger.d(msg)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request: Request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }

        val requestBody: RequestBody? = request.body
        val connection: Connection? = chain.connection()

        var requestStartMessage = "--> ${request.method} ${connection?.protocol()?.name }"
        if (level == Level.BODY || level == Level.HEADERS) {
            requestStartMessage += "(${requestBody?.contentLength()}) byte body"
        }
        log(requestStartMessage)

        if (level == Level.BODY || level == Level.HEADERS) {
            log("[Request Headers]")
            for ((key, value) in request.headers) {
                log("$key: $value")
            }
        }

        if (level == Level.BODY && requestBody != null) {
            log("[Request Body]")
            val type: MediaType? = requestBody.contentType()
            if (type != null && type.type == "multipart") {
                val multipartBody: MultipartBody = requestBody as MultipartBody
                for (i in 0 until  multipartBody.size) {
                    log("[Request Body Part - $i]")
                    val part: MultipartBody.Part = multipartBody.part(i)
                    log("[Request Body Part ($i) Headers]")
                    if (part.headers != null) {
                        for ((k, v) in part.headers!!) {
                            log("$k: $v")
                        }
                    }
                    log("[Request Body Part ($i) Body]")
                    val contentType: String? = part.headers?.get("Content-Disposition")
                    if (contentType != null && contentType.indexOf("metadata") != -1) {
                        val buffer: Buffer = Buffer()
                        part.body.writeTo(buffer)
                        log(buffer.readString(StandardCharsets.UTF_8))
                    } else {
                        log("<Audio Data>")
                    }
                }
            } else {
                val buffer: Buffer = Buffer()
                requestBody.writeTo(buffer)
                log(buffer.readString(StandardCharsets.UTF_8))
            }
            log("--> END ${request.method} ${requestBody.contentLength()} byte body")
        } else {
            log("--> END ${request.method} empty body")
        }

        val startMs: Long = System.nanoTime()
        val response: Response = chain.proceed(request)
        val tookMS: Long = System.nanoTime() - startMs

        val responseBody: ResponseBody? = response.body
        log("<-- ${response.code} ${response.protocol.name} ${response.message} ${response.request.url} $tookMS ms ${responseBody?.contentLength() ?: 0} byte body")

        if (level == Level.BODY || level == Level.HEADERS) {
            log("[Response Headers]")
            for ((key, value) in response.headers) {
                log("$key: $value")
            }

            if (level == Level.BODY) {
                log("[Response Body]")
                if (responseBody?.contentLength() == 0L) {
                    log("<-- END (0) byte body")
                } else if (responseBody?.contentLength() == -1L) {
                    val type: MediaType? = responseBody.contentType()
                    when (type?.subtype) {
                        "multipart" -> log("<-- END (multipart) body")
                        "octet-stream" -> log("<-- END (octet-stream) body")
                        "json" -> {
                            log(responseBody.string())
                            log("<-- END (json) body")
                        }
                        else -> log("<-- END (${type?.subtype}） body")
                    }
                } else {
                    val buffer: Buffer = response.peekBody(responseBody!!.contentLength()).source().buffer
                    log(buffer.readString(StandardCharsets.UTF_8))
                    log("<-- END (${responseBody.contentLength()}） byte body")
                }
            } else {
                log("<-- END HTTP")
            }
        }

        return response
    }
}