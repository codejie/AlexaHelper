package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import com.amazon.identity.auth.device.AuthError
import com.amazon.identity.auth.device.api.authorization.*
import jie.android.alexahelper.smartwatchsdk.*
import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.channel.alexa.makeCodeChallenge
import jie.android.alexahelper.smartwatchsdk.channel.sdk.ChannelData
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKNotification
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Utils.makeMessageId
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.*
import org.json.JSONObject
import java.util.*

fun loginAction(
    sdk: SmartWatchSDK,
    withToken: Boolean,
    action: ActionWrapper,
    callback: ActionResultCallback
) {
    if (withToken) {
        authorizeWithToken(sdk, action, callback)
    } else {
        authorize(sdk, action, callback)
    }
}

private fun authorize(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    if (DeviceInfo.productInfo.id == null || DeviceInfo.productInfo.serialNumber == null) {
        throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        )
    }

    sdk.requestContext.registerListener(object: AuthorizeListener() {
        override fun onSuccess(result: AuthorizeResult?) {
            onAuthorizeSuccess(sdk, action, result, callback)
        }

        override fun onError(error: AuthError?) {
            onAuthorizeFailed(sdk, action, error?.message, callback)
        }

        override fun onCancel(result: AuthCancellation?) {
            onAuthorizeFailed(sdk, action, result.toString(), callback)
        }
    })

    RuntimeInfo.verifierCode = UUID.randomUUID().toString()
    val challengeCode = makeCodeChallenge(RuntimeInfo.verifierCode!!)
    val scopeData = buildJsonObject {
        putJsonObject("productInstanceAttributes") {
            put("deviceSerialNumber", DeviceInfo.productInfo.serialNumber)
        }
        put("productID", DeviceInfo.productInfo.id)
    }

    AuthorizationManager.authorize(
        AuthorizeRequest.Builder(sdk.requestContext)
            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
            .withProofKeyParameters(challengeCode, "S256")
            .build())
}

private fun onAuthorizeFailed(sdk: SmartWatchSDK, action: ActionWrapper, message: String?, callback: ActionResultCallback) {
    callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_LOGIN_FAIL, message))
//    action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_LOGIN_FAIL, message).build().toString())
}

private fun onAuthorizeSuccess(sdk: SmartWatchSDK, action: ActionWrapper, result: AuthorizeResult?, callback: ActionResultCallback) {
    RuntimeInfo.authorizationCode = result?.authorizationCode
    RuntimeInfo.redirectUri = result?.redirectURI

    fetchAuthorizeToken(sdk, action, callback)
}

private fun authorizeWithToken(
    sdk: SmartWatchSDK,
    action: ActionWrapper,
    callback: ActionResultCallback
) {
    val payload: JsonObject = action.data!!.getJsonObject("payload")!!;
    val refreshToken = payload.getString("refreshToken")
        ?: throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        );

    sdk.httpChannel.postRefreshAccessToken(refreshToken) { success, reason, _ ->
        if (success) {
            createDownChannel(sdk, action, callback);
        } else {
            Logger.w("authorize token failed - $reason")
            callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason"))
//            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason").build().toString())
        }
    }
}

private fun fetchAuthorizeToken(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    sdk.httpChannel.postAuthorize { success, reason, response ->
        if (success) {
            // create down channel
            Logger.d("fetchAuthorizeToken success - ${response!!.code}")
            createDownChannel(sdk, action, callback)
        } else {
            Logger.w("authorize token failed - $reason")
            callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason"))
//            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "authorize token failed - $reason").build().toString())
        }
    }
}

private fun createDownChannel(
    sdk: SmartWatchSDK,
    action: ActionWrapper,
    callback: ActionResultCallback
): Unit {
    sdk.httpChannel.getDownChannel { success, reason, _ ->
        if (success) {
            // synchronize state
            Logger.d("createDownChannel success")
//            postSynchronizeStateAction(sdk, action)
            postVerifyGateway(sdk, action, callback)
        } else {
            Logger.w("create down channel failed - $reason")
            callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "create down channel failed - $reason"))
//            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "create down channel failed - $reason").build().toString())
        }
    }
}

private fun postSynchronizeStateAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_SYNCHRONIZE_STATE).apply {
        setContext(DeviceInfo.stateInfo.makeContext())
    }.create()
    sdk.httpChannel.postEvent(event) { success, reason, response ->
        if (success) {
            Logger.d("postSynchronizeStateAction success - ${response!!.code}")
//            postVerifyGateway(sdk, action)
            postAlexaDiscovery(sdk, action, callback)
        } else {
            Logger.w("postSynchronizeStateAction failed - $reason")
            callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postSynchronizeStateAction - $reason"))
//            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postSynchronizeStateAction - $reason").build().toString())
        }
    }
}

private fun postVerifyGateway(
    sdk: SmartWatchSDK,
    action: ActionWrapper,
    callback: ActionResultCallback
) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_ALEXA_API_GATEWAY, AlexaConst.NAME_VERIFY_GATEWAY).create()
    sdk.httpChannel.postEvent(event) { success, reason, response ->
        if (success) {
            Logger.d("postVerifyGateway success - ${response!!.code}")
//            postAlexaDiscovery(sdk, action)
            postSynchronizeStateAction(sdk, action, callback)
        } else {
            Logger.w("postVerifyGateway failed - $reason")
            callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postVerifyGateway - $reason"))
//            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postVerifyGateway - $reason").build().toString())
        }
    }
}

private fun postAlexaDiscovery(
    sdk: SmartWatchSDK,
    action: ActionWrapper,
    callback: ActionResultCallback
) {
    val event: JsonObject = EventBuilder(AlexaConst.NS_ALEXA_DISCOVERY, AlexaConst.NAME_ADD_OR_UPDATE_REPORT).apply {
        addHeader("payloadVersion", "3")
        addHeader("eventCorrelationToken", makeMessageId())

        val scope: JsonObject = buildJsonObject {
            put("type","BearerToken")
            put("token", RuntimeInfo.accessToken)
        }
        addPayload("scope", scope)
        DeviceInfo.makeEndpointList()?.let { addPayload("endpoints", it) } // .productInfo.makeEndPoints()!!) // .endpointInfo.makeList())
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
            }

//            sdk.httpChannel.isLogin = true
            DeviceInfo.isLogin = true

            CoroutineScope(Dispatchers.IO).launch {
                sdk.sdkChannel.send(
                    ChannelData(
                        ChannelData.DataType.Notification,
                        SDKNotification.Message.LOGIN_SUCCESS
                    )
                )
            }
            callback(result)
//            action.callback?.onResult(result.toString())
        } else {
//            sdk.httpChannel.isLogin = false
            sdk.httpChannel.closeDownChannel()
            DeviceInfo.isLogin = false

            Logger.w("postAlexaDiscovery failed - $reason")
//            sdk.resultCallbackHook(action, ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postAlexaDiscovery - $reason"))
            action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_ACTION_FAILED, "postAlexaDiscovery - $reason").build().toString())
        }
    }
}

//fun tokenUpdatedAction(sdk: SmartWatchSDK, success: Boolean, message: String?, callback: OnResultCallback) {
//    val action = ActionWrapper(SDKConst.ACTION_ALEXA_TOKEN_UPDATED).apply {
//        val payload = buildJsonObject {
//            put("accessToken", RuntimeInfo.accessToken)
//            put("refreshToken", RuntimeInfo.refreshToken)
//        }
//        setPayload(payload)
//    }
//
//    sdk.toAction(action) { _ ->
//
//    }
//}
