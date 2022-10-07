package com.SmartWatchVoice.bestapp.alexa.api;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class Context<T extends Context.Payload> {
//    public static class HeaderPayloadContext<T extends HeaderPayloadContext.Payload> extends Context {
    public static class Header {
        public String namespace;
        public String name;
    }

    public static class Payload {}

    public Header header;
    public T payload;

    public static class Builder<T extends Payload> {
        private Header header = new Header();
        private T payload;

        public Context build() {
            Context ret = new Context();
            ret.header = header;
            ret.payload = payload;

            return ret;
        }

        public Builder setHeaderNamespace(String namespace) {
            header.namespace = namespace;
            return this;
        }

        public Builder setHeaderName(String name) {
            header.name = name;
            return this;
        }

        public Builder setPayload(T payload) {
            this.payload = payload;
            return this;
        }
    }
}
//
//    public static class ReportableContext<T> extends Context {
//        public String namespace;
//        public String name;
//        public String timeOfSample;
//        public Long uncertaintyInMilliseconds;
//        public T value;
//
//        public static class Builder<T> {
//            private ReportableContext context = new ReportableContext();
//
//            public Context build() {
//                return context;
//            }
//
//            public ReportableContext.Builder setNamespace(String namespace) {
//                context.namespace = namespace;
//                return this;
//            }
//
//            public ReportableContext.Builder setName(String name) {
//                context.name = name;
//                return this;
//            }
//
//            public ReportableContext.Builder setTimeOfSample(String timeOfSample) {
//                context.timeOfSample = timeOfSample;
//                return this;
//            }
//
//            public ReportableContext.Builder setUncertaintyInMilliseconds(Long ms) {
//                context.uncertaintyInMilliseconds = ms;
//                return this;
//            }
//
//            public ReportableContext.Builder setValue(T value) {
//                context.value = value;
//                return this;
//            }
//        }
//    }
//
//    public static final String makeToken() {
//        return UUID.randomUUID().toString();
//    }
//    public static final String makeDateTimeString(Date date) {
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        return sdf.format(date);
//    }
//}
