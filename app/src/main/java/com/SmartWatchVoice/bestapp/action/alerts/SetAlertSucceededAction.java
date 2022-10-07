package com.SmartWatchVoice.bestapp.action.alerts;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

public class SetAlertSucceededAction extends EventAction {

    private final String token;

    public SetAlertSucceededAction(String token) {
        this.token = token;
    }
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.token = token;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALERTS)
                .setHeaderName(ApiConst.NAME_SET_ALERT_SUCCEEDED)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
