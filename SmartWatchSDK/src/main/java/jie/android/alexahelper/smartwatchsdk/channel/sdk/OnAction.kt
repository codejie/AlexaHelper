package jie.android.alexahelper.smartwatchsdk.channel.sdk

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.device.DeviceAction
import jie.android.alexahelper.smartwatchsdk.action.sdk.sdk.SDKAction
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*

internal fun onAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {

    try {
        when (action.name) {
    //               SDKConst.ACTION_ALEXA_SPEECH_RECOGNIZE -> AlexaAction.speechRecognize(sdk, action)
            SDKConst.ACTION_SDK_TEST -> SDKAction.test(sdk, action, callback)
            SDKConst.ACTION_DEVICE_SET_INFO -> DeviceAction.setInfo(sdk, action, callback)
            SDKConst.ACTION_DEVICE_SYNC_STATE -> DeviceAction.syncState(sdk, action, callback)
            SDKConst.ACTION_ALEXA_LOGIN -> AlexaAction.login(sdk, action, callback) // onActionLogin(action)
            SDKConst.ACTION_ALEXA_LOGIN_WITH_TOKEN -> AlexaAction.loginWithToken(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SET_DND -> AlexaAction.setDND(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEECH_START -> AlexaAction.speechStart(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEECH_END -> AlexaAction.speechEnd(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEECH_RECOGNIZE -> AlexaAction.speechRecognize(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEECH_EXPECT_SKIPPED -> AlexaAction.speechExpectSkipped(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEAK_START -> AlexaAction.speakStart(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEAK_END -> AlexaAction.speakEnd(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SPEAK_INTERRUPTED -> AlexaAction.speakInterrupted(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SET_TIME_ZONE -> AlexaAction.setTimeZone(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SET_LOCALS -> AlexaAction.setLocales(sdk, action, callback)
            SDKConst.ACTION_ALEXA_ALERT_START -> AlexaAction.alertStart(sdk, action, callback)
            SDKConst.ACTION_ALEXA_ALERT_END -> AlexaAction.alertEnd(sdk, action, callback)
            SDKConst.ACTION_ALEXA_SET_VOLUME -> AlexaAction.setVolume(sdk, action, callback)
            SDKConst.ACTION_ALEXA_VERIFY_GATEWAY -> AlexaAction.verifyGateway(sdk, action, callback)
            else -> throw SDKException(
                SDKConst.RESULT_CODE_INVALID_FORMAT,
                SDKConst.RESULT_MESSAGE_INVALID_FORMAT
            )
        }
    } catch (e: SDKException) {
        callback(ResultWrapper(SDKConst.ACTION_SDK_EXCEPTION, e.code, e.message))
    }
}