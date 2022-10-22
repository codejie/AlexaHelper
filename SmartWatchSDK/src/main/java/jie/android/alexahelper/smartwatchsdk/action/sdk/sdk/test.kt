package jie.android.alexahelper.smartwatchsdk.action.sdk.sdk

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback
import jie.android.alexahelper.smartwatchsdk.utils.Logger

fun testAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    sdk.onActionListener.onAction(action.data.toString(), action.extra, object: OnResultCallback {
        override fun onResult(data: String, extra: Any?) {
            Logger.d("get action result - $data")
        }
    })
    action.callback?.onResult(action.data.toString(), action.extra)
}