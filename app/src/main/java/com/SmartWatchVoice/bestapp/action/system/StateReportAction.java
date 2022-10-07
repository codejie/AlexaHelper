package com.SmartWatchVoice.bestapp.action.system;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Header;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.system.SettingInfo;

import java.util.Arrays;

public class StateReportAction extends EventAction {

    @Override
    public EventAction create() {
        Payload.State timeZoneState = new Payload.State();
        timeZoneState.header = new Header();
        timeZoneState.header.namespace = ApiConst.NS_SYSTEM;
        timeZoneState.header.name = ApiConst.NAME_TIME_ZONE_REPORT;
        timeZoneState.payload = new Payload();
        timeZoneState.payload.timeZone = SettingInfo.getInstance().timeZone; // "Asia/Shanghai";

        Payload.State localesState = new Payload.State();
        localesState.header = new Header();
        localesState.header.namespace = ApiConst.NS_SYSTEM;
        localesState.header.name = ApiConst.NAME_LOCALES_REPORT;
        localesState.payload = new Payload();
        localesState.payload.locales = SettingInfo.getInstance().locales; // Arrays.asList("en-US", "fr-FR", "it-IT");

        Payload payload = new Payload();
        payload.states = Arrays.asList(timeZoneState, localesState);

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SYSTEM)
                .setHeaderName(ApiConst.NAME_STATE_REPORT)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
