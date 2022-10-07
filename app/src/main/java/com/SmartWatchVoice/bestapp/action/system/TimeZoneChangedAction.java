package com.SmartWatchVoice.bestapp.action.system;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

public class TimeZoneChangedAction extends EventAction {

    private final String timeZone;

    public TimeZoneChangedAction(String timeZone) {
        this.timeZone = timeZone;
    }
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.timeZone = this.timeZone;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SYSTEM)
                .setHeaderName(ApiConst.NAME_TIME_ZONE_CHANGED)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
