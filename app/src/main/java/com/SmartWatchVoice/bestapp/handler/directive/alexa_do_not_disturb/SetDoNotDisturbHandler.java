package com.SmartWatchVoice.bestapp.handler.directive.alexa_do_not_disturb;

import androidx.annotation.NonNull;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.alexa_do_not_disturb.ReportDoNotDisturbAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

import okhttp3.Response;

public class SetDoNotDisturbHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        SettingInfo.getInstance().doNotDisturb = directive.payload.enabled;

        new ReportDoNotDisturbAction().create().post(new EventAction.OnChannelResponse() {
            @Override
            public void OnResponse(@NonNull Response response) {
                Utils.sendToHandlerMessage(RuntimeInfo.getInstance().homeFragmentHandler, HandlerConst.SETTING_DO_NOT_DISTURB);
            }
        });
    }
}
