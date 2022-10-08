package jie.android.alexahelper.channel

import jie.android.alexahelper.InnerDeviceCallback
import jie.android.alexahelper.device.ProductInfo
import jie.android.alexahelper.device.RuntimeInfo
import jie.android.alexahelper.utils.makePartBoundary
import kotlinx.serialization.json.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.util.concurrent.TimeUnit

//internal typealias CreateDownChannelCallback = (result: Boolean, reason: String?) -> Unit
//internal typealias FetchAuthorizeTokenCallback = (result: Boolean, reason: String?) -> Unit
internal typealias ChannelPostCallback = (success: Boolean, reason: String?, response: Response?) -> Unit

class HttpChannel (callback: InnerDeviceCallback) {

    private var avsBaseUrl: String = "https://alexa.na.gateway.devices.a2z.com"
    private val avsAuthorizeUrl: String = "https://api.amazon.com/auth/o2/token"
    private val avsVersion: String = "/v20160207"

    private val deviceCallback: InnerDeviceCallback = callback

    private var downChannel: DownChannel? = null;

    private var client: OkHttpClient = OkHttpClient.Builder()
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .readTimeout(60, TimeUnit.MINUTES)
        .writeTimeout(60, TimeUnit.MINUTES)
        .connectTimeout(10000, TimeUnit.MILLISECONDS)
        .pingInterval(280, TimeUnit.SECONDS)
        .build()

    internal fun postEvents(bodies: Map<String, RequestBody>, callback: ChannelPostCallback): Unit {
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

    internal fun postEvents(json: JsonObject, callback: ChannelPostCallback): Unit {
        val body: RequestBody = json.toString().toRequestBody(
            "application/json;charset=utf-8".toMediaType())

        val bodies: Map<String, RequestBody> = mutableMapOf(Pair<String, RequestBody>("metadata", body))
        postEvents(bodies, callback)
    }
//
//    internal fun postAuthorize(body: RequestBody, callback: Callback): Unit {
//        val request: Request = Request.Builder()
//            .url(avsAuthorizeUrl)
//            .post(body)
//            .build()
//        client.newCall(request).enqueue(callback)
//    }
//
//    internal fun postAuthorize(json: JsonObject, callback: Callback): Unit {
//        val body: RequestBody = json.toString().toRequestBody(
//            "application/json;charset=utf-8".toMediaType())
//        postAuthorize(body, callback)
//    }

    internal fun postAuthorize(callback: ChannelPostCallback) {
        val body: JsonObject = buildJsonObject {
            put("grant_type", "authorization_code")
            put("code", RuntimeInfo.authorizeCode)
            put("redirect_uri", RuntimeInfo.redirectUri)
            put("client_id", ProductInfo.clientId)
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
                        Json.parseToJsonElement(response.body!!.string()).jsonObject

                    RuntimeInfo.accessToken = result["access_token"].toString()
                    RuntimeInfo.refreshToken = result["refresh_token"].toString()

                    response.close()
                    callback(true, null, null)
                } else {
                    response.close()
                    callback(false, "response code - ${response.code}", null)
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
                downChannel = DownChannel(response, deviceCallback)
                callback(true, null, null)
            }
        })
    }

}