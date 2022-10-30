package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.device.DeviceAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.sdk.SDKAction
import jie.android.alexahelper.smartwatchsdk.channel.alexa.ChannelPostCallback
import jie.android.alexahelper.smartwatchsdk.channel.alexa.HttpChannel
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKChannel
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.utils.SDKScheduler
import kotlinx.serialization.SerializationException
import okhttp3.Response

class SmartWatchSDK constructor() {
    internal lateinit var requestContext: RequestContext
    internal lateinit var onActionListener: OnActionListener;

    internal val httpChannel: HttpChannel = HttpChannel(this)

    internal val sdkChannel: SDKChannel = SDKChannel(this)
    internal val sdkScheduler: SDKScheduler = SDKScheduler(this)

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
            Logger.d("sdk action() - $data")
            val action: ActionWrapper = ActionWrapper.parse(data, extra, callback)
            when (action.name) {
//                SDKConst.ACTION_ALEXA_SPEECH_RECOGNIZE -> AlexaAction.speechRecognize(this, action)
                SDKConst.ACTION_SDK_TEST -> SDKAction.test(this, action)
                SDKConst.ACTION_DEVICE_SET_INFO -> DeviceAction.setInfo(this, action)
                SDKConst.ACTION_ALEXA_LOGIN -> AlexaAction.login(this, action) // onActionLogin(action)
                SDKConst.ACTION_ALEXA_LOGIN_WITH_TOKEN -> AlexaAction.loginWithToken(this, action)
                SDKConst.ACTION_ALEXA_SET_DND -> AlexaAction.setDND(this, action)
                SDKConst.ACTION_ALEXA_SPEECH_START -> AlexaAction.speechStart(this, action)
                SDKConst.ACTION_ALEXA_SPEECH_END -> AlexaAction.speechStop(this, action)
                SDKConst.ACTION_ALEXA_SPEECH_RECOGNIZE -> AlexaAction.speechRecognize(this, action)
                SDKConst.ACTION_ALEXA_SET_TIME_ZONE -> AlexaAction.setTimeZone(this, action)
                SDKConst.ACTION_ALEXA_SET_LOCALS -> AlexaAction.setLocales(this, action)
                SDKConst.ACTION_ALEXA_ALERT_START -> AlexaAction.alertStart(this, action)
                SDKConst.ACTION_ALEXA_ALERT_END -> AlexaAction.alertEnd(this, action)
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

    fun refreshToken() {
        Logger.d("refreshToken.")

        RuntimeInfo.refreshToken?.let {
            httpChannel.postRefreshAccessToken(it) { success, reason, _ ->
                AlexaAction.tokenUpdated(this, success, reason, object : OnResultCallback {
                    override fun onResult(data: String, extra: Any?) {
                        Logger.d("token updated result - $data")
                    }
                })
            }
        }
    }

    fun pingDownChannel() {
        Logger.d("ping down channel.")

        RuntimeInfo.accessToken?.let {
            httpChannel.postDownChannelPing(it) { success, reason, _ ->
                if (!success) {
                    Logger.w("down channel ping failed - $reason")
                } else {
                    RuntimeInfo.downChannelPingTimer = sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
                }
            }
        }

//        RuntimeInfo.downChannelPingTimer = sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
    }

    fun onDownChannelBreak() {
        sdkScheduler.removeTimer(RuntimeInfo.downChannelPingTimer)

        httpChannel.getDownChannel { success, reason, response ->
            if (success) {
                Logger.d("down channel recreate.")
//                RuntimeInfo.downChannelPingTimer = sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
            } else {
                Logger.w("down channel recreate failed - $reason")
            }
        }
    }

}