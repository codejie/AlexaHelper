package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.getString
import jie.android.alexahelper.smartwatchsdk.utils.Logger

fun onAlexaApiGatewayDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_SET_GATEWAY -> onSetGateway(sdk, directive, parts)
        else -> Logger.w("unsupported AlexaApiGateway - $directive")
    }
}

private fun onSetGateway(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    val gateway = directive.payload?.getString("gateway", false)
    gateway?.also {
        sdk.httpChannel.avsBaseUrl = gateway
    }
}
