package com.SmartWatchVoice.bestapp.system.thing;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;

public abstract class ThingEventAction extends EventAction {
    protected final Thing thing;
    protected final Directive directive;

    public ThingEventAction(Thing thing, Directive directive) {
        this.thing = thing;
        this.directive = directive;
    }
}
