package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.channel.alexa.HttpChannel
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKChannel
import jie.android.alexahelper.smartwatchsdk.channel.sdk.onAction
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnActionListener
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.utils.SDKScheduler

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
    }

    fun detach(context: Context) {
        sdkScheduler.stop()
        sdkChannel.stop()
    }

    fun resume(context: Context) {
        requestContext.onResume()
    }

    fun action(data: String, extra: Any?, callback: OnResultCallback?) {
        onAction(this, data, extra, callback)
    }

    fun refreshToken() {
        Logger.d("refreshToken.")

        RuntimeInfo.refreshToken?.let {
            httpChannel.postRefreshAccessToken(it) { success, reason, _ ->
                AlexaAction.tokenUpdated(this, success, reason, object : OnResultCallback {
                    override fun onResult(data: String, extra: Any?) {
                        Logger.v("token updated result - $data")
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
                    Logger.v("down channel ping")
                }
            }
        }

//        RuntimeInfo.downChannelPingTimer = sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
    }

    fun onDownChannelBreak() {
        sdkScheduler.removeTimer(RuntimeInfo.downChannelPingTimer)

        httpChannel.getDownChannel { success, reason, response ->
            if (success) {
                Logger.v("down channel recreate.")
//                RuntimeInfo.downChannelPingTimer = sdkScheduler.addTimer(SDKScheduler.Timer(280 * 1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_PING))
            } else {
                Logger.w("down channel recreate failed - $reason")
            }
        }
    }

}