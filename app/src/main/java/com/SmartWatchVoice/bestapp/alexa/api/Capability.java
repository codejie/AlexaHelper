package com.SmartWatchVoice.bestapp.alexa.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Deprecated
public class Capability {
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

    public String type;
    @SerializedName("interface")
    public String inf;
    public String version;
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

        public Builder setConfigurations(Configurations configurations) {
            capability.configurations = configurations;
            return this;
        }
    }
}
