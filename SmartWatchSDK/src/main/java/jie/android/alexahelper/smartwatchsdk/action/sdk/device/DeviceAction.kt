package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object DeviceAction {
    fun setInfo(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = setInfoAction(sdk, action, callback)
    fun syncState(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = syncStateAction(sdk, action, callback)
}