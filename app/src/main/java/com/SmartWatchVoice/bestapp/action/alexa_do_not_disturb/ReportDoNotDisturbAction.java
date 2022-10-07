package com.SmartWatchVoice.bestapp.action.alexa_do_not_disturb;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.system.SettingInfo;

public class ReportDoNotDisturbAction extends EventAction {
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.enabled = SettingInfo.getInstance().doNotDisturb;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA_DO_NOT_DISTURB)
                .setHeaderName(ApiConst.NAME_REPORT_DO_NOT_DISTURB)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();
        return this;
    }
}
