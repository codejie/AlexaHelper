package com.SmartWatchVoice.bestapp.handler.directive.alerts;

import androidx.annotation.NonNull;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.alerts.SetAlertSucceededAction;
import com.SmartWatchVoice.bestapp.alexa.api.Directive;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.handler.directive.DirectiveHandler;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.channel.DirectiveParser;

import java.util.List;

public class SetAlertHandler extends DirectiveHandler {
    @Override
    public void handle(Directive directive, List<DirectiveParser.Part> parts) {
        // download asset
        for (final Payload.Asset asset : directive.payload.assets) {
            SettingInfo.getInstance().alertInfo.setAsset(asset.assetId, asset.url, null);
        }
        // set alert
        SettingInfo.getInstance().alertInfo.setAlert(directive.payload);

        new SetAlertSucceededAction(directive.payload.token)
                .create().post();
    }
}
