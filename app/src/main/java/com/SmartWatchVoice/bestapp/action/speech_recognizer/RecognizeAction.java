package com.SmartWatchVoice.bestapp.action.speech_recognizer;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.ApiConst;
import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.handler.context.ContextConfig;

public class RecognizeAction extends EventAction {

    @Override
    public EventAction create() {

        Payload payload = new Payload();
        payload.profile = "CLOSE_TALK";
        payload.format = "AUDIO_L16_RATE_16000_CHANNELS_1";

        payload.initiator = new Payload.Initiator();
        payload.initiator.type = "PRESS_AND_HOLD";

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SPEECH_RECOGNIZER)
                .setHeaderName(ApiConst.NAME_RECOGNIZE)
                .setHeaderMessageId(makeMessageId())
                .setHeaderDialogRequestId(makeMessageId())
                .setPayload(payload)
//                .setContext(ContextConfig.makeContextList())
                .build();
//        wrapper.setContext(ContextConfig.makeContextList());

        return this;
    }
}
