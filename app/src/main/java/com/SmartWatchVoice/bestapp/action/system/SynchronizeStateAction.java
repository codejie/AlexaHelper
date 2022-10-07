package com.SmartWatchVoice.bestapp.action.system;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.alexa.api.*;
import com.SmartWatchVoice.bestapp.handler.context.ContextConfig;

import java.util.ArrayList;


public class SynchronizeStateAction extends EventAction {
    @Override
    public EventAction create() {

        wrapper.event = (Event) new Event.Builder()
                .setHeaderNamespace(ApiConst.NS_SYSTEM)
                .setHeaderName(ApiConst.NAME_SYNCHRONIZE_STATE)
                .setHeaderMessageId(makeMessageId())
//                .setContext(ContextConfig.makeContextList()) // Arrays.asList(ContextConfig.getNotifications()))
                .build();
//        wrapper.setContext(ContextConfig.makeContextList());

        return this;
    }
}
