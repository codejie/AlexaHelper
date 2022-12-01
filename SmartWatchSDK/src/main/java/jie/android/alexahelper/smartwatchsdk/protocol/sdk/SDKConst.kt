package jie.android.alexahelper.smartwatchsdk.protocol.sdk

open class SDKException constructor(val code: Int, message: String? = null): Exception(message) {
}

object SDKConst {

    // App Action
    const val ACTION_SDK_GET_INFO = "sdk.getInfo"
    const val ACTION_SDK_EXCEPTION = "sdk.exception"
    const val ACTION_SDK_TEST = "sdk.test"

    const val ACTION_DEVICE_SET_INFO = "device.setInfo"
    const val ACTION_DEVICE_SYNC_STATE = "device.syncState"

    const val ACTION_ALEXA_LOGIN = "alexa.login"
    const val ACTION_ALEXA_LOGIN_WITH_TOKEN = "alexa.loginWithToken"
    const val ACTION_ALEXA_SET_DND = "alexa.setDoNotDisturb"
    const val ACTION_ALEXA_CLEAR_INDICATOR = "alexa.clearIndicator"
    const val ACTION_ALEXA_SPEECH_START = "alexa.speechStart"
    const val ACTION_ALEXA_SPEECH_END = "alexa.speechEnd"
    const val ACTION_ALEXA_SPEECH_RECOGNIZE = "alexa.speechRecognize"
    const val ACTION_ALEXA_SPEECH_EXPECT_SKIPPED = "alexa.speechExpectSkipped"
    const val ACTION_ALEXA_SPEAK_START = "alexa.speakStart"
    const val ACTION_ALEXA_SPEAK_END = "alexa.speakEnd"
    const val ACTION_ALEXA_SPEAK_INTERRUPTED = "alexa.speakInterrupted"
    const val ACTION_ALEXA_SET_TIME_ZONE = "alexa.setTimeZone"
    const val ACTION_ALEXA_SET_LOCALS = "alexa.setLocales"
    const val ACTION_ALEXA_SET_VOLUME = "alexa.setVolume"
    const val ACTION_ALEXA_VERIFY_GATEWAY = "alexa.verifyGateway"

    const val ACTION_ENDPOINT_STATE_UPDATED = "endpoint.stateUpdated"
    const val ACTION_ENDPOINT_SYNC_STATE = "endpoint.syncState"
    const val ACTION_ENDPOINT_STATE_EXPECTED = "endpoint.stateExpected"


    // Alexa Action
    const val ACTION_ALEXA_TOKEN_UPDATED = "alexa.tokenUpdated"
    const val ACTION_ALEXA_DND_UPDATED = "alexa.doNotDisturbUpdated"
    const val ACTION_ALEXA_SPEECH_SPEAK = "alexa.speechSpeak"
    const val ACTION_ALEXA_SPEECH_EXPECTED = "alexa.speechExpected"
    const val ACTION_ALEXA_SPEECH_STOP = "alexa.speechStop"
    const val ACTION_ALEXA_TIME_ZONE_UPDATED = "alexa.timeZoneUpdated"
    const val ACTION_ALEXA_LOCALES_UPDATED = "alexa.localesUpdated"
    const val ACTION_ALEXA_ALERT_ADDED = "alexa.alertAdded"
    const val ACTION_ALEXA_ALERT_DELETED = "alexa.alertDeleted"
    const val ACTION_ALEXA_ALERT_START = "alexa.alertStart"
    const val ACTION_ALEXA_ALERT_END = "alexa.alertEnd"
    const val ACTION_ALEXA_VOLUME_UPDATED = "alexa.volumeUpdated"
    const val ACTION_ALEXA_SETTING_EXPECTED = "alexa.settingExpected"
    // template
    const val ACTION_ALEXA_TEMPLATE_CARD = "alexa.template.card"
    const val ACTION_ALEXA_TEMPLATE_LIST = "alexa.template.list"
    const val ACTION_ALEXA_TEMPLATE_WEATHER = "alexa.template.weather"
    // endpoint
    const val ACTION_EP_POWER_CONTROLLER_STATE_UPDATED = "ep.powerController.stateUpdated"

    // Result
    const val RESULT_CODE_SUCCESS = 0
    const val RESULT_CODE_INVALID_FORMAT = -1
    const val RESULT_CODE_MISSING_FIELD = -2
    const val RESULT_CODE_MISSING_PARAMETERS = -3
    const val RESULT_CODE_ACTION_FAILED = -4
    const val RESULT_CODE_OFFLINE = -5
    const val RESULT_CODE_NOT_SUPPORTED = -6
    const val RESULT_CODE_ENDPOINT_UNKNOWN_STATE = -7
    const val RESULT_CODE_ENDPOINT_NOT_FOUND = -8

    const val RESULT_CODE_LOGIN_FAIL = -100

    const val RESULT_MESSAGE_INVALID_FORMAT = "invalid format"
    const val RESULT_MESSAGE_MISSING_FIELD = "missing field"
    const val RESULT_MESSAGE_MISSING_EXTRA = "missing extra"
    const val RESULT_MESSAGE_MISSING_PARAMETERS = "missing parameters"
    const val RESULT_MESSAGE_OFFLINE = "not online"
    const val RESULT_MESSAGE_NOT_SUPPORTED = "action not supported"
    const val RESULT_MESSAGE_ENDPOINT_UNKNOWN_STATE = "unknown endpoint state"
    const val RESULT_MESSAGE_ENDPOINT_NOT_FOUND = "endpoint not found"

