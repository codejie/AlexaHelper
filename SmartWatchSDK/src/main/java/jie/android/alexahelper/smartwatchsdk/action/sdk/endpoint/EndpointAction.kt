package jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object EndpointAction {
    fun powerControllerSet(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = PowerController.set(sdk, action, callback)
}