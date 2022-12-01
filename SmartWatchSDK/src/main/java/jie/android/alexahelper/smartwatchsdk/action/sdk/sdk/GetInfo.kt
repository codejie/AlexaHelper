package jie.android.alexahelper.smartwatchsdk.action.sdk.sdk

import jie.android.alexahelper.smartwatchsdk.ActionResultCallback
import jie.android.alexahelper.smartwatchsdk.BuildConfig
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.SDKInfo
import jie.android.alexahelper.smartwatchsdk.action.sdk.endpoint.makeDate
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun getInfoAction(sdk: SmartWatchSDK, action: ActionWrapper, callback: ActionResultCallback) {
    val result = ResultWrapper(SDKConst.ACTION_SDK_GET_INFO, SDKConst.RESULT_CODE_SUCCESS).apply {
        setPayload(buildJsonObject {
            put("version", "${SDKInfo.Version} (${(if (BuildConfig.IS_DEBUG) "debug" else "release")})")
            put("manufacturer", SDKInfo.Manufacturer)
            put("release", makeDate())
        })
    }

    callback(result)
}