    // Endpoint Result code
    const val RESULT_CODE_ALREADY_IN_OPERATION = -1000
    const val RESULT_CODE_BRIDGE_UNREACHABLE = -1001
    const val RESULT_CODE_CLOUD_CONTROL_DISABLED = -1002
    const val RESULT_CODE_ENDPOINT_BUSY = -1003
    const val RESULT_CODE_ENDPOINT_CONTROL_UNAVAILABLE = -1004
    const val RESULT_CODE_ENDPOINT_LOW_POWER = -1005
    const val RESULT_CODE_ENDPOINT_UNREACHABLE = -1006
    const val RESULT_CODE_EXPIRED_AUTHORIZATION_CREDENTIAL = -1007
    const val RESULT_CODE_FIRMWARE_OUT_OF_DATE = -1008
    const val RESULT_CODE_HARDWARE_MALFUNCTION = -1009
    const val RESULT_CODE_INSUFFICIENT_PERMISSIONS = -1010
    const val RESULT_CODE_INTERNAL_ERROR = -1011
    const val RESULT_CODE_INVALID_AUTHORIZATION_CREDENTIAL = -1012
    const val RESULT_CODE_INVALID_DIRECTIVE = -1013
    const val RESULT_CODE_INVALID_VALUE = -1014
    const val RESULT_CODE_NO_SUCH_ENDPOINT = -1015
    const val RESULT_CODE_NOT_CALIBRATED = -1016
    const val RESULT_CODE_NOT_IN_OPERATION = -1017
    const val RESULT_CODE_NOT_SUPPORTED_IN_CURRENT_MODE = -1018
    const val RESULT_CODE_NOT_SUPPORTED_WITH_CURRENT_BATTERY_CHARGE_STATE = -1019
    const val RESULT_CODE_PARTNER_APPLICATION_REDIRECTION = -1020
    const val RESULT_CODE_POWER_LEVEL_NOT_SUPPORTED = -1021
    const val RESULT_CODE_RATE_LIMIT_EXCEEDED = -1022
    const val RESULT_CODE_TEMPERATURE_VALUE_OUT_OF_RANGE = -1023
    const val RESULT_CODE_TOO_MANY_FAILED_ATTEMPTS = -1024
    const val RESULT_CODE_VALUE_OUT_OF_RANGE = -1025

    fun codeToAlexaEndpointErrorType(code: Int): String {
        return when (code) {
            RESULT_CODE_ALREADY_IN_OPERATION -> "ALREADY_IN_OPERATION"
            RESULT_CODE_BRIDGE_UNREACHABLE -> "BRIDGE_UNREACHABLE"
            RESULT_CODE_CLOUD_CONTROL_DISABLED -> "CLOUD_CONTROL_DISABLED"
            RESULT_CODE_ENDPOINT_BUSY -> "ENDPOINT_BUSY"
            RESULT_CODE_ENDPOINT_CONTROL_UNAVAILABLE -> "ENDPOINT_CONTROL_UNAVAILABLE"
            RESULT_CODE_ENDPOINT_LOW_POWER -> "ENDPOINT_LOW_POWER"
            RESULT_CODE_ENDPOINT_UNREACHABLE -> "ENDPOINT_UNREACHABLE"
            RESULT_CODE_EXPIRED_AUTHORIZATION_CREDENTIAL -> "EXPIRED_AUTHORIZATION_CREDENTIAL"
            RESULT_CODE_FIRMWARE_OUT_OF_DATE -> "FIRMWARE_OUT_OF_DATE"
            RESULT_CODE_HARDWARE_MALFUNCTION -> "HARDWARE_MALFUNCTION"
            RESULT_CODE_INSUFFICIENT_PERMISSIONS -> "INSUFFICIENT_PERMISSIONS"
            RESULT_CODE_INTERNAL_ERROR -> "INTERNAL_ERROR"
            RESULT_CODE_INVALID_AUTHORIZATION_CREDENTIAL -> "INVALID_AUTHORIZATION_CREDENTIAL"
            RESULT_CODE_INVALID_DIRECTIVE -> "INVALID_DIRECTIVE"
            RESULT_CODE_INVALID_VALUE -> "INVALID_VALUE"
            RESULT_CODE_NO_SUCH_ENDPOINT -> "NO_SUCH_ENDPOINT"
            RESULT_CODE_NOT_CALIBRATED -> "NOT_CALIBRATED"
            RESULT_CODE_NOT_IN_OPERATION -> "NOT_IN_OPERATION"
            RESULT_CODE_NOT_SUPPORTED_IN_CURRENT_MODE -> "NOT_SUPPORTED_IN_CURRENT_MODE"
            RESULT_CODE_NOT_SUPPORTED_WITH_CURRENT_BATTERY_CHARGE_STATE -> "NOT_SUPPORTED_WITH_CURRENT_BATTERY_CHARGE_STATE"
            RESULT_CODE_PARTNER_APPLICATION_REDIRECTION -> "PARTNER_APPLICATION_REDIRECTION"
            RESULT_CODE_POWER_LEVEL_NOT_SUPPORTED -> "POWER_LEVEL_NOT_SUPPORTED"
            RESULT_CODE_RATE_LIMIT_EXCEEDED -> "RATE_LIMIT_EXCEEDED"
            RESULT_CODE_TEMPERATURE_VALUE_OUT_OF_RANGE -> "TEMPERATURE_VALUE_OUT_OF_RANGE"
            RESULT_CODE_TOO_MANY_FAILED_ATTEMPTS -> "TOO_MANY_FAILED_ATTEMPTS"
            RESULT_CODE_VALUE_OUT_OF_RANGE -> "VALUE_OUT_OF_RANGE"
            else -> "Unknown"
        }
    }
}