package jie.android.alexahelper

import android.content.Context
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
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

typealias OnDownChannelCreated = (result: Boolean) -> Unit

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
//    internal lateinit var productInfo: ProductInfo
//    internal lateinit var settingInfo: Settinginfo

    private lateinit var requestContext: RequestContext

//    internal lateinit var runtimeInfo: RuntimeInfo

    internal lateinit var deviceCallback: InnerDeviceCallback // = { integer: Integer, any: Any, function: AppDeviceCallback -> };
    private lateinit var httpChannel: HttpChannel

    fun setProductInfo(id: String, clientId: String, serial: String): Unit {
        ProductInfo.id = id
        ProductInfo.clientId = clientId
        ProductInfo.serialNumber = serial
    }

    fun attach(context: Context, appDeviceCallback: AppDeviceCallback): Unit {
        deviceCallback  = { what: Message, result: Any? ->
            synchronized(this) {
                when (what) {
                    Message.LOGIN -> {
                        appDeviceCallback(Message.LOGIN.value, result)
                    }
                    else -> {}
                }
            }
        }
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
        httpChannel.postAuthorize { result, reason ->
            if (result) {
                // create down channel
                createDownChannel { result -> deviceCallback(Message.LOGIN, result) }
                // synchronize state
//                postSynchronizeStateAction()
            } else {
                Logger.w("authorize token failed - $reason")
            }
        }
    }

    private fun createDownChannel(callback: OnDownChannelCreated): Unit {
        httpChannel.getDownChannel { result, response ->
            if (result) {
                // DownChannel

                // callback
                callback(true)
            } else {
                Logger.w("create down channel failed.")
                callback(false)
            }
        }
    }

}