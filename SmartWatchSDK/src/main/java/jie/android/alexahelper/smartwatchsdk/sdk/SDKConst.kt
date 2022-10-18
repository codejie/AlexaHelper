package jie.android.alexahelper.smartwatchsdk.sdk

open class SDKException constructor(val code: Int, message: String? = null): Exception(message) {
}

object SDKConst {
    const val ACTION_SDK_EXCEPTION = "sdk.exception"
    const val ACTION_DEVICE_SET_INFO = "device.setInfo"
    const val ACTION_ALEXA_LOGIN = "alexa.login"


    const val RESULT_CODE_SUCCESS = 0
    const val RESULT_CODE_INVALID_FORMAT = -1
    const val RESULT_CODE_MISSING_FIELD = -2
    const val RESULT_CODE_MISSING_PARAMETERS = -3
    const val RESULT_CODE_ACTION_FAILED = -4

    const val RESULT_CODE_LOGIN_FAIL = -100

    const val RESULT_MESSAGE_INVALID_FORMAT = "invalid format"
    const val RESULT_MESSAGE_MISSING_FIELD = "missing field"
    const val RESULT_MESSAGE_MISSING_PARAMETERS = "missing parameters"
}