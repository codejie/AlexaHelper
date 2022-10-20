package com.SmartWatchVoice.bestapp.system.thing.lightspot.action;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Endpoint;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.system.thing.Thing;
import com.SmartWatchVoice.bestapp.system.thing.ThingEventAction;
import com.SmartWatchVoice.bestapp.system.thing.api.Context;
import com.SmartWatchVoice.bestapp.system.thing.lightspot.LightSpotThing;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.Arrays;
import java.util.Date;


public class StateReportAction extends ThingEventAction {
    public StateReportAction(Thing thing, Directive directive) {
        super(thing, directive);
    }

    @Override
    public EventAction create() {
        Endpoint endpoint = new Endpoint();
        endpoint.endpointId = thing.getId();
        endpoint.scope = new Endpoint.Scope();
        endpoint.scope.type = "BearerToken";
//        endpoint.scope.token = HttpChannel.getInstance().getAccessToken();

        Context.Property<String> property = new Context.Property.Builder<>()
                .setNamespace(ApiConst.NS_ALEXA_POWER_CONTROLLER)
                .setName(ApiConst.NAME_POWER_STATE)
                .setValue(((LightSpotThing)thing).state)
                .setTimeOfSample(Utils.makeDateTimeString(new Date()))
                .setUncertaintyInMilliseconds(5000L)
                .build();
        Context context = new Context();
        context.properties = Arrays.asList(property);

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_ALEXA)
                .setHeaderName(ApiConst.NAME_STATE_REPORT)
                .setHeaderMessageId(makeMessageId())
                .setHeaderCorrelationToken(directive.header.correlationToken)
                .setEndpoint(endpoint)
//                .setContext(Arrays.asList(context))
                .build();
        wrapper.setContext(context);

        return this;
    }
}
