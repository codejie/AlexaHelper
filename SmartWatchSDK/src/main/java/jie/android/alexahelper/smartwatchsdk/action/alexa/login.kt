package jie.android.alexahelper.smartwatchsdk.action.alexa

import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.EndpointInfo
import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.alexa.Event
import jie.android.alexahelper.smartwatchsdk.alexa.Utils.makeMessageId
import jie.android.alexahelper.smartwatchsdk.channel.HttpChannel
import jie.android.alexahelper.smartwatchsdk.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.sdk.SDKException
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.utils.makeCodeChallenge
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.util.*

fun loginAction(requestContext: RequestContext, action: ActionWrapper) {
    if (DeviceInfo.Product.id == null || DeviceInfo.Product.serialNumber == null) {
        throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        )
    }

    requestContext.registerListener(object: AuthorizeListener() {
        override fun onSuccess(result: AuthorizeResult?) {
            onAuthorizeSuccess(action, result)
        }

        override fun onError(error: AuthError?) {
            onAuthorizeFailed(action, error?.message)
        }

        override fun onCancel(result: AuthCancellation?) {
            onAuthorizeFailed(action, result.toString())
        }
    })

    RuntimeInfo.verifierCode = UUID.randomUUID().toString()
    val challengeCode = makeCodeChallenge(RuntimeInfo.verifierCode!!)
    val scopeData = buildJsonObject {
        putJsonObject("productInstanceAttributes") {
            put("deviceSerialNumber", DeviceInfo.Product.serialNumber)
        }
        put("productID", DeviceInfo.Product.id)
    }

    AuthorizationManager.authorize(
        AuthorizeRequest.Builder(requestContext)
            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
            .withProofKeyParameters(challengeCode, "S256")
            .build())
}

private fun onAuthorizeFailed(action: ActionWrapper, message: String?) {
    action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_LOGIN_FAIL, message).build().toString())
}

private fun onAuthorizeSuccess(action: ActionWrapper, result: AuthorizeResult?) {
    RuntimeInfo.authorizeCode = result?.authorizationCode
    RuntimeInfo.redirectUri = result?.redirectURI

    fetchAuthorizeToken(action)
}


private fun fetchAuthorizeToken(action: ActionWrapper) {
    HttpChannel.postAuthorize { success, reason, response ->
        if (success) {
            // create down channel
            Logger.d("fetchAuthorizeToken success - ${response!!.code}")
            createDownChannel(action)
        } else {
            Logger.w("authorize token failed - $reason")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason").build().toString())
        }
    }
}

private fun createDownChannel(action: ActionWrapper): Unit {
    HttpChannel.getDownChannel { success, reason, _ ->
        if (success) {
            // synchronize state
            Logger.d("createDownChannel success")
            postSynchronizeStateAction(action)
        } else {
            Logger.w("create down channel failed - $reason")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "create down channel failed - $reason").build().toString())
        }
    }
}

private fun postSynchronizeStateAction(action: ActionWrapper) {
    val event: JsonObject = Event(AlexaConst.NS_SYSTEM, AlexaConst.NAME_SYNCHRONIZE_STATE).create()
    HttpChannel.postEvents(event) { success, reason, response ->
        if (success) {
            Logger.d("postSynchronizeStateAction success - ${response!!.code}")
            postVerifyGateway(action)
        } else {
            Logger.w("postSynchronizeStateAction failed - $reason")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postSynchronizeStateAction - $reason").build().toString())
        }
    }
}

private fun postVerifyGateway(action: ActionWrapper) {
    val event: JsonObject = Event(AlexaConst.NS_ALEXA_API_GATEWAY, AlexaConst.NAME_VERIFY_GATEWAY).create()
    HttpChannel.postEvents(event) { success, reason, response ->
        if (success) {
            Logger.d("postVerifyGateway success - ${response!!.code}")
            postAlexaDiscovery(action)
        } else {
            Logger.w("postVerifyGateway failed - $reason")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postVerifyGateway - $reason").build().toString())
        }
    }
}

private fun postAlexaDiscovery(action: ActionWrapper) {
    val event: JsonObject = Event(AlexaConst.NS_ALEXA_DISCOVERY, AlexaConst.NAME_ADD_OR_UPDATE_REPORT).apply {
        setHeader("payloadVersion", "3")
        setHeader("eventCorrelationToken", makeMessageId())

        val scope: JsonObject = buildJsonObject {
            put("type","BearerToken")
            put("token", RuntimeInfo.accessToken)
        }
        setPayload("scope", scope)
        setPayload("endpoints", buildJsonArray {
            for (element in EndpointInfo.endpoints.values) {
                add(element)
            }
        })
    }.create()

    HttpChannel.postEvents(event) { success, reason, response ->
        if (success) {
            Logger.d("postAlexaDiscovery success - ${response!!.code}")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString())
        } else {
            Logger.w("postAlexaDiscovery failed - $reason")
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postAlexaDiscovery - $reason").build().toString())
        }
    }
}
