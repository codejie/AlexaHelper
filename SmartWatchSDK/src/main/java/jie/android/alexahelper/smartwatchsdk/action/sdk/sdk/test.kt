package jie.android.alexahelper.smartwatchsdk.action.sdk.sdk

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst

fun testAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    sdk.toAction(action) { _ ->

    }
//    sdk.onActionListener.onAction(action.data.toString(), action.extra, object: OnResultCallback {
//        override fun onResult(data: String, extra: Any?) {
//            Logger.d("get action result - $data")
//        }
//    })
    val result = ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS)
    callback(result)
//    action.callback?.onResult(action.data.toString(), action.extra)
}