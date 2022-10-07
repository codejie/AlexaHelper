package com.SmartWatchVoice.bestapp.handler.directive.alexa;

import androidx.annotation.NonNull;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.system.StateReportAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.ThingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.util.List;

import okhttp3.Response;

public class ReportStateHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        if (!ThingInfo.getInstance().onDirective(directive, parts)) {
            Logger.e("Unsupported by thing directive name - " + directive.toString());
        }
    }
}
