package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper

object AlexaAction {
    fun login(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = loginAction(sdk, false, action, callback)
    fun loginWithToken(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = loginAction(sdk, true, action, callback)
    fun setDND(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = setDNDAction(sdk, action, callback)
    fun speechStart(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speechStartAction(sdk, action, callback)
    fun speechEnd(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speechEndAction(sdk, action, callback)
    fun speechRecognize(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speechRecognizeAction(sdk, action, callback)
    fun speechExpectSkipped(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speechExpectSkippedAction(sdk, action, callback)
    fun speakStart(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speakStartAction(sdk, action, callback)
    fun speakEnd(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speakEndAction(sdk, action, callback)
    fun speakInterrupted(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = speakInterruptedAction(sdk, action, callback)
    fun setTimeZone(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = setTimeZoneAction(sdk,action, callback)
    fun setLocales(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = setLocalesAction(sdk, action, callback)
    fun alertStart(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = alertStartAction(sdk, action, callback)
    fun alertEnd(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = alertEndAction(sdk, action, callback)
    fun setVolume(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = setVolumeAction(sdk, action, callback)
    fun verifyGateway(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) = verifyGatewayAction(sdk, action, callback)
    // fun called by self
//    fun tokenUpdated(sdk: SmartWatchSDK, success: Boolean, message: String?, callback: OnResultCallback) = tokenUpdatedAction(sdk, success, message, callback)
}