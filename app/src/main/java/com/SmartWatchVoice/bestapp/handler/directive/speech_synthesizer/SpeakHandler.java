package com.SmartWatchVoice.bestapp.handler.directive.speech_synthesizer;

import android.os.Message;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class SpeakHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
//        Message.obtain(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_RESET_VIEW);

        if (directive.payload.url != null) {
            String[] octet = directive.payload.url.split(":");
            DirectiveParser.OctetFilePart octetStream = (DirectiveParser.OctetFilePart) findPartByHeader(parts, "Content-ID", "<" + octet[1] + ">");
            if (octetStream == null) {
                Logger.w("Can't find stream - " + octet[1]);
                return;
            }
//            Message.obtain(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_AUDIO_FILE, octetStream.getFile()).sendToTarget();
            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_AUDIO_FILE, octetStream.getFile());
        }

        if (directive.payload.caption != null) {
//            Message.obtain(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_WEB_VTT, directive.payload.caption).sendToTarget();
            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_WEB_VTT, directive.payload.caption);
        }
    }
}
