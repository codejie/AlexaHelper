package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.RuntimeInfo
import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.HttpChannel
import jie.android.alexahelper.smartwatchsdk.sdk.*
import kotlinx.serialization.json.JsonObject

fun loginWithTokenAction(sdk: SmartWatchSDK, action: ActionWrapper) {
    val payload: JsonObject = action.data!!.getJsonObject("payload")!!;
    val refreshToken = payload.getString("refreshToken")
        ?: throw throw SDKException(
            SDKConst.RESULT_CODE_MISSING_PARAMETERS,
            SDKConst.RESULT_MESSAGE_MISSING_PARAMETERS
        );

    HttpChannel.postRefreshAccessToken(refreshToken) { success, reason, response ->
        if (success) {

        } else {

        }
    }

}