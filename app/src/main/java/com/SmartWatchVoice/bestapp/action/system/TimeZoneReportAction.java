package com.SmartWatchVoice.bestapp.action.system;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.system.SettingInfo;

public class TimeZoneReportAction extends EventAction {
    private final String timezone;

    public TimeZoneReportAction(String timezone) {
        this.timezone = timezone;
    }

    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.timeZone = timezone;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SYSTEM)
                .setHeaderName(ApiConst.NAME_TIME_ZONE_REPORT)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
