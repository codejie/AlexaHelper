package jie.android.alexahelper.smartwatchsdk.action.alexa

import com.amazon.identity.auth.device.api.workflow.RequestContext
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.sdk.ActionWrapper

object AlexaAction {
    fun login(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, false, action)
    fun loginWithToken(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, true, action)
}