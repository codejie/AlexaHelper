package com.SmartWatchVoice.bestapp.handler.context;

import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Context;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.Arrays;
import java.util.List;

public class ContextConfig {
    public static final Context getNotifications() {
        IndicatorStateContextPayload payload = new IndicatorStateContextPayload();
        payload.isEnabled = false; // SettingInfo.getInstance().doNotDisturb;
        payload.isVisualIndicatorPersisted = false; // SettingInfo.getInstance().indicator;

        return new Context.Builder<IndicatorStateContextPayload>().setHeaderNamespace(ApiConst.NS_NOTIFICATIONS)
                .setHeaderName(ApiConst.NAME_INDICATOR_STATE)
                .setPayload(payload)
                .build();
    }

    public static final Context getSpeechSynthesizer() {
        SpeechStateContextPayload payload = new SpeechStateContextPayload();
//        Context.Payload payload = new Context.Payload();
        payload.token = Utils.makeToken(); // Context.makeToken();
        payload.offsetInMilliseconds = 10;
        payload.playerActivity = "FINISHED";

        return new Context.Builder<SpeechStateContextPayload>().setHeaderNamespace(ApiConst.NS_SPEECH_SYNTHESIZER)
                .setHeaderName(ApiConst.NAME_SPEECH_STATE)
                .setPayload(payload)
                .build();
    }

    public static final Context getAlerts() {
        AlertsStateContextPayload payload = new AlertsStateContextPayload();
//        Context.Payload payload = new Context.Payload();
        payload.allAlerts = AlertsStateContextPayload.getContextAllAlerts();

        payload.activeAlerts = AlertsStateContextPayload.getContextActiveAlerts();

        return new Context.Builder<AlertsStateContextPayload>().setHeaderNamespace(ApiConst.NS_ALERTS)
                .setHeaderName(ApiConst.NAME_ALERTS_STATE)
                .setPayload(payload)
                .build();
    }

    public static final Context getRecognizerState() {
        RecognizerStateContextPayload payload = new RecognizerStateContextPayload();
        payload.wakeword = "ALEXA";

        return new Context.Builder<AlertsStateContextPayload>().setHeaderNamespace(ApiConst.NS_SPEECH_RECOGNIZER)
                .setHeaderName(ApiConst.NAME_RECOGNIZE_STATE)
                .setPayload(payload)
                .build();
    }


    public static final List<Context> makeContextList() {
        return Arrays.asList(getNotifications(),
                getSpeechSynthesizer(),
                getAlerts(),
                getRecognizerState());
    }
}
