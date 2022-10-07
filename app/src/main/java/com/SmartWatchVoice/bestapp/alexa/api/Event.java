package com.SmartWatchVoice.bestapp.alexa.api;

import java.util.List;

public class Event extends Protocol {

    public static class EventWrapper {
        public Event event = null;
//        public List<String> context = null;
        public Object context = null;

        public void setContext(Object context) {
            this.context = context;
        }
    }

    protected static final String Name = "Event";

    public static class Builder extends Protocol.Builder<Event> {
        @Override
        protected Event newT() {
            return new Event();
        }
    }

}
