package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import jie.android.alexahelper.smartwatchsdk.*
import jie.android.alexahelper.smartwatchsdk.EndpointInfo
import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.channel.alexa.makeCodeChallenge
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Utils.makeMessageId
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.util.*

fun loginAction(sdk: SmartWatchSDK, withToken: Boolean, action: ActionWrapper) {
    if (withToken) {
        authorizeWithToken(sdk, action)
    } else {
        authorize(sdk, action)
//        fetchAuthorizeToken(sdk, action)
    }
}

private fun authorize(sdk: SmartWatchSDK, action: ActionWrapper) {
    if (DeviceInfo.Product.id == null || DeviceInfo.Product.serialNumber == null) {
        throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        )
    }

    sdk.requestContext.registerListener(object: AuthorizeListener() {
        override fun onSuccess(result: AuthorizeResult?) {
            onAuthorizeSuccess(sdk, action, result)
        }

        override fun onError(error: AuthError?) {
            onAuthorizeFailed(sdk, action, error?.message)
        }

        override fun onCancel(result: AuthCancellation?) {
            onAuthorizeFailed(sdk, action, result.toString())
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
        AuthorizeRequest.Builder(sdk.requestContext)
            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
            .withProofKeyParameters(challengeCode, "S256")
            .build())
}

private fun onAuthorizeFailed(sdk: SmartWatchSDK, action: ActionWrapper, message: String?) {
    action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_LOGIN_FAIL, message).build().toString())
}

private fun onAuthorizeSuccess(sdk: SmartWatchSDK, action: ActionWrapper, result: AuthorizeResult?) {
    RuntimeInfo.authorizationCode = result?.authorizationCode
    RuntimeInfo.redirectUri = result?.redirectURI

    fetchAuthorizeToken(sdk, action)
}

private fun authorizeWithToken(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload: JsonObject = action.data!!.getJsonObject("payload")!!;
    val refreshToken = payload.getString("refreshToken")
        ?: throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        );

    sdk.httpChannel.postRefreshAccessToken(refreshToken) { success, reason, _ ->
        if (success) {
            createDownChannel(sdk, action);
        } else {
            Logger.w("authorize token failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason").build().toString())
        }
    }
}

private fun fetchAuthorizeToken(sdk: SmartWatchSDK, action: ActionWrapper) {

//    val payload = action.getPayload()!!
//    RuntimeInfo.verifierCode = payload.getString("verifierCode")
//    RuntimeInfo.authorizationCode = payload.getString("authorizationCode")
//    RuntimeInfo.redirectUri = payload.getString("redirectUri")
//    RuntimeInfo.authorizationClientId = payload.getString("authorizationClientId")

    sdk.httpChannel.postAuthorize { success, reason, response ->
        if (success) {
            // create down channel
            Logger.d("fetchAuthorizeToken success - ${response!!.code}")
            createDownChannel(sdk, action)
        } else {
            Logger.w("authorize token failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason").build().toString())
        }
    }
}

private fun createDownChannel(sdk: SmartWatchSDK, action: ActionWrapper): Unit {
    sdk.httpChannel.getDownChannel { success, reason, _ ->
        if (success) {
            // synchronize state
            Logger.d("createDownChannel success")
            postSynchronizeStateAction(sdk, action)
        } else {
            Logger.w("create down channel failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "create down channel failed - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "create down channel failed - $reason").build().toString())
        }
    }
}

private fun postSynchronizeStateAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_SYNCHRONIZE_STATE).create()
    sdk.httpChannel.postEvent(event) { success, reason, response ->
        if (success) {
            Logger.d("postSynchronizeStateAction success - ${response!!.code}")
            postVerifyGateway(sdk, action)
        } else {
            Logger.w("postSynchronizeStateAction failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postSynchronizeStateAction - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postSynchronizeStateAction - $reason").build().toString())
        }
    }
}

private fun postVerifyGateway(sdk: SmartWatchSDK, action: ActionWrapper) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_ALEXA_API_GATEWAY, AlexaConst.NAME_VERIFY_GATEWAY).create()
    sdk.httpChannel.postEvent(event) { success, reason, response ->
        if (success) {
            Logger.d("postVerifyGateway success - ${response!!.code}")
            postAlexaDiscovery(sdk, action)
        } else {
            Logger.w("postVerifyGateway failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postVerifyGateway - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postVerifyGateway - $reason").build().toString())
        }
    }
}

private fun postAlexaDiscovery(sdk: SmartWatchSDK, action: ActionWrapper) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_ALEXA_DISCOVERY, AlexaConst.NAME_ADD_OR_UPDATE_REPORT).apply {
        addHeader("payloadVersion", "3")
        addHeader("eventCorrelationToken", makeMessageId())

        val scope: JsonObject = buildJsonObject {
            put("type","BearerToken")
            put("token", RuntimeInfo.accessToken)
        }
        addPayload("scope", scope)
        addPayload("endpoints", buildJsonArray {
            for (element in EndpointInfo.endpoints.values) {
                add(element)
            }
        })
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, response ->
        if (success) {
            Logger.d("postAlexaDiscovery success - ${response!!.code}")

            val result = ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).apply {
                val payload = buildJsonObject {
                    put("accessToken", RuntimeInfo.accessToken)
                    put("refreshToken", RuntimeInfo.refreshToken)
                }
                setPayload(payload)
            }.build()

            sdk.httpChannel.isLogin = true

            action.callback?.onResult(result.toString())
//            sdk.sdkCallback(SDKMessage.LOGIN_SUCCESS, null)
//            sdk.resultCallbackHook(action, result);

            // set Timer

        } else {
            sdk.httpChannel.isLogin = false

            Logger.w("postAlexaDiscovery failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postAlexaDiscovery - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postAlexaDiscovery - $reason").build().toString())
        }
    }
}

