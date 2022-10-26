package jie.android.alexahelper.smartwatchsdk.protocol.sdk

open class SDKException constructor(val code: Int, message: String? = null): Exception(message) {
}

object SDKConst {

    // Action
    const val ACTION_SDK_EXCEPTION = "sdk.exception"
    const val ACTION_SDK_TEST = "sdk.test"

    const val ACTION_DEVICE_SET_INFO = "device.setInfo"

    const val ACTION_ALEXA_LOGIN = "alexa.login"
    const val ACTION_ALEXA_LOGIN_WITH_TOKEN = "alexa.loginWithToken"
    const val ACTION_ALEXA_SET_DND = "alexa.setDoNotDisturb"
    const val ACTION_ALEXA_CLEAR_INDICATOR = "alexa.clearIndicator"
    const val ACTION_ALEXA_SPEECH_START = "alexa.speechStart"
    const val ACTION_ALEXA_SPEECH_END = "alexa.speechEnd"
    const val ACTION_ALEXA_SPEECH_RECOGNIZE = "alexa.speechRecognize"
    const val ACTION_ALEXA_SET_TIME_ZONE = "alexa.setTimeZone"
    const val ACTION_ALEXA_SET_LOCALS = "alexa.setLocales"


    const val ACTION_ALEXA_DND_UPDATED = "alexa.doNotDisturbUpdated"
    const val ACTION_ALEXA_SPEECH_SPEAK = "alexa.speechSpeak"
    const val ACTION_ALEXA_TIME_ZONE_UPDATED = "alexa.timeZoneUpdated"
    const val ACTION_ALEXA_LOCALES_UPDATED = "alexa.localesUpdated"



    // Result
    const val RESULT_CODE_SUCCESS = 0
    const val RESULT_CODE_INVALID_FORMAT = -1
    const val RESULT_CODE_MISSING_FIELD = -2
    const val RESULT_CODE_MISSING_PARAMETERS = -3
    const val RESULT_CODE_ACTION_FAILED = -4
    const val RESULT_CODE_OFFLINE = -5

    const val RESULT_CODE_LOGIN_FAIL = -100

    const val RESULT_MESSAGE_INVALID_FORMAT = "invalid format"
    const val RESULT_MESSAGE_MISSING_FIELD = "missing field"
    const val RESULT_MESSAGE_MISSING_EXTRA = "missing extra"
    const val RESULT_MESSAGE_MISSING_PARAMETERS = "missing parameters"
    const val RESULT_MESSAGE_OFFLINE = "not online"
}