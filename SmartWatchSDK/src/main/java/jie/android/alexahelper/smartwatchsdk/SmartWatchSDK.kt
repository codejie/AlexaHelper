package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.action.device.DeviceAction
import jie.android.alexahelper.smartwatchsdk.sdk.*
import kotlinx.serialization.SerializationException


class SmartWatchSDK constructor() {
    private lateinit var requestContext: RequestContext

    private lateinit var onActionListener: OnActionListener;

    fun attach(context: Context, actionListener: OnActionListener) {
        requestContext = RequestContext.create(context)
        onActionListener = actionListener
    }

    fun detach(context: Context) {
    }

    fun resume(context: Context) {
        requestContext.onResume()
    }

    fun action(data: String, extra: Any?, callback: OnResultCallback) {
//        this.onActionListener.onAction(data, extra, object: OnResultCallback {
//            override fun onResult(data: String, extra: Any?) {
//                Log.d("TAG", "$data")
//            }
//        })
//        callback.onResult(data, extra)

        try {
            val action: ActionWrapper = ActionWrapper.parse(data, extra, callback)
            when (action.name) {
                SDKConst.ACTION_DEVICE_SET_INFO -> DeviceAction.setInfo(action)
                SDKConst.ACTION_ALEXA_LOGIN -> AlexaAction.login(requestContext, action) // onActionLogin(action)
                else -> throw SDKException(
                    SDKConst.RESULT_CODE_INVALID_FORMAT,
                    SDKConst.RESULT_MESSAGE_INVALID_FORMAT
                )
            }
        } catch (e: SDKException) {
            callback.onResult(ResultWrapper(SDKConst.ACTION_SDK_EXCEPTION, e.code, e.message).build().toString())
        } catch (e: SerializationException) {
            callback.onResult(ResultWrapper(SDKConst.ACTION_SDK_EXCEPTION, SDKConst.RESULT_CODE_SUCCESS).build().toString())
        }
    }
//
//    private fun onActionLogin(action: ActionWrapper) {
//        if (DeviceInfo.Product.id == null || DeviceInfo.Product.serialNumber == null) {
//            throw throw SDKException(
//                SDKConst.RESULT_CODE_MISSING_PARAMETERS,
//                SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
//            )
//        }
//
//        requestContext.registerListener(object: AuthorizeListener() {
//            override fun onSuccess(result: AuthorizeResult?) {
//                onAuthorizeSuccess(action, result)
//            }
//
//            override fun onError(error: AuthError?) {
//                onAuthorizeFailed(action, error?.message)
//            }
//
//            override fun onCancel(result: AuthCancellation?) {
//                onAuthorizeFailed(action, result.toString())
//            }
//        })
//
//        RuntimeInfo.verifierCode = UUID.randomUUID().toString()
//        val challengeCode = makeCodeChallenge(RuntimeInfo.verifierCode!!)
//        val scopeData = buildJsonObject {
//            putJsonObject("productInstanceAttributes") {
//                put("deviceSerialNumber", DeviceInfo.Product.serialNumber)
//            }
//            put("productID", DeviceInfo.Product.id)
//        }
//
//        AuthorizationManager.authorize(
//            AuthorizeRequest.Builder(requestContext)
//            .addScopes(ScopeFactory.scopeNamed("alexa:voice_service:pre_auth"), ScopeFactory.scopeNamed("alexa:all", JSONObject(scopeData.toString())))
//            .forGrantType(AuthorizeRequest.GrantType.AUTHORIZATION_CODE)
//            .withProofKeyParameters(challengeCode, "S256")
//            .build())
//    }
//
//    private fun onAuthorizeFailed(action: ActionWrapper, message: String?) {
//        action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_LOGIN_FAIL, message).build().toString())
//    }
//
//    private fun onAuthorizeSuccess(action: ActionWrapper, result: AuthorizeResult?) {
//        RuntimeInfo.authorizeCode = result?.authorizationCode
//        RuntimeInfo.redirectUri = result?.redirectURI
//
//
//
//    }
//
//    private fun onActionDeviceSetInfo(action: ActionWrapper) {
//        val payload: JsonObject = action.getPayload()!!
//
//        val product = payload.getJsonObject("payload")!!
//        DeviceInfo.Product.id = product.getString("id")
//        DeviceInfo.Product.clientId = product.getString("clientId")
//        DeviceInfo.Product.serialNumber = product.getString("serialNumber")
//        DeviceInfo.Product.name = product.getString("name")
//        DeviceInfo.Product.friendlyName = product.getString("friendlyName", false)
//        DeviceInfo.Product.description = product.getString("description", false)
//
//        val manufacturer = payload.getJsonObject("manufacturer")!!
//        DeviceInfo.Manufacturer.name = manufacturer.getString("name")
//        DeviceInfo.Manufacturer.model = manufacturer.getString("model")
//        DeviceInfo.Manufacturer.firmware = manufacturer.getString("firmware")
//        DeviceInfo.Manufacturer.software = manufacturer.getString("software")
//
//        action.callback?.onResult(ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString())
//    }

}