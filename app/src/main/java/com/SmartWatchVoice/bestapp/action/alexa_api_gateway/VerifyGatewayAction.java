package com.SmartWatchVoice.bestapp.action.alexa_api_gateway;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;

public class VerifyGatewayAction extends EventAction {

    @Override
    public EventAction create() {

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA_API_GATEWAY)
                .setHeaderName(ApiConst.NAME_VERIFY_GATEWAY)
                .setHeaderMessageId(makeMessageId())
                .build();

        return this;
    }
}
