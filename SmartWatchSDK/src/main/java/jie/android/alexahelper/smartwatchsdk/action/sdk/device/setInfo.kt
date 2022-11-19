package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import jie.android.alexahelper.smartwatchsdk.utils.Logger
import kotlinx.serialization.json.JsonObject

fun setInfoAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload: JsonObject = action.getPayload()!!

    val result = DeviceInfo.parseDeviceSetting(payload)

    action.callback?.onResult(
        if (result)
            ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString()
        else
            ResultWrapper(action.name, SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD).build().toString()
    )
}
