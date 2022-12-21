package com.SmartWatchVoice.bestapp.system.setting;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class AlertInfo implements SettingItem {
    public enum State {
        DISABLED,
        ENABLED,
        ACTIVE
    }

    public static class Alert {
        public State state;
        public String token;
        public String type;
        public String scheduledTime;
        public List<String> assetIds;
        public List<String> playOrder;
        public String backgroundAssetId;
        public Long loopCount;
        public Long loopPauseInMilliSeconds;
        public String label;
        public String originalTime;
    }

    public static class Asset {
        public String assetId;
        public String url;
        public String filename;
    }

    private List<Alert> alerts = new LinkedList<>();
    private Map<String, Asset> assets = new HashMap<>();

    @Override
    public void load() {

    }

    @Override
    public void flush() {

    }

    public List<Alert> getAlerts() {
        return alerts;
    }

//    public List<Context.Payload.Alert> getContextAllAlerts() {
//        List<Context.Payload.Alert> ret = new ArrayList<>();
//        alerts.forEach(item -> {
//            Context.Payload.Alert alert = new Context.Payload.Alert();
//            alert.token = item.token;
//            alert.type = item.type;
//            alert.scheduledTime = item.scheduledTime;
//            ret.add(alert);
//        });
//        return ret;
//    }
//
//    public List<Context.Payload.Alert> getContextActiveAlerts() {
//        List<Context.Payload.Alert> ret = new ArrayList<>();
//        alerts.forEach(item -> {
//            if (item.state == State.ACTIVE) {
//                Context.Payload.Alert alert = new Context.Payload.Alert();
//                alert.token = item.token;
//                alert.type = item.type;
//                alert.scheduledTime = item.scheduledTime;
//                ret.add(alert);
//            }
//        });
//        return ret;
//    }
//
//    public void setAlert(final Payload payload) {
//        alerts.removeIf(item -> item.token.equals(payload.token));
//
//        Alert alert = new Alert();
//        alert.token = payload.token;
//        alert.type = payload.type;
//        alert.state = State.ENABLED;
//        alert.scheduledTime = payload.scheduledTime;
//        alert.assetIds = new ArrayList<String>();
//        if (payload.assets != null) {
//            payload.assets.forEach(item -> {
//                alert.assetIds.add(item.assetId);
//            });
//        }
//        alert.playOrder = payload.assetPlayOrder;
//        alert.backgroundAssetId = payload.backgroundAlertAsset;
//        alert.loopCount = payload.loopCount;
//        alert.loopPauseInMilliSeconds = payload.loopPauseInMilliSeconds;
//        alert.label = payload.label;
//        alert.originalTime = payload.originalTime;
//
//        alerts.add(alert);
//    }

    public void setAlert(String token, String type, String scheduledTime, Long loopCount, Long loopPause,
                         String label) {
        alerts.removeIf(item -> item.token.equals(token));

        Alert alert = new Alert();
        alert.token = token;
        alert.type = type;
        alert.state = State.ENABLED;
        alert.scheduledTime = scheduledTime;
        alert.loopCount = loopCount;
        alert.loopPauseInMilliSeconds = loopPause;
        alert.label = label;
//        alert.originalTime = startTime;

        alerts.add(alert);
    }

    public void setAsset(String id, String url, String filename) {
        if (assets.get(id) == null) {
            Asset asset = new Asset();
            asset.assetId = id;
            asset.url = url;
            asset.filename = filename;

            assets.put(id, asset);
        }
    }

    public void deleteAlert(String token) {
        alerts.removeIf(item -> item.token.equals(token));
    }

    public void deleteAlerts(List<String> tokens) {
        for (final String token: tokens) {
            alerts.removeIf(item -> item.token.equals(token));
        }
    }
}
