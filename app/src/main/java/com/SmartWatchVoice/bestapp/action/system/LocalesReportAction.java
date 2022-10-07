package com.SmartWatchVoice.bestapp.action.system;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

import java.util.List;

public class LocalesReportAction extends EventAction {
    private final List<String> locales;
    public LocalesReportAction(List<String> locales) {
        this.locales = locales;
    }
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.locales = locales;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SYSTEM)
                .setHeaderName(ApiConst.NAME_LOCALES_REPORT)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
