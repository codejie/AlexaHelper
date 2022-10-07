package com.SmartWatchVoice.bestapp.handler.directive.system;

import com.SmartWatchVoice.bestapp.action.system.StateReportAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.List;

public class SystemReportStateHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        new StateReportAction().create().post();
    }
}
