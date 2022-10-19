package jie.android.alexahelper.smartwatchsdk.channel

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.utils.makePartBoundary
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

typealias DownChannelActionCallback = () -> Unit
internal typealias ChannelPostCallback = (success: Boolean, reason: String?, response: Response?) -> Unit

object HttpChannel {

    var avsBaseUrl: String = "https://alexa.na.gateway.devices.a2z.com"
    private const val avsAuthorizeUrl: String = "https://api.amazon.com/auth/o2/token"
    private const val avsVersion: String = "/v20160207"

    private val downChannel: DownChannel = DownChannel()

    var downChannelCallback: DownChannelActionCallback = {

    }

    private var client: OkHttpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .readTimeout(60, TimeUnit.MINUTES)
        .writeTimeout(60, TimeUnit.MINUTES)
        .connectTimeout(10000, TimeUnit.MILLISECONDS)
        .pingInterval(280, TimeUnit.SECONDS)
        .addInterceptor(MyLoggingInterceptor().apply { level = MyLoggingInterceptor.Level.BODY })
        .build()

    fun postEvents(bodies: Map<String, RequestBody>, callback: ChannelPostCallback): Unit {
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
                callback(false, e.message, null)
            }

            override fun onResponse(call: Call, response: Response) {
                response.close()
                callback(true, null, response)
            }

        })
    }

    fun postEvents(json: JsonObject, callback: ChannelPostCallback): Unit {
        val body: RequestBody = json.toString().toRequestBody(
            "application/json;charset=utf-8".toMediaType())

        val bodies: Map<String, RequestBody> = mutableMapOf(Pair<String, RequestBody>("metadata", body))
        postEvents(bodies, callback)
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
                downChannel.start(response, downChannelCallback)
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