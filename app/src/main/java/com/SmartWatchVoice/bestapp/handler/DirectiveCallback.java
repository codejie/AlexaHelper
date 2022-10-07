package com.SmartWatchVoice.bestapp.handler;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.SmartWatchVoice.bestapp.handler.directive.alerts.DeleteAlertsHandler;
import com.SmartWatchVoice.bestapp.handler.directive.audio_player.PlayHandler;
import com.SmartWatchVoice.bestapp.handler.directive.speech_recognizer.ExpectSpeechHandler;
import com.SmartWatchVoice.bestapp.handler.directive.system.SetLocalesHandler;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.directive.alerts.DeleteAlertHandler;
import com.SmartWatchVoice.bestapp.handler.directive.alerts.SetAlertHandler;
import com.SmartWatchVoice.bestapp.handler.directive.alexa.EventProcessedHandler;
import com.SmartWatchVoice.bestapp.handler.directive.alexa_do_not_disturb.SetDoNotDisturbHandler;
import com.SmartWatchVoice.bestapp.handler.directive.notifications.ClearIndicatorHandler;
import com.SmartWatchVoice.bestapp.handler.directive.notifications.SetIndicatorHandler;
import com.SmartWatchVoice.bestapp.handler.directive.speech_synthesizer.SpeakHandler;
import com.SmartWatchVoice.bestapp.handler.directive.alexa.ReportStateHandler;
import com.SmartWatchVoice.bestapp.handler.directive.system.ResetUserInactivityHandler;
import com.SmartWatchVoice.bestapp.handler.directive.system.SetTimeZoneHandler;
import com.SmartWatchVoice.bestapp.handler.directive.system.SystemReportStateHandler;
import com.SmartWatchVoice.bestapp.handler.directive.template_runtime.RenderTemplateHandler;
import com.SmartWatchVoice.bestapp.system.ThingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.util.List;

public class DirectiveCallback implements Handler.Callback {
    @Override
    public boolean handleMessage(@NonNull Message message) {
        if (message.what == HandlerConst.MSG_STREAM_DIRECTIVE_PARTS || message.what == HandlerConst.MSG_CHANNEL_DIRECTIVE_PARTS) {
            onDirectiveParts((List<DirectiveParser.Part>)message.obj);
        }
        return true;
    }

    private void onDirectiveParts(List<DirectiveParser.Part> parts) {
        int index = 0;
        while (index < parts.size()) {
            DirectiveParser.Part part = parts.get(index);

            Logger.v(part.toString());

            if (part.getType() == DirectiveParser.PartType.DIRECTIVE) {
                final Directive directive = ((DirectiveParser.DirectivePart) part).getDirective();
                Logger.v("Directive - " + directive.toString());

                switch (directive.header.namespace) {
                    case ApiConst.NS_ALEXA:
                        onAlexaDirective(directive, parts);
                        break;
                    case ApiConst.NS_ALEXA_DO_NOT_DISTURB:
                        onAlexaDoNotDisturbDirective(directive, parts);
                        break;
                    case ApiConst.NS_SYSTEM:
                        onSystemDirective(directive, parts);
                        break;
                    case ApiConst.NS_NOTIFICATIONS:
                        onNotificationsDirective(directive, parts);
                        break;
                    case ApiConst.NS_SPEECH_SYNTHESIZER:
                        onSpeechSynthesizerDirective(directive, parts);
                        break;
                    case ApiConst.NS_SPEECH_RECOGNIZER:
                        onSpeechRecognizerDirective(directive, parts);
                        break;
                    case ApiConst.NS_ALERTS:
                        onAlertsDirective(directive, parts);
                        break;
                    case ApiConst.NS_AUDIO_PLAYER:
                        onPlayDirective(directive, parts);
                        break;
                    case ApiConst.NS_TEMPLATE_RUNTIME:
                        onTemplateRunTimeDirective(directive, parts);
                        break;
                    case ApiConst.NS_ALEXA_POWER_CONTROLLER:
                        onAlexaPowerControllerDirective(directive, parts);
                        break;
                    default:
                        Logger.e("Unsupported directive - " + directive.toString());
                }
            }

            ++ index;
        }
    }

    private void onAlexaPowerControllerDirective(Directive directive, List<DirectiveParser.Part> parts) {
        if (!ThingInfo.getInstance().onDirective(directive, parts)) {
            Logger.e("Unsupported by thing directive name - " + directive.toString());
        }
    }

    private void onTemplateRunTimeDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_RENDER_TEMPLATE:
                new RenderTemplateHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onPlayDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_PLAY:
                new PlayHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onSpeechRecognizerDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_EXPECT_SPEECH:
                new ExpectSpeechHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onAlertsDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_SET_ALERT:
                new SetAlertHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_DELETE_ALERT:
                new DeleteAlertHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_DELETE_ALERTS:
                new DeleteAlertsHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onNotificationsDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_SET_INDICATOR:
                new SetIndicatorHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_CLEAR_INDICATOR:
                new ClearIndicatorHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onAlexaDoNotDisturbDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_SET_DO_NOT_DISTURB:
                new SetDoNotDisturbHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_REPORT_STATE:
                new SystemReportStateHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onSystemDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_REPORT_STATE:
                new SystemReportStateHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_RESET_USER_INACTIVITY:
                new ResetUserInactivityHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_SET_TIMEZONE:
                new SetTimeZoneHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_SET_LOCALES:
                new SetLocalesHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive - " + directive.toString());
        }
    }

    private void onAlexaDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_EVENT_PROCESSED:
                new EventProcessedHandler().handle(directive, parts);
                break;
            case ApiConst.NAME_REPORT_STATE:
                new ReportStateHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive name - " + directive.toString());
        }
    }

    private void onSpeechSynthesizerDirective(Directive directive, List<DirectiveParser.Part> parts) {
        switch (directive.header.name) {
            case ApiConst.NAME_SPEAK:
                new SpeakHandler().handle(directive, parts);
                break;
            default:
                Logger.e("Unsupported directive name - " + directive.toString());
        }
    }
}
