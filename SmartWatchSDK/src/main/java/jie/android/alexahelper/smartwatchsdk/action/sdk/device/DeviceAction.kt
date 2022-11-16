package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback

object DeviceAction {
    fun setInfo(sdk: SmartWatchSDK, action: ActionWrapper) = setInfoAction(sdk, action)
    fun syncState(sdk: SmartWatchSDK, action: ActionWrapper) = syncStateAction(sdk, action)
}