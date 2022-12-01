package jie.android.alexahelper.smartwatchsdk.action.sdk.sdk

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object SDKAction {
    fun test(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = testAction(sdk, action, callback)
    fun getInfo(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = getInfoAction(sdk, action, callback)
}