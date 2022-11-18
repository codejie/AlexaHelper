package jie.android.alexahelper.smartwatchsdk.channel.alexa

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK
import jie.android.alexahelper.smartwatchsdk.action.alexa.*
import jie.android.alexahelper.smartwatchsdk.action.sdk.alexa.AlexaAction
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.AlexaConst
import jie.android.alexahelper.smartwatchsdk.protocol.alexa.Directive
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.SDKConst
import jie.android.alexahelper.smartwatchsdk.utils.Logger

fun onDirectiveParts(sdk: SmartWatchSDK, directiveParts: List<DirectiveParser.Part>) {
    try {
        for (part in directiveParts) {
            Logger.d("process directive part - $part")
            if (part.type == DirectiveParser.PartType.DIRECTIVE) {
                val directive = Directive.parse((part as DirectiveParser.DirectivePart).directive)
                when (directive?.namespace) {
                    AlexaConst.NS_ALEXA -> onAlexaDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_NOTIFICATIONS -> onNotificationsDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_ALEXA_DO_NOT_DISTURB -> onAlexaDoNotDisturbDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SPEECH_RECOGNIZER -> onSpeechRecognizerDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SPEECH_SYNTHESIZER -> onSpeechSynthesizerDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_ALEXA_API_GATEWAY -> onAlexaApiGatewayDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SYSTEM -> onSystemDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_ALERTS -> onAlertsDirective(sdk, directive, directiveParts)
                    AlexaConst.NS_SPEAKER -> onSpeakerDirective(sdk, directive, directiveParts)
                    else -> Logger.w("unsupported - ${directive.toString()}")
                }
            }
        }
    } catch (e: Exception) {
        Logger.w("alexa directive exception - ${e.message}")
    }
}