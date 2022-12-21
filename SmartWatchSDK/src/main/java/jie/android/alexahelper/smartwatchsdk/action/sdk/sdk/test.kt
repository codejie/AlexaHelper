package jie.android.alexahelper.smartwatchsdk.action.sdk.sdk

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst

fun testAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    sdk.toAction(action) { _ ->

    }

    val result = ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS)
    callback(result)
}