package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

fun onSpeakerDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_VOLUME -> onSetVolume(sdk, directive, parts)
        AlexaConst.NAME_ADJUST_VOLUME ->onAdjustVolume(sdk, directive, parts)
        AlexaConst.NAME_SET_MUTE -> onSetMute(sdk, directive, parts)
        else -> Logger.w("unsupported Alexa - $directive")
    }
}

private fun onSetMute(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val muted = directive.payload!!.getBoolean("muted")!!
    val action = ActionWrapper(SDKConst.ACTION_ALEXA_VOLUME_UPDATED).apply {
        val payload = buildJsonObject {
            put("mode", if (muted) "MUTE" else "UNMUTE")
            put("volume", if (muted) 1 else 0)
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        val volume = result.getPayload()!!.getInt("volume")!!
        muteChanged(sdk, muted, volume)
    }

//    sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
//        override fun onResult(data: String, extra: Any?) {
//            val result = ResultWrapper.parse(data, extra)
//            val volume = result.getPayload()!!.getInt("volume")!!
//
//            muteChanged(sdk, muted, volume)
//        }
//    })
}

private fun muteChanged(sdk: SmartWatchSDK, muted: Boolean, volume: Int) {
    val event = EventBuilder(AlexaConst.NS_SPEAKER, AlexaConst.NAME_MUTE_CHANGED).apply {
        addPayload("muted", muted)
        addPayload("volume", volume)
    }.create()

    sdk.httpChannel.postEvent(event, null)
}

private fun onAdjustVolume(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val volume = directive.payload!!.getInt("volume")
    val action = ActionWrapper(SDKConst.ACTION_ALEXA_VOLUME_UPDATED).apply {
        val payload = buildJsonObject {
            put("mode", "RELATIVE")
            put("volume", volume)
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        val volume = result.getPayload()!!.getInt("volume")!!

        volumeChanged(sdk, volume)
    }

//    sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
//        override fun onResult(data: String, extra: Any?) {
//            val result = ResultWrapper.parse(data, extra)
//            val volume = result.getPayload()!!.getInt("volume")!!
//
//            volumeChanged(sdk, volume)
//        }
//    })
}

private fun onSetVolume(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val volume = directive.payload!!.getInt("volume")
    val action = ActionWrapper(SDKConst.ACTION_ALEXA_VOLUME_UPDATED).apply {
        val payload = buildJsonObject {
            put("mode", "ABSOLUTE")
            put("volume", volume)
        }
        setPayload(payload)
    }

    sdk.toAction(action) { result ->
        val volume = result.getPayload()!!.getInt("volume")!!

        volumeChanged(sdk, volume)
    }

//    sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
//        override fun onResult(data: String, extra: Any?) {
//            val result = ResultWrapper.parse(data, extra)
//            val volume = result.getPayload()!!.getInt("volume")!!
//
//            volumeChanged(sdk, volume)
//        }
//    })
}

private fun volumeChanged(sdk: SmartWatchSDK, volume: Int) {
    val event = EventBuilder(AlexaConst.NS_SPEAKER, AlexaConst.NAME_VOLUME_CHANGED).apply {
        addPayload("muted", false)
        addPayload("volume", volume)
    }.create()
    sdk.httpChannel.postEvent(event, null)
}

