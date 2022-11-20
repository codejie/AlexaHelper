package jie.android.alexahelper.smartwatchsdk.action.sdk.device

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.DeviceInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.*
import kotlinx.serialization.json.JsonObject

fun setInfoAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    val payload: JsonObject = action.getPayload()!!

    val result = DeviceInfo.parseDeviceSetting(payload)

    if (result)
        callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS))
    else
        callback(ResultWrapper(action.name, SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD))

//    action.callback?.onResult(
//        if (result)
//            ResultWrapper(action.name, SDKConst.RESULT_CODE_SUCCESS).build().toString()
//        else
//            ResultWrapper(action.name, SDKConst.RESULT_CODE_MISSING_FIELD, SDKConst.RESULT_MESSAGE_MISSING_FIELD).build().toString()
//    )
}
