package com.SmartWatchVoice.bestapp.handler.directive.system;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.Date;
import java.util.List;

public class ResetUserInactivityHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        RuntimeInfo.getInstance().lastActiveDate = new Date();
    }
}
