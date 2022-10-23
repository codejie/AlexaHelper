package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.ResponseStreamDirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Utils
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import okio.BufferedSource

fun speechStartAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val dialogId = Utils.makeMessageId()

    val result = ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).apply {
        val payload = buildJsonObject {
            put("dialogId", dialogId)
        }
        setPayload(payload)
    }.build()

    action.callback?.onResult(result.toString())
}

fun speechStopAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val actionPayload = action.getPayload()
        ?: throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD)

    val dialogId = actionPayload.getString("dialogId")!!

    val result = ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).apply {
        val payload = buildJsonObject {
            put("dialogId", dialogId)
        }
        setPayload(payload)
    }.build()

    action.callback?.onResult(result.toString())
}

fun speechRecognizeAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    if (action.extra == null) {
        throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_EXTRA)
    }

    action.getPayload()
        ?: throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD)

    val dialogId = Utils.makeMessageId()
    val initiatorType = "TAP"

    val initiator = buildJsonObject {
        put("type", initiatorType)
    }

    val event: JsonObject = EventBuilder(
        AlexaConst.NS_SPEECH_RECOGNIZER,
        AlexaConst.NAME_RECOGNIZE).apply {

        addHeader("dialogRequestId", dialogId)
        addPayload("profile", "NEAR_FIELD")
        addPayload("format", "AUDIO_L16_RATE_16000_CHANNELS_1")
        addPayload("initiator", initiator)
    }.create()

    sdk.httpChannel.postEventWithAudio(event, action.extra as ByteArray) { success, reason, response ->

        val source: BufferedSource = response!!.body!!.source()

//        Logger.d(if (source.exhausted()) "SOURCE exhausted" else "SOURCE not")

        val parser: ResponseStreamDirectiveParser = ResponseStreamDirectiveParser()
        val parts = parser.parseParts(source)

        Logger.d("recognize response - ")
        for (part in parts) {
            Logger.d(part.toString())
        }

        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                put("dialogId", dialogId)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString())
    }

}