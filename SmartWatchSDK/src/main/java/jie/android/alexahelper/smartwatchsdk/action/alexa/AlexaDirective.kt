package jie.android.alexahelper.smartwatchsdk.action.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.channel.alexa.DirectiveParser
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.utils.Logger

fun onAlexaDirective(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {
    when (directive.name) {
        AlexaConst.NAME_EVENT_PROCESSED -> onEventProcessed(sdk, directive, parts)
        else -> Logger.w("unsupported Alexa - ${directive.toString()}")
    }
}

private fun onEventProcessed(sdk: SmartWatchSDK, directive: Directive, parts: List<DirectiveParser.Part>) {

}
