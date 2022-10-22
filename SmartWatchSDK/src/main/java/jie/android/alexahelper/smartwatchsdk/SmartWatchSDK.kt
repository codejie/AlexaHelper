package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.device.DeviceAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.sdk.SDKAction
import jie.android.alexahelper.smartwatchsdk.channel.alexa.HttpChannel
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKChannel
import jie.android.alexahelper.smartwatchsdk.utils.SDKScheduler
import kotlinx.serialization.SerializationException

enum class SDKMessage {
    LOGIN_SUCCESS
}

typealias ResultCallbackHook = (action: ActionWrapper, result: ResultWrapper) -> Unit

class SmartWatchSDK constructor() {
    internal lateinit var requestContext: RequestContext
    internal lateinit var onActionListener: OnActionListener;

    internal val httpChannel: HttpChannel = HttpChannel(this)

    internal val sdkChannel: SDKChannel = SDKChannel(this)
    internal val sdkScheduler: SDKScheduler = SDKScheduler(this)

    internal val resultCallbackHook: ResultCallbackHook = { action, result ->
        when (result.name) {
            SDKConst.ACTION_ALEXA_LOGIN,
            SDKConst.ACTION_ALEXA_LOGIN_WITH_TOKEN -> resultLogin(action, result)
            else -> {
                Logger.d("Not been hooked action - ${action.name}")
                action.callback?.onResult(result.build().toString(), result.extra)
            }
        }
    }

    fun attach(context: Context, actionListener: OnActionListener) {
        requestContext = RequestContext.create(context)
        onActionListener = actionListener

        sdkChannel.start()
        sdkScheduler.start()

//        sdkScheduler.addTimer(SDKScheduler.Timer(2000, true, "timer1"))
//        sdkScheduler.addTimer(SDKScheduler.Timer(4000, false, "timer2"))
    }

    fun detach(context: Context) {
        sdkScheduler.stop()
        sdkChannel.stop()
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

    private fun resultLogin(action: ActionWrapper, result: ResultWrapper) {
        Logger.d("login action result - ${result.code}")
        action.callback?.onResult(result.build().toString(), result.extra)
    }


}