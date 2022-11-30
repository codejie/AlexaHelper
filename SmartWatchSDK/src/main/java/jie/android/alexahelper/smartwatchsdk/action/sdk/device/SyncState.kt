package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.StateInfo
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

internal fun syncStateAction(
    sdk: SmartWatchSDK,
    action: ActionWrapper,
    callback: ActionResultCallback
) {
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
                setContext(DeviceInfo.stateInfo.makeContext())
            }.create()
        sdk.httpChannel.postEvent(event) { success, reason, response ->
            if (success) {
                Logger.d("postSynchronizeStateAction success - ${response!!.code}")
            } else {
                Logger.w("postSynchronizeStateAction failed - $reason")
            }
        }
    }

    val result = ResultWrapper(SDKConst.ACTION_DEVICE_SYNC_STATE, SDKConst.RESULT_CODE_SUCCESS)
    callback(result)
}

private fun onSpeakerState(state: JsonObject) {
    val items = state.getJsonArray("items")
    items?.let {

        if (DeviceInfo.stateInfo.volumeState == null)
            DeviceInfo.stateInfo.volumeState = StateInfo.VolumeState()

        var volume: Int? = null
        var muted: Int? = null
        for (index in 0 until it.size) {
            val item = it[index] as JsonObject
            when (item.getString("name")) {
                "volume" -> DeviceInfo.stateInfo.volumeState!!.volume = item.getInt("value")
                "muted" -> DeviceInfo.stateInfo.volumeState!!.volume = item.getInt("value")
            }
        }
    }
}

private fun onAlertState(state: JsonObject) {
    val items = state.getJsonArray("items")
    items?.let {

        DeviceInfo.stateInfo.alertsState = StateInfo.AlertsState()

        for (index in 0 until it.size) {
            val item = it[index] as JsonObject
            when (item.getString("name")) {
                "allAlerts" -> parseAlertsInfo(DeviceInfo.stateInfo.alertsState!!.allAlerts, item.getJsonArray("value"))
                "activeAlerts" -> parseAlertsInfo(DeviceInfo.stateInfo.alertsState!!.activeAlerts, item.getJsonArray("value"))
            }
        }
    }
}

fun parseAlertsInfo(alerts: ArrayList<StateInfo.AlertsState.Alert>, items: JsonArray?) {
    items?.let {
        for (index in 0 until items.size) {
            val item = items[index] as JsonObject
            alerts.add(StateInfo.AlertsState.Alert(
                item.getString("token")!!,
                item.getString("type")!!,
                item.getString("scheduledTime")!!
            ))
        }
    }
}
