package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object AlexaAction {
    fun login(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, false, action)
    fun loginWithToken(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, true, action)
    fun setDND(sdk: SmartWatchSDK, action: ActionWrapper) = setDNDAction(sdk, action)
}