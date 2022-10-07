package com.SmartWatchVoice.bestapp.handler.context;

import com.SmartWatchVoice.bestapp.alexa.api.Context;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.setting.AlertInfo;

import java.util.ArrayList;
import java.util.List;

public class AlertsStateContextPayload extends Context.Payload {
    public static class Alert {
        public String token;
        public String type;
        public String scheduledTime;
    }
    public List<Alert> allAlerts;
    public List<Alert> activeAlerts;

    public static List<Alert> getContextAllAlerts() {
        List<Alert> ret = new ArrayList<>();
        SettingInfo.getInstance().alertInfo.getAlerts().forEach(item -> {
            Alert alert = new Alert();
            alert.token = item.token;
            alert.type = item.type;
            alert.scheduledTime = item.scheduledTime;
            ret.add(alert);
        });
        return ret;
    }

    public static List<Alert> getContextActiveAlerts() {
        List<Alert> ret = new ArrayList<>();
        SettingInfo.getInstance().alertInfo.getAlerts().forEach(item -> {
            if (item.state == AlertInfo.State.ACTIVE) {
                Alert alert = new Alert();
                alert.token = item.token;
                alert.type = item.type;
                alert.scheduledTime = item.scheduledTime;
                ret.add(alert);
            }
        });
        return ret;
    }
}
