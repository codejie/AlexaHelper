package com.SmartWatchVoice.bestapp.system.thing.api;

import java.util.List;

public class Context {
    public static class Property<T> {
        public String namespace;
        public String name;
        public String timeOfSample;
        public Long uncertaintyInMilliseconds;
        public T value;

        public static class Builder<T> {
            private Property<T> context = new Property<>();

            public Property build() {
                return context;
            }

            public Property.Builder setNamespace(String namespace) {
                context.namespace = namespace;
                return this;
            }

            public Property.Builder setName(String name) {
                context.name = name;
                return this;
            }

            public Property.Builder setTimeOfSample(String timeOfSample) {
                context.timeOfSample = timeOfSample;
                return this;
            }

            public Property.Builder setUncertaintyInMilliseconds(Long ms) {
                context.uncertaintyInMilliseconds = ms;
                return this;
            }

            public Property.Builder setValue(T value) {
                context.value = value;
                return this;
            }
        }
    }
    public List<Property> properties;
}
