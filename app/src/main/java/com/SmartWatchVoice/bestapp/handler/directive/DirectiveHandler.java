package com.SmartWatchVoice.bestapp.handler.directive;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.List;

public abstract class DirectiveHandler {
    public DirectiveParser.Part findPartByHeader(List<DirectiveParser.Part> parts, String key, String value) {
        for (int index = 0; index < parts.size(); ++ index) {
            final DirectiveParser.Part part = parts.get(index);
            if (value.equals(part.getHeaders().get(key))) {
                return part;
            }
        }
        return null;
    }

    public abstract void handle(Directive directive, List<DirectiveParser.Part> parts);
}
