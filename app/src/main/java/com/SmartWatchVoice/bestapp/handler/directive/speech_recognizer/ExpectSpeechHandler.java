package com.SmartWatchVoice.bestapp.handler.directive.speech_recognizer;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class ExpectSpeechHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_EXPECT_SPEECH, directive.payload.timeoutInMilliseconds);
    }
}
