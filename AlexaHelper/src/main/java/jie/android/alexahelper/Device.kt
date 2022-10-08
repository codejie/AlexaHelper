package jie.android.alexahelper

import android.content.Context
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.api.event.system.SynchronizeStateEvent
import jie.android.alexahelper.channel.ChannelPostCallback
import jie.android.alexahelper.channel.HttpChannel
import jie.android.alexahelper.device.ProductInfo
import jie.android.alexahelper.device.RuntimeInfo
import jie.android.alexahelper.utils.Logger
import jie.android.alexahelper.utils.makeCodeChallenge
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject
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
        deviceCallback(Message.LOGIN, null)
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
        httpChannel.postAuthorize { success, reason, _ ->
            if (success) {
                // create down channel
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
                postSynchronizeStateAction()
                // callback
//                deviceCallback()
            } else {
                Logger.w("create down channel failed.")
//                callback(false)
            }
        }
    }

    private fun postSynchronizeStateAction() {
        val event: JsonObject = SynchronizeStateEvent().apply {
            set("", "")
        }.create()
        httpChannel.postEvents(event) { success, reason, response ->

        }
    }
}