package com.SmartWatchVoice.bestapp.action.speech_recognizer;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;

public class ExpectSpeechTimeOutAction extends EventAction {
    @Override
    public EventAction create() {
        Payload payload = new Payload();

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SPEECH_RECOGNIZER)
                .setHeaderName(ApiConst.NAME_EXPECT_SPEECH_TIMEOUT)
                .setHeaderMessageId(makeMessageId())
                .setPayload(payload)
                .build();

        return this;
    }
}
