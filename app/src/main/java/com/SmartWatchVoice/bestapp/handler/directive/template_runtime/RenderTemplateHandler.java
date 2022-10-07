package com.SmartWatchVoice.bestapp.handler.directive.template_runtime;

import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.util.List;

public class RenderTemplateHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        if (directive.payload.type.equals("BodyTemplate2") || directive.payload.type.equals("BodyTemplate1")) {
            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_TEMPLATE_RENDER, directive.payload);
        } else {
            Logger.w("unsupported template reader type - " + directive.payload.type);
        }
    }
}
