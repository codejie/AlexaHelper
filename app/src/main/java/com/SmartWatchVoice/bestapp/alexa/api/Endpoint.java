package com.SmartWatchVoice.bestapp.alexa.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Endpoint {
    public static class Scope {
        public String type;
        public String token;
    }

    public static class Registration {
        public String productId;
        public String deviceSerialNumber;
    }

    public static class Connection {
        public String type;
        public String macAddress;
        public String homeId;
        public String nodeId;
        public String value;
    }

    public static class AdditionalAttributes {
        public String manufacturer;
        public String model;
        public String serialNumber;
        public String firmwareVersion;
        public String softwareVersion;
        public String customIdentifier;
    }

    public static class Cookie {
    }

    public static class Capability {
        public static class Configurations {
            public static class MaximumAlerts {
                public int overall;
                public int alarms;
                public int timers;
            }

            public static class WakeWord {
                public List<String> scopes;
                public List<List<String>> values;
            }

            public static class FingerPrint {
                @SerializedName("package")
                public String packageName;
                public String buildType;
                public String versionNumber;
            }

            public MaximumAlerts maximumAlerts;
            public List<WakeWord> wakeWords;
            public List<String> locales;
            public List<List<String>> localeCombinations;
            // AudioPlay
            public FingerPrint fingerprint;
        }

        public static class Properties {
            public static class Supported {
                public String name;
            }

            public List<Supported> supported;
            public Boolean proactivelyReported;
            public Boolean retrievable;
        }
        public String type;
        @SerializedName("interface")
        public String inf;
        public String version;
        public Properties properties;
        public Configurations configurations;

        public static class Builder {
            private Capability capability = new Capability();

            public Capability build() {
                return capability;
            }

            public Builder setType(String type) {
                capability.type = type;
                return this;
            }

            public Builder setInterface(String inf) {
                capability.inf = inf;
                return this;
            }

            public Builder setVersion(String version) {
                capability.version = version;
                return this;
            }

            public Builder setProperties(Properties properties) {
                capability.properties = properties;
                return this;
            }

            public Builder setConfigurations(Configurations configurations) {
                capability.configurations = configurations;
                return this;
            }
        }
    }

    public String endpointId;
    public Scope scope;
    public Registration registration;
    public String manufacturerName;
    public String description;
    public String friendlyName;
    public List<String> displayCategories;
    public AdditionalAttributes additionalAttributes;
    public Cookie cookie;
    public List<Capability> capabilities;
    public List<Connection> connections;
    public String endpointType;
}
