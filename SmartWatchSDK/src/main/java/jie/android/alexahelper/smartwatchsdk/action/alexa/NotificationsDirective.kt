package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.ActionWrapper
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.utils.Logger

fun onNotificationsDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val action = ActionWrapper(SDKConst.ACTION_ALEXA_CLEAR_INDICATOR).build()
    sdk.onActionListener.onAction(action.toString(), null, object : OnResultCallback {
        override fun onResult(data: String, extra: Any?) {
            Logger.d("onNotificationsDirective result - $data")
        }
    })
}