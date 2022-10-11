package jie.android.alexahelper

import android.content.Context
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.device.event.alexaApiGateway.VerifyGatewayEvent
import jie.android.alexahelper.device.event.alexaDiscovery.AddOrUpdateReportEvent
import jie.android.alexahelper.device.event.system.SynchronizeStateEvent
import jie.android.alexahelper.channel.HttpChannel
import jie.android.alexahelper.device.EndpointInfo
import jie.android.alexahelper.device.ProductInfo
import jie.android.alexahelper.device.RuntimeInfo
import jie.android.alexahelper.utils.Logger
import jie.android.alexahelper.utils.ScheduleTimer
import jie.android.alexahelper.utils.makeCodeChallenge
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.util.*

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
            when (what) {
                Message.MSG_LOGIN_SUCCESS -> {
                    appDeviceCallback.onMessage(what.value, result)
                }
                Message.MSG_LOGIN_FAIL -> {
                    appDeviceCallback.onMessage(what.value, result)
                }
                else -> {
                    Logger.w("unknown message ${what.name}")
                }
            }
        }
    };


    fun setProductInfo(id: String, clientId: String, serial: String): Unit {
        ProductInfo.id = id
        ProductInfo.clientId = clientId
        ProductInfo.serialNumber = serial
    }

    fun addEndpoint(id: String, json: String): Unit {
        EndpointInfo.add(id, json)
    }

    fun attach(context: Context, appDeviceCallback: AppDeviceCallback): Unit {
        this.appDeviceCallback = appDeviceCallback
        // RuntimeInfo
        RuntimeInfo.load(context)
        // authorize request
        requestContext = RequestContext.create(context)
        // http channel
        httpChannel = HttpChannel(deviceCallback)

        ScheduleTimer.start()

        ScheduleTimer.addTimer(ScheduleTimer.Timer(5000L, true, "1 timer") {
            Logger.v((it ?: "null") as String)
        })

        val id = ScheduleTimer.addTimer(ScheduleTimer.Timer(3000L, true, "2 timer") {
            Logger.v((it ?: "null") as String)
        })

        ScheduleTimer.addTimer(ScheduleTimer.Timer(8000L, false, "3 timer") {
            ScheduleTimer.removeTimer(id)
            Logger.v((it ?: "null") as String)
        })


    }

    fun detach(context: Context): Unit {
        ScheduleTimer.stop()
        RuntimeInfo.flush(context)
    }

    fun onResume(context: Context): Unit {
        requestContext.onResume()
    }

    fun login(): Unit {
//        deviceCallback(Message.LOGIN, null)
        if (RuntimeInfo.refreshToken != null) {
            authorizeWithToken()
        } else {
            authorize()
        }
    }

    private fun authorizeWithToken() {
        httpChannel.postRefreshAccessToken { success, reason, response ->
            if (success) {
                createDownChannel()
            } else {
                // failed
                deviceCallback(Message.MSG_LOGIN_FAIL, reason)
            }
        }
    }

    private fun authorize(): Unit {
        requestContext.registerListener(object: AuthorizeListener() {
            override fun onSuccess(result: AuthorizeResult?) {
                Logger.d("auth success")
                onAuthorizeSuccess(result!!)
            }

            override fun onError(e: AuthError?) {
                Logger.e("auth failed")
                deviceCallback(Message.MSG_LOGIN_FAIL, e?.message?: "login failed")
            }

            override fun onCancel(e: AuthCancellation?) {
                deviceCallback(Message.MSG_LOGIN_FAIL, e?.description)
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
        val event: JsonObject = AddOrUpdateReportEvent().apply {}.create()
        httpChannel.postEvents(event) { success, reason, response ->
            if (success) {
                Logger.d("postAlexaDiscovery success - ${response!!.code}")
                deviceCallback(Message.MSG_LOGIN_SUCCESS, null)
            } else {
                Logger.w("postAlexaDiscovery failed - $reason")
            }
        }
    }
}