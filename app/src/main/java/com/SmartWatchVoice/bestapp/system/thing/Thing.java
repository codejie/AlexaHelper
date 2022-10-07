package com.SmartWatchVoice.bestapp.system.thing;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Endpoint;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.List;

public abstract class Thing {
    protected final String id;
    protected final Endpoint endpoint;

    public Thing() {
        endpoint = declare();
        id = endpoint.endpointId;
    }

    public String getId() {
        return id;
    }

    protected abstract Endpoint declare();

    public abstract boolean handle(Directive directive, List<DirectiveParser.Part> parts);

    public Endpoint getEndpoint() {
        return endpoint;
    }
}
