package com.SmartWatchVoice.bestapp.handler.directive.audio_player;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class PlayHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        if (directive.payload.playBehavior.equals("ENQUEUE")) {
            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_AUDIO_PLAY_ENQUEUE, directive.payload.audioItem.stream.url);
        }
    }
}
