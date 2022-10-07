package com.SmartWatchVoice.bestapp.action.alerts;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

import java.util.List;

public class DeleteAlertsSucceededAction extends EventAction {
    private final List<String> tokens;

    public DeleteAlertsSucceededAction(List<String> tokens) {
        this.tokens = tokens;
    }
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.tokens = tokens;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALERTS)
                .setHeaderName(ApiConst.NAME_DELETE_ALERTS_SUCCEEDED)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
