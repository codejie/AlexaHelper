package com.SmartWatchVoice.bestapp.alexa.config;

import com.SmartWatchVoice.bestapp.alexa.api.Capability;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;

import java.util.Arrays;

@Deprecated
public class CapabilityConfig {
    public static final Capability getAlexa() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa")
                .setVersion("3")
                .build();
    }

    public static final Capability getAlexaApiGateway() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa.ApiGateway")
                .setVersion("1.0")
                .build();
    }

    public static final Capability getNotifications() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("Notifications")
                .setVersion("1.0")
                .build();
    }

    public static final Capability getAlexaDoNotDisturb() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("Alexa.DoNotDisturb")
                .setVersion("1.0")
                .build();
    }

    public static final Capability getAlerts() {
        Capability.Configurations.MaximumAlerts maximumAlerts = new Capability.Configurations.MaximumAlerts();
        maximumAlerts.overall = 10;
        maximumAlerts.alarms = 5;
        maximumAlerts.timers = 5;

        Capability.Configurations configurations = new Capability.Configurations();
        configurations.maximumAlerts = maximumAlerts;

        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("Alerts")
                .setVersion("1.5")
                .setConfigurations(configurations)
                .build();
    }

    public static final Capability getSystem() {
        Capability.Configurations configurations = new Capability.Configurations();
        configurations.locales = DeviceInfo.SupportedLocales; // Arrays.asList("en-GB", "en-CA", "en-US", "en-ES", "fr-FR", "it-IT");
        configurations.localeCombinations = DeviceInfo.SupportedLocaleCombinations;

        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("System")
                .setVersion("2.1")
                .setConfigurations(configurations)
                .build();
    }

    public static final Capability getSpeedRecognizer() {
        Capability.Configurations.WakeWord wakeWord = new Capability.Configurations.WakeWord();
        wakeWord.scopes = Arrays.asList("DEFAULT");
        wakeWord.values = Arrays.asList(Arrays.asList("ALEXA"));

        Capability.Configurations configurations = new Capability.Configurations();
        configurations.wakeWords = Arrays.asList(wakeWord);

        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("SpeedRecognizer")
                .setVersion("2.3")
                .setConfigurations(configurations)
                .build();
    }

    public static final Capability getSpeechSynthesizer() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("SpeechSynthesizer")
                .setVersion("1.3")
                .build();
    }

    public static final Capability getAudioPlayer() {
        Capability.Configurations configurations = new Capability.Configurations();
        configurations.fingerprint = new Capability.Configurations.FingerPrint();
        configurations.fingerprint.packageName = "android.media.MediaPlayer";
        configurations.fingerprint.buildType = "DEBUG";
        configurations.fingerprint.versionNumber = "1.0";

        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("AudioPlayer")
                .setVersion("1.4")
                .setConfigurations(configurations)
                .build();
    }

    public static final Capability getTemplateRuntime() {
        return new Capability.Builder().setType("AlexaInterface")
                .setInterface("TemplateRuntime")
                .setVersion("1.2")
                .build();
    }
}
