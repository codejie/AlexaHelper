package jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object EndpointAction {
    fun powerControllerSyncState(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = PowerController.syncState(sdk, action, callback)
}