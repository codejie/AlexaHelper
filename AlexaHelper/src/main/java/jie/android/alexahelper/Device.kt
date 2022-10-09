package jie.android.alexahelper

import android.content.Context
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.api.event.alexaApiGateway.VerifyGatewayEvent
import jie.android.alexahelper.api.event.alexaDiscovery.AddOrUpdateReportEvent
import jie.android.alexahelper.api.event.system.SynchronizeStateEvent
import jie.android.alexahelper.channel.HttpChannel
import jie.android.alexahelper.device.ProductInfo
import jie.android.alexahelper.device.RuntimeInfo
import jie.android.alexahelper.utils.Logger
import jie.android.alexahelper.utils.makeCodeChallenge
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.util.*

//typealias OnDownChannelCreated = (result: Boolean) -> Unit

class Device private constructor() {

    companion object {
        private var instance: Device? = null
        fun create(): Device {
            synchronized(this) {
                if (instance === null) {
                    instance = Device()
                }
            }
            return instance!!
        }
    }

    private lateinit var requestContext: RequestContext
    private lateinit var httpChannel: HttpChannel

    private lateinit var appDeviceCallback: AppDeviceCallback;
    internal val deviceCallback: InnerDeviceCallback  = { what: Message, result: Any? ->
        synchronized(this) {

        }
    };


    fun setProductInfo(id: String, clientId: String, serial: String): Unit {
        ProductInfo.id = id
        ProductInfo.clientId = clientId
        ProductInfo.serialNumber = serial

//        val json: JsonObject = Json.parseToJsonElement("""
//            {"a": "b"}
//        """.trimIndent()) as JsonObject
//        val m: String? = json["a"]?.jsonPrimitive?.content
//
//        Logger.v("$m")
    }

    fun attach(context: Context, appDeviceCallback: AppDeviceCallback): Unit {
        this.appDeviceCallback = appDeviceCallback
        // RuntimeInfo
//        runtimeInfo = RuntimeInfo(context)
        RuntimeInfo.load(context)
        // authorize request
        requestContext = RequestContext.create(context)
        // http channel
        httpChannel = HttpChannel(deviceCallback)
    }

    fun detach(context: Context): Unit {
        RuntimeInfo.flush(context)
    }

    fun onResume(context: Context): Unit {
        requestContext.onResume()
    }

    fun login(): Unit {
//        deviceCallback(Message.LOGIN, null)
        authorize()
    }

    private fun authorize(): Unit {
        requestContext.registerListener(object: AuthorizeListener() {
            override fun onSuccess(result: AuthorizeResult?) {
                Logger.d("auth success")
                onAuthorizeSuccess(result!!)
            }

            override fun onError(p0: AuthError?) {
                Logger.e("auth failed")
            }

            override fun onCancel(p0: AuthCancellation?) {
                TODO("Not yet implemented")
            }
        })

        RuntimeInfo.verifierCode = UUID.randomUUID().toString()
        val challengeCode: String = makeCodeChallenge(RuntimeInfo.verifierCode!!)

        val scopeData: JsonObject = buildJsonObject {
            putJsonObject("productInstanceAttributes") {
                put("deviceSerialNumber", ProductInfo.serialNumber)
            }
            put("productID", ProductInfo.id)
        }

        AuthorizationManager.authorize(AuthorizeRequest.Builder(requestContext)
            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
            .withProofKeyParameters(challengeCode, "S256")
            .build())
    }

    private fun onAuthorizeSuccess(result: AuthorizeResult): Unit {
        RuntimeInfo.authorizeCode = result.authorizationCode
        RuntimeInfo.redirectUri = result.redirectURI

        fetchAuthorizeToken()
    }

    private fun fetchAuthorizeToken() {
        httpChannel.postAuthorize { success, reason, response ->
            if (success) {
                // create down channel
                Logger.d("fetchAuthorizeToken success - ${response!!.code}")
                createDownChannel()
            } else {
                Logger.w("authorize token failed - $reason")
            }
        }
    }

    private fun createDownChannel(): Unit {
        httpChannel.getDownChannel { success, reason, response ->
            if (success) {
                // synchronize state
                Logger.d("createDownChannel success")
                postSynchronizeStateAction()
                // callback
//                deviceCallback()
            } else {
                Logger.w("create down channel failed - $reason")
//                callback(false)
            }
        }
    }

    private fun postSynchronizeStateAction() {
        val event: JsonObject = SynchronizeStateEvent().create()
        httpChannel.postEvents(event) { success, reason, response ->
            if (success) {
                Logger.d("postSynchronizeStateAction success - ${response!!.code}")
                postVerifyGateway()
            } else {
                Logger.w("postSynchronizeStateAction failed - $reason")
            }
        }
    }

    private fun postVerifyGateway() {
        val event: JsonObject = VerifyGatewayEvent().create()
        httpChannel.postEvents(event) { success, reason, response ->
            if (success) {
                Logger.d("postVerifyGateway success - ${response!!.code}")
                postAlexaDiscovery()
            } else {
                Logger.w("postVerifyGateway failed - $reason")
            }
        }
    }

    private fun postAlexaDiscovery() {
        val event: JsonObject = AddOrUpdateReportEvent().apply {
            val endpoints: JsonArray = buildJsonArray {  }
            setPayload("endpoints", endpoints)
        }.create()
        httpChannel.postEvents(event) { success, reason, response ->
            if (success) {
                Logger.d("postAlexaDiscovery success - ${response!!.code}")
            } else {
                Logger.w("postAlexaDiscovery failed - $reason")
            }
        }
    }
}