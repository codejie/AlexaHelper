package com.SmartWatchVoice.bestapp.system.thing.lightspot.action;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.system.thing.Thing;
import com.SmartWatchVoice.bestapp.system.thing.ThingEventAction;
import com.SmartWatchVoice.bestapp.system.thing.api.Context;

import java.util.Arrays;

public class ResponseAction extends ThingEventAction {
    private final Context context;

    public ResponseAction(Thing thing, Directive directive, Context context) {
        super(thing, directive);
        this.context = context;
    }

    @Override
    public EventAction create() {
        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA)
                .setHeaderName(ApiConst.NAME_RESPONSE)
                .setHeaderMessageId(makeMessageId())
                .setHeaderCorrelationToken(directive.header.correlationToken)
                .setHeaderPayloadVersion("3")
                .setEndpoint(directive.endpoint)
//                .setContext(Arrays.asList(this.context))
                .build();
        wrapper.setContext(this.context);

        return this;
    }
}
