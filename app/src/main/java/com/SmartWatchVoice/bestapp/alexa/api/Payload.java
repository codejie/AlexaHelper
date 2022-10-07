package com.SmartWatchVoice.bestapp.alexa.api;

import java.util.List;

public class Payload {
    public static class Scope {
        public String type;
        public String token;
    }

//    public static class Endpoint {
//        public static class Registration {
//            public String productId;
//            public String deviceSerialNumber;
//        }
//
//        public static class Connection {
//            public String type;
//            public String macAddress;
//            public String homeId;
//            public String nodeId;
//            public String value;
//        }
//
//        public static class AdditionalAttributes {
//            public String manufacturer;
//            public String model;
//            public String serialNumber;
//            public String firmwareVersion;
//            public String softwareVersion;
//            public String customIdentifier;
//        }
//
//        public String endpointId;
//        public Registration registration;
//        public String manufacturerName;
//        public String description;
//        public String friendlyName;
//        public List<String> displayCategories;
//        public AdditionalAttributes additionalAttributes;
//        public List<Capability> capabilities;
//        public List<Connection> connections;
//        public String endpointType;
//        public Scope scope;
//    }

    public static class Initiator {
        public static class InitiatorPayload {
            public static class WakeWordIndices {
                public long startIndexInSamples;
                public long endIndexInSamples;
            }

            public WakeWordIndices wakeWordIndices;
            public String wakeWord;
            public String token;
        }
        public String type;
        public InitiatorPayload payload;
    }

    public static class Caption {
        public String content;
        public String type;
    }

    public static class NetworkInfo {
        public String connectionType;
        public String ESSID;
        public String BSSID;
        public String IPAddress;
        public String subnetMask;
        public String MACAddress;
        public String DHCPServerAddress;
        public boolean staticIP;
    }

    public static class State {
        public Header header;
        public Payload payload;
    }

    public static class Asset {
        public String assetId;
        public String url;
    }

    public static class AudioItem {
        public class Stream {
            public String url;
            public String token;
            public Long offsetInMilliseconds;
            public String expiryTime;
        }

        public String audioItemId;
        public Stream stream;
    }

    public static class Title {
        public String mainTitle;
        public String subTitle;
    }

    public static class Image {
        public static class Source {
            public String url;
            public String darkBackgroundUrl;
            public String size;
            public Long widthPixels;
            public Long heightPixels;
        }

        public String contentDescription;
        public List<Source> sources;
    }


    //    public String token;
    public Scope scope;
    public List<Endpoint> endpoints;
    // SpeechRecognizer Event
    public String profile;
    public String format;
    public Initiator initiator;
    public String startOfSpeechTimestamp;
    // SpeechSynthesizer Speak Directive
    public String url;
    public String token;
    public String playBehavior;
    public Caption caption;
    // TimeZoneReport
    public String timeZone;
    // LocalesReport
    public  List<String> locales;
    // NetworkInfoReport
    public NetworkInfo networkInfo;
    // UserInactivityReport
    public Long inactiveTimeInSeconds = null;
    // StateReport
    public List<State> states;
    // DoNotDisturb
    public Boolean enabled = null;
    // SetAlert
    public String type;
    public String scheduledTime;
    public List<Asset> assets;
    public List<String> assetPlayOrder;
    public String backgroundAlertAsset;
    public Long loopCount;
    public Long loopPauseInMilliSeconds;
    public String label;
    public String originalTime;
    // DeleteAlerts
    public List<String> tokens;
    // ExpectSpeech
    public Long timeoutInMilliseconds;
    // Play
    public AudioItem audioItem;
    // RenderTemplate
    public Title title;
    public String textField;
    public Image image;
}
