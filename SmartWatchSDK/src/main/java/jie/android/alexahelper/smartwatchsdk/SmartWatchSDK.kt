package jie.android.alexahelper.smartwatchsdk

import android.content.Context
import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.channel.alexa.HttpChannel
import jie.android.alexahelper.smartwatchsdk.channel.sdk.SDKChannel
import jie.android.alexahelper.smartwatchsdk.channel.sdk.onAction
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import jie.android.alexahelper.smartwatchsdk.utils.SDKScheduler
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal typealias ActionResultCallback = (result: ResultWrapper) -> Unit

class SmartWatchSDK constructor() {
    internal lateinit var requestContext: RequestContext
    private lateinit var onActionListener: OnActionListener;

    internal val httpChannel: HttpChannel = HttpChannel(this)

    internal val sdkChannel: SDKChannel = SDKChannel(this)
    internal val sdkScheduler: SDKScheduler = SDKScheduler(this)

    fun attach(context: Context, actionListener: OnActionListener): Boolean {

//        if (!DeviceInfo.inited) return false

        requestContext = RequestContext.create(context)
        onActionListener = actionListener

        sdkChannel.start()
        sdkScheduler.start()

        return true
    }

    fun detach(context: Context) {
        sdkScheduler.stop()
        sdkChannel.stop()
    }

    fun resume(context: Context) {
        requestContext.onResume()
    }

    fun action(data: String, extra: Any?, callback: OnResultCallback?) {
        Logger.d("sdk action() - $data")
        try {
            val action: ActionWrapper = ActionWrapper.parse(data, extra, callback)
            onAction(this, action) { result ->
                val data = result.build().toString()
                Logger.d("sdk action() result - $data")
                action.callback?.onResult(data, result.extra)
            }
        } catch (e: SerializationException) {
            Logger.w("sdk action() exception - ${e.message}")
            callback?.onResult(ResultWrapper(SDKConst.ACTION_SDK_EXCEPTION, SDKConst.RESULT_CODE_INVALID_FORMAT).build().toString())
        } catch (e: IllegalArgumentException) {
            Logger.w("sdk action() exception - ${e.message}")
            callback?.onResult(ResultWrapper(SDKConst.ACTION_SDK_EXCEPTION, SDKConst.RESULT_CODE_INVALID_FORMAT).build().toString())
        }
    }

    fun toAction(action: ActionWrapper, callback: ActionResultCallback) {
        try {
            val data = action.build().toString()
            Logger.d("sdk toAction() - $data")
            onActionListener.onAction(data, action.extra, object : OnResultCallback {
                override fun onResult(data: String, extra: Any?) {
                    Logger.d("sdk toAction() result - $data")
                    try {
                        val result = ResultWrapper.parse(data, extra)
                        callback(result)
                    } catch (e: Exception) {
                        Logger.w("${action.name} result parse failed - ${e.message}")
                    }
                }
            })
        } catch (e: Exception) {
            Logger.w("${action.name} to failed - ${e.message}")
        }
    }

    fun refreshToken() {
        Logger.d("refreshToken.")

        RuntimeInfo.refreshToken?.let {
            httpChannel.postRefreshAccessToken(it) { success, reason, _ ->
                val action = ActionWrapper(SDKConst.ACTION_ALEXA_TOKEN_UPDATED).apply {
                    val payload = buildJsonObject {
                        put("accessToken", RuntimeInfo.accessToken)
                        put("refreshToken", RuntimeInfo.refreshToken)
                    }
                    setPayload(payload)
                }
                this.toAction(action) { _ ->
                }
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
            } else {
                Logger.w("down channel recreate failed - $reason")
                sdkScheduler.addTimer(SDKScheduler.Timer(1000, false, SDKScheduler.TimerType.DOWN_CHANNEL_CREATE))
            }
        }
    }

}