package jie.android.alexahelper.smartwatchsdk.channel.alexa

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

typealias DownChannelActionCallback = () -> Unit
internal typealias ChannelPostCallback = (success: Boolean, reason: String?, response: Response?) -> Unit

class HttpChannel constructor(val sdk: SmartWatchSDK) {

    var avsBaseUrl: String = "https://alexa.na.gateway.devices.a2z.com"
    private val avsAuthorizeUrl: String = "https://api.amazon.com/auth/o2/token"
    private val avsVersion: String = "/v20160207"

    public var isLogin = false

    private val downChannel: DownChannel = DownChannel(sdk)

    private var client: OkHttpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .readTimeout(60, TimeUnit.MINUTES)
        .writeTimeout(60, TimeUnit.MINUTES)
        .connectTimeout(10000, TimeUnit.MILLISECONDS)
        .pingInterval(280, TimeUnit.SECONDS)
        .addInterceptor(MyLoggingInterceptor().apply { level = MyLoggingInterceptor.Level.BODY })
        .build()

    private fun postEvent(closeResponse: Boolean, bodies: Map<String, RequestBody>, callback: ChannelPostCallback?): Unit {
        val boundary: String = makePartBoundary()
        val builder: MultipartBody.Builder = MultipartBody.Builder()
        with (builder) {
            "multipart/form-data".toMediaTypeOrNull()?.let { this.setType(it) }
            for ((key, value) in bodies) {
                this.addFormDataPart(key, null, value)
            }
        }

        val request: Request = Request.Builder()
            .url("$avsBaseUrl$avsVersion/events")
            .addHeader("authorization", "Bearer ${RuntimeInfo.accessToken}")
            .addHeader("content_type", "multipart/form-data;boundary=$boundary")
            .post(builder.build())
            .build()

        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                if (callback != null)
                    callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (closeResponse) response.close()
                if (callback != null)
                    callback(true, null, response)
            }

        })
    }

    fun postEvent(closeResponse: Boolean, json: JsonObject, callback: ChannelPostCallback?): Unit {
        val body: RequestBody = json.toString().toRequestBody(
            "application/json;charset=utf-8".toMediaType())

        val bodies: Map<String, RequestBody> = mutableMapOf(Pair<String, RequestBody>("metadata", body))
        postEvent(closeResponse, bodies, callback)
    }

    fun postEvent(json: JsonObject, callback: ChannelPostCallback?): Unit {
        postEvent(true, json, callback)
    }

    fun postEventWithAudio(json: JsonObject, extra: ByteArray, callback: ChannelPostCallback?): Unit {
        val body: RequestBody = json.toString().toRequestBody("application/json;charset=utf-8".toMediaType())

        val audio: RequestBody = extra.toRequestBody("application/octet-stream".toMediaType())

        val bodies: Map<String, RequestBody> = mutableMapOf(
            Pair<String, RequestBody>("metadata", body),
            Pair<String, RequestBody>("audio", audio)
        )
        postEvent(false, bodies, callback)
    }

    internal fun postAuthorize(callback: ChannelPostCallback) {
        val body: JsonObject = buildJsonObject {
            put("grant_type", "authorization_code")
            put("code", RuntimeInfo.authorizeCode)
            put("redirect_uri", RuntimeInfo.redirectUri)
            put("client_id", DeviceInfo.Product.clientId)
            put("code_verifier", RuntimeInfo.verifierCode)
        }

        val request: Request = Request.Builder()
            .url(avsAuthorizeUrl)
            .post(body.toString().toRequestBody( "application/json;charset=utf-8".toMediaType()))
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code in 200..204) {
                    val result: JsonObject =
                        Json.parseToJsonElement(response.body!!.string()) as JsonObject

                    RuntimeInfo.accessToken = result["access_token"]?.jsonPrimitive?.content
                    RuntimeInfo.refreshToken = result["refresh_token"]?.jsonPrimitive?.content

                    response.close()
                    callback(true, null, response)
                } else {
                    response.close()
                    callback(false, "response code - ${response.code}", response)
                }
            }
        })
    }

    internal fun getDownChannel(callback: ChannelPostCallback): Unit {
        val request: Request = Request.Builder()
            .url("$avsBaseUrl$avsVersion/directives")
            .addHeader("authorization", "Bearer ${RuntimeInfo.accessToken}")
            .build()
        client.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                downChannel.start(response)
                callback(true, null, response)
            }
        })
    }

    fun postRefreshAccessToken(refreshToken: String, callback: ChannelPostCallback) {
        val body: JsonObject = buildJsonObject {
            put("grant_type", "refresh_token")
            put("refresh_token", refreshToken)
            put("client_id", DeviceInfo.Product.clientId)
        }
        val request: Request = Request.Builder()
            .url(avsAuthorizeUrl)
            .post(body.toString().toRequestBody( "application/json;charset=utf-8".toMediaType()))
            .build()

        client.newCall(request).enqueue(object: Callback{
            override fun onFailure(call: Call, e: IOException) {
                callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.code in 200..204) {
                    val result: JsonObject =
                        Json.parseToJsonElement(response.body!!.string()) as JsonObject

                    RuntimeInfo.accessToken = result["access_token"]?.jsonPrimitive?.content
                    RuntimeInfo.refreshToken = result["refresh_token"]?.jsonPrimitive?.content

                    response.close()
                    callback(true, null, response)
                } else {
                    response.close()
                    callback(false, "response code - ${response.code}", response)
                }
            }
        })
    }

}