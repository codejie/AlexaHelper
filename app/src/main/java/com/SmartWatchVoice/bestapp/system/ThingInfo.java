package com.SmartWatchVoice.bestapp.system;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.system.thing.lightspot.LightSpotThing;
import com.SmartWatchVoice.bestapp.system.thing.Thing;
import com.SmartWatchVoice.bestapp.system.thing.TouchAlexaThing;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThingInfo {
    private static ThingInfo instance = null;
    public static ThingInfo getInstance() {
        if (instance == null) {
            instance = new ThingInfo();
        }
        return instance;
    }

    private Map<String, Thing> things = new HashMap<>();

    public Map<String, Thing> getThings() {
        return things;
    }

    public void init() {
        register(new TouchAlexaThing());
        register(new LightSpotThing());
    }

    private void register(final Thing thing) {
        things.put(thing.getId(), thing);
    }

    public boolean onDirective(Directive directive, List<DirectiveParser.Part> parts) {
        if (directive.endpoint == null) {
            return false;
        }

        final Thing thing = things.get(directive.endpoint.endpointId);
        if (thing != null) {
            return thing.handle(directive, parts);
        }
        return false;
    }
}
