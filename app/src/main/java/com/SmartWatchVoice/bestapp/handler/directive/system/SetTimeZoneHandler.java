package com.SmartWatchVoice.bestapp.handler.directive.system;

import com.SmartWatchVoice.bestapp.action.system.TimeZoneReportAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class SetTimeZoneHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        SettingInfo.getInstance().timeZone = directive.payload.timeZone;
        SettingInfo.getInstance().flush();

        new TimeZoneReportAction(SettingInfo.getInstance().timeZone).create().post();

        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().homeFragmentHandler, HandlerConst.MSG_SETTING_CHANGED);
    }
}
