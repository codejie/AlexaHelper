package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.action.device.DeviceAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.SDKAction
import jie.android.alexahelper.smartwatchsdk.sdk.*
import kotlinx.serialization.SerializationException

enum class SDKMessage {
    LOGIN_SUCCESS
}
typealias SDKCallback = (what: SDKMessage, extra: Any?) -> Unit

class SmartWatchSDK constructor() {
    internal lateinit var requestContext: RequestContext
    internal lateinit var onActionListener: OnActionListener;

    internal val sdkCallback: SDKCallback = { what: SDKMessage, extra: Any? ->
        when (what) {

            else -> {}
        }
    }

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
        try {
            val action: ActionWrapper = ActionWrapper.parse(data, extra, callback)
            when (action.name) {
                SDKConst.ACTION_SDK_TEST -> SDKAction.test(this, action)
                SDKConst.ACTION_DEVICE_SET_INFO -> DeviceAction.setInfo(this, action)
                SDKConst.ACTION_ALEXA_LOGIN -> AlexaAction.login(this, action) // onActionLogin(action)
                SDKConst.ACTION_ALEXA_LOGIN_WITH_TOKEN -> AlexaAction.loginWithToken(this, action)
                SDKConst.ACTION_SET_DND -> AlexaAction.setDND(this, action)
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

}