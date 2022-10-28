package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object AlexaAction {
    fun login(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, false, action)
    fun loginWithToken(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, true, action)
    fun setDND(sdk: SmartWatchSDK, action: ActionWrapper) = setDNDAction(sdk, action)
    fun speechStart(sdk: SmartWatchSDK, action: ActionWrapper) = speechStartAction(sdk, action)
    fun speechStop(sdk: SmartWatchSDK, action: ActionWrapper) = speechStopAction(sdk, action)
    fun speechRecognize(sdk: SmartWatchSDK, action: ActionWrapper) = speechRecognizeAction(sdk, action)
    fun setTimeZone(sdk: SmartWatchSDK, action: ActionWrapper) = setTimeZoneAction(sdk,action)
    fun setLocales(sdk: SmartWatchSDK, action: ActionWrapper) = setLocalesAction(sdk, action)
    fun alertStart(sdk: SmartWatchSDK, action: ActionWrapper) = alertStartAction(sdk, action)
    fun alertEnd(sdk: SmartWatchSDK, action: ActionWrapper) = alertEndAction(sdk, action)

}