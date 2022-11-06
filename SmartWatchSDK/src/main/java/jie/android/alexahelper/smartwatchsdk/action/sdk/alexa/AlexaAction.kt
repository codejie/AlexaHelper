package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback

object AlexaAction {
    fun login(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, false, action)
    fun loginWithToken(sdk: SmartWatchSDK, action: ActionWrapper) = loginAction(sdk, true, action)
    fun setDND(sdk: SmartWatchSDK, action: ActionWrapper) = setDNDAction(sdk, action)
    fun speechStart(sdk: SmartWatchSDK, action: ActionWrapper) = speechStartAction(sdk, action)
    fun speechEnd(sdk: SmartWatchSDK, action: ActionWrapper) = speechEndAction(sdk, action)
    fun speechRecognize(sdk: SmartWatchSDK, action: ActionWrapper) = speechRecognizeAction(sdk, action)
    fun speechExpectSkipped(sdk: SmartWatchSDK, action: ActionWrapper) = speechExpectSkippedAction(sdk, action)
    fun speakStart(sdk: SmartWatchSDK, action:ActionWrapper) = speakStartAction(sdk, action)
    fun speakEnd(sdk: SmartWatchSDK, action:ActionWrapper) = speakEndAction(sdk, action)
    fun speakInterrupted(sdk: SmartWatchSDK, action: ActionWrapper) = speakInterruptedAction(sdk, action)
    fun setTimeZone(sdk: SmartWatchSDK, action: ActionWrapper) = setTimeZoneAction(sdk,action)
    fun setLocales(sdk: SmartWatchSDK, action: ActionWrapper) = setLocalesAction(sdk, action)
    fun alertStart(sdk: SmartWatchSDK, action: ActionWrapper) = alertStartAction(sdk, action)
    fun alertEnd(sdk: SmartWatchSDK, action: ActionWrapper) = alertEndAction(sdk, action)

    fun tokenUpdated(sdk: SmartWatchSDK, success: Boolean, message: String?, callback: OnResultCallback) = tokenUpdatedAction(sdk, success, message, callback)

}