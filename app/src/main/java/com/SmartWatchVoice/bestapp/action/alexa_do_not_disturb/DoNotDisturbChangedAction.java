package com.SmartWatchVoice.bestapp.action.alexa_do_not_disturb;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

public class DoNotDisturbChangedAction extends EventAction {
    private boolean enabled;
    public DoNotDisturbChangedAction(boolean enabled) {
        this.enabled = enabled;
    }
    @Override
    public EventAction create() {
        Payload payload = new Payload();
        payload.enabled = this.enabled;

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA_DO_NOT_DISTURB)
                .setHeaderName(ApiConst.NAME_DO_NOT_DISTURB_CHANGED)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
