package com.SmartWatchVoice.bestapp.alexa.api;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class Protocol {
    protected static final String Name = "Protocol";

    public Header header = new Header();
    public Payload payload = null;
    public Endpoint endpoint = null;
//    public List<Context> context = null;

    @NonNull
    @Override
    public final String toString() {
        return Name + " { namespace = " + header.namespace + " | name = " + header.name + " }";
    }

    public static abstract class Builder<T extends Protocol> {
        protected T t = newT();

        protected abstract T newT();

        public Builder setHeaderNamespace(final String namespace) {
            t.header.namespace = namespace;
            return this;
        }

        public Builder setHeaderName(final String name) {
            t.header.name = name;
            return this;
        }

        public Builder setHeaderMessageId(final String messageId) {
            t.header.messageId = messageId;
            return this;
        }

        public Builder setHeaderPayloadVersion(String version) {
            t.header.payloadVersion = version;
            return this;
        }

        public Builder setHeaderCorrelationToken(String token) {
            t.header.correlationToken = token;
            return this;
        }

        public Builder setHeaderEventCorrelationToken(String token) {
            t.header.eventCorrelationToken = token;
            return this;
        }

        public Builder setHeaderDialogRequestId(String requestId) {
            t.header.dialogRequestId = requestId;
            return this;
        }

        public Builder setPayload(final Payload payload) {
            t.payload = payload;
            return this;
        }

//        public Builder setContext(List<Context> context) {
//            t.context = context;
//            return this;
//        }

        public Builder setEndpoint(Endpoint endpoint) {
            t.endpoint = endpoint;
            return this;
        }

        public T build() {
            return t;
        }
    }
}
