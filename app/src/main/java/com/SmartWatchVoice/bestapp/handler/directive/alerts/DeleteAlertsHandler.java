package com.SmartWatchVoice.bestapp.handler.directive.alerts;

import com.SmartWatchVoice.bestapp.action.alerts.DeleteAlertSucceededAction;
import com.SmartWatchVoice.bestapp.action.alerts.DeleteAlertsSucceededAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.List;

public class DeleteAlertsHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        SettingInfo.getInstance().alertInfo.deleteAlerts(directive.payload.tokens);

        new DeleteAlertsSucceededAction(directive.payload.tokens).create().post();
    }
}
