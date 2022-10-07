package com.SmartWatchVoice.bestapp.system.thing;

import com.SmartWatchVoice.bestapp.alexa.api.Endpoint;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;

import java.util.Arrays;

public class Helper {
    public static final Endpoint.Capability getAlexa() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa")
                .setVersion("3")
                .build();
    }

    public static final Endpoint.Capability getAlexaApiGateway() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa.ApiGateway")
                .setVersion("1.0")
                .build();
    }

    public static final Endpoint.Capability getNotifications() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Notifications")
                .setVersion("1.0")
                .build();
    }

    public static final Endpoint.Capability getAlexaDoNotDisturb() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa.DoNotDisturb")
                .setVersion("1.0")
                .build();
    }

    public static final Endpoint.Capability getAlerts() {
        Endpoint.Capability.Configurations.MaximumAlerts maximumAlerts = new Endpoint.Capability.Configurations.MaximumAlerts();
        maximumAlerts.overall = 10;
        maximumAlerts.alarms = 5;
        maximumAlerts.timers = 5;

        Endpoint.Capability.Configurations configurations = new Endpoint.Capability.Configurations();
        configurations.maximumAlerts = maximumAlerts;

        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Alerts")
                .setVersion("1.5")
                .setConfigurations(configurations)
                .build();
    }

    public static final Endpoint.Capability getSystem() {
        Endpoint.Capability.Configurations configurations = new Endpoint.Capability.Configurations();
        configurations.locales = DeviceInfo.SupportedLocales; // Arrays.asList("en-GB", "en-CA", "en-US", "en-ES", "fr-FR", "it-IT");
        configurations.localeCombinations = DeviceInfo.SupportedLocaleCombinations;

        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("System")
                .setVersion("2.1")
                .setConfigurations(configurations)
                .build();
    }

    public static final Endpoint.Capability getSpeedRecognizer() {
        Endpoint.Capability.Configurations.WakeWord wakeWord = new Endpoint.Capability.Configurations.WakeWord();
        wakeWord.scopes = Arrays.asList("DEFAULT");
        wakeWord.values = Arrays.asList(Arrays.asList("ALEXA"));

        Endpoint.Capability.Configurations configurations = new Endpoint.Capability.Configurations();
        configurations.wakeWords = Arrays.asList(wakeWord);

        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("SpeedRecognizer")
                .setVersion("2.3")
                .setConfigurations(configurations)
                .build();
    }

    public static final Endpoint.Capability getSpeechSynthesizer() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("SpeechSynthesizer")
                .setVersion("1.3")
                .build();
    }

    public static final Endpoint.Capability getAudioPlayer() {
        Endpoint.Capability.Configurations configurations = new Endpoint.Capability.Configurations();
        configurations.fingerprint = new Endpoint.Capability.Configurations.FingerPrint();
        configurations.fingerprint.packageName = "android.media.MediaPlayer";
        configurations.fingerprint.buildType = "DEBUG";
        configurations.fingerprint.versionNumber = "1.0";

        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("AudioPlayer")
                .setVersion("1.4")
                .setConfigurations(configurations)
                .build();
    }

    public static final Endpoint.Capability getTemplateRuntime() {
        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("TemplateRuntime")
                .setVersion("1.2")
                .build();
    }

    public static final Endpoint.Capability getAlexaPowerController() {
        Endpoint.Capability.Properties.Supported powerState = new Endpoint.Capability.Properties.Supported();
        powerState.name = "powerState";

        Endpoint.Capability.Properties properties = new Endpoint.Capability.Properties();
        properties.supported = Arrays.asList(powerState);
        properties.proactivelyReported = false;
        properties.retrievable = false;

        return new Endpoint.Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa.PowerController")
                .setVersion("3")
                .setProperties(properties)
                .build();
    }
}
