package com.SmartWatchVoice.bestapp.handler.context;

import com.SmartWatchVoice.bestapp.alexa.api.Context;

public class SpeechStateContextPayload extends Context.Payload {
    public String token;
    public long offsetInMilliseconds;
    public String playerActivity;
}
