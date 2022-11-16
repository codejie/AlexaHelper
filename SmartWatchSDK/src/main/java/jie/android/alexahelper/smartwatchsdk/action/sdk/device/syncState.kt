package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun syncStateAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val alexa = action.getPayload()?.getJsonArray("alexa")
    alexa?.let {
        for (index in 0 until it.size) {
            val state: JsonObject = it[index] as JsonObject
            when (state.getString("name")) {
                "speaker" -> onSpeakerState(state)
                "alert" -> onAlertState(state)
                else -> Logger.w("unknown State name - ${state.getString("name")}")
            }
        }
    }

    // sync state to Alexa
    if (DeviceInfo.isLogin) {
        val event: JsonObject =
            EventBuilder(AlexaConst.NS_SYSTEM, AlexaConst.NAME_SYNCHRONIZE_STATE).apply {
                setContext(DeviceInfo.makeContext())
            }.create()
        sdk.httpChannel.postEvent(event) { success, reason, response ->
            if (success) {
                Logger.d("postSynchronizeStateAction success - ${response!!.code}")
            } else {
                Logger.w("postSynchronizeStateAction failed - $reason")
            }
        }
    }
}

fun onSpeakerState(state: JsonObject) {
    val items = state.getJsonArray("items")
    items?.let {
        var volume: Int? = null
        var muted: Int? = null
        for (index in 0 until it.size) {
            val item = it[index] as JsonObject
            when (item.getString("name")) {
                "volume" -> volume = item.getInt("value")
                "muted" -> muted = item.getInt("value")
            }
        }
        DeviceInfo.State.volumeState = buildJsonObject {
            volume?.let { put("volume", volume) }
            muted?.let { put("muted", muted == 1) }
        }
    }
}

fun onAlertState(state: JsonObject) {
    val items = state.getJsonArray("items")
    items?.let {
        for (index in 0 until it.size) {
            val item = it[index] as JsonObject
            when (item.getString("name")) {
                "allAlerts" -> DeviceInfo.State.allAlerts = item.getJsonArray("value")
            }
        }
    }
}
