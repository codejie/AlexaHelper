package jie.android.alexahelper.smartwatchsdk.action.sdk.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.EventBuilder
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ResultWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst

fun verifyGatewayAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val event = EventBuilder(AlexaConst.NS_ALEXA_API_GATEWAY, AlexaConst.NAME_VERIFY_GATEWAY).create()
    sdk.httpChannel.postEvent(event) { success, reason, _ ->
        val result = ResultWrapper(action.name,
            if (success) SDKConst.RESULT_CODE_SUCCESS else SDKConst.RESULT_CODE_ACTION_FAILED,
            reason
        ).build()

        action.callback?.onResult(result.toString())
    }
}