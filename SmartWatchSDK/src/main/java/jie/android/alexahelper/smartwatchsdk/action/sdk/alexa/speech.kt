package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.ResponseStreamDirectiveParser
import jie.android.alexahelper.smartwatchsdk.channel.sdk.ChannelData
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Utils
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

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

        CoroutineScope(Dispatchers.IO).launch {
            // speak
//            val source: BufferedSource = response!!.body!!.source()

            val parser: ResponseStreamDirectiveParser = ResponseStreamDirectiveParser()
            val parts = response?.let { parser.parseParts(it) }

//            Logger.d("recognize response - ")
//            for (part in parts) {
//                Logger.d(part.toString())
//            }
            parts?.let { sdk.sdkChannel.send(ChannelData(ChannelData.DataType.DirectiveParts, it)) }
        }
    }
}

fun speakStartAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    action.getPayload()
        ?: throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD)

    val dialogId = action.getPayload()!!.getString("dialogId")
    val token = action.getPayload()!!.getString("token")!!

    val event = EventBuilder(AlexaConst.NS_SPEECH_SYNTHESIZER, AlexaConst.NAME_SPEECH_STARTED).apply {
        addPayload("token", token)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                dialogId?.let { put("dialogId", it) }
                put("token", token)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString(), null)
    }
}

fun speakEndAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    action.getPayload()
        ?: throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD)

    val dialogId = action.getPayload()!!.getString("dialogId")
    val token = action.getPayload()!!.getString("token")!!

    val event = EventBuilder(AlexaConst.NS_SPEECH_SYNTHESIZER, AlexaConst.NAME_SPEECH_FINISHED).apply {
        addPayload("token", token)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                dialogId?.let { put("dialogId", it) }
                put("token", token)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString(), null)
    }
}

fun speakInterruptedAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    action.getPayload()
        ?: throw SDKException(SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD)

    val dialogId = action.getPayload()!!.getString("dialogId")
    val token = action.getPayload()!!.getString("token")!!
    val offset = action.getPayload()!!.getInt("offset")!!

    val event = EventBuilder(AlexaConst.NS_SPEECH_SYNTHESIZER, AlexaConst.NAME_SPEECH_INTERRUPTED).apply {
        addPayload("token", token)
    }.create()

    sdk.httpChannel.postEvent(event) { success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).apply {
            val payload = buildJsonObject {
                dialogId?.let { put("dialogId", it) }
                put("token", token)
                put("offsetInMilliseconds", offset)
            }
            setPayload(payload)
        }.build()

        action.callback?.onResult(result.toString(), null)
    }
}