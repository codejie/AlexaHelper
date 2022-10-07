package jie.android.alexahelper

import android.content.Context
import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.channel.HttpChannel
import jie.android.alexahelper.device.ProductInfo
import jie.android.alexahelper.device.RuntimeInfo
import jie.android.alexahelper.utils.Logger
import jie.android.alexahelper.utils.Singleton
import jie.android.alexahelper.utils.makeCodeChallenge
import kotlinx.serialization.json.*
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import java.util.*

interface DeviceAuthorizeCallback {
    fun onSuccess(): Unit
    fun onError(error: String):Unit
}

typealias OnDownChannelCreated = (result: Boolean) -> Unit

class Device private constructor() {

    companion object: Singleton<Device>(::Device)

    private lateinit var requestContext: RequestContext

    internal lateinit var productInfo: ProductInfo
    internal lateinit var runtimeInfo: RuntimeInfo

    internal lateinit var httpChannel: HttpChannel

    fun attach(context: Context, productInfo: ProductInfo): Unit {
        // product
        this.productInfo = productInfo
        // RuntimeInfo
        runtimeInfo = RuntimeInfo(context)
        // authorize request
        requestContext = RequestContext.create(context)

        // http channel
        httpChannel = HttpChannel(this)
    }

    fun detach(context: Context): Unit {

    }

    fun onResume(): Unit {
        requestContext.onResume()
    }

    fun login(): Unit {
        authorize()
    }

    private fun authorize(): Unit {
        requestContext.registerListener(object: AuthorizeListener() {
            override fun onSuccess(p0: AuthorizeResult?) {
                Logger.d("auth success")
                TODO("Not yet implemented")

            }

            override fun onError(p0: AuthError?) {
                Logger.e("auth failed")
            }

            override fun onCancel(p0: AuthCancellation?) {
                TODO("Not yet implemented")
            }
        })

        runtimeInfo.verifierCode = UUID.randomUUID().toString()
        val challengeCode: String = makeCodeChallenge(runtimeInfo.verifierCode!!)

        val scopeData: JsonObject = buildJsonObject {
            putJsonObject("productInstanceAttributes") {
                put("deviceSerialNumber", productInfo.serialNumber)
            }
            put("productID", productInfo.productId)
        }

        AuthorizationManager.authorize(AuthorizeRequest.Builder(requestContext)
            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
            .withProofKeyParameters(challengeCode, "S256")
            .build())
    }

    private fun onAuthorizeSuccess(result: AuthorizeResult): Unit {
        runtimeInfo.authorizeCode = result.authorizationCode
        runtimeInfo.redirectUri = result.redirectURI

        fetchAuthorizeToken()
    }

    private fun fetchAuthorizeToken() {
        httpChannel.postAuthorize { result, reason ->
            if (result) {
                // create down channel
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