package com.SmartWatchVoice.bestapp.sdk;

import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK;
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;

public class SDKAction {
    public static SmartWatchSDK sdk = null;

    public static void getSDKInfo(OnResultCallback callback) {
        JSONObject info = new JSONObject();
        try {
            info.put("type", "action");
            info.put("name", "sdk.getInfo");
            info.put("version", 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sdk.action(info.toString(), null, callback);
    }

    public static void setDeviceInfo(OnResultCallback callback) {
        JSONObject product = new JSONObject();
        JSONArray endpoints = new JSONArray();
        try {
//            JSONObject spot = new JSONObject();
//            spot.put("id", "lightSpot");
//            spot.put("serialNumber", "spot-111");
//            spot.put("friendlyName", "spot");
//            spot.put("description", "LightSpot model 1");
//            spot.put("firmware", "1.1");
//            spot.put("software", "20221120");
//
//            endpoints.put(spot);

            JSONObject mode = new JSONObject();
            mode.put("id", "garageDoor");
            mode.put("serialNumber", "GarageDoor-333");
            mode.put("friendlyName", "GarageDoor");
            mode.put("description", "GarageDoor Controller");
            mode.put("firmware", "1.0");
            mode.put("software", "20221201");

            endpoints.put(mode);

            JSONObject lightMode = new JSONObject();
            lightMode.put("id", "lightMode");
            lightMode.put("serialNumber", "LightMode-333");
            lightMode.put("friendlyName", "LightMode");
            lightMode.put("description", "Light Mode Controller");
            lightMode.put("firmware", "1.0");
            lightMode.put("software", "20221201");

            endpoints.put(lightMode);

//            JSONObject toggle = new JSONObject();
//            toggle.put("id", "lockSwitch");
//            toggle.put("serialNumber", "LockSwitch-222");
//            toggle.put("friendlyName", "LSwitch");
//            toggle.put("description", "Lock Controller");
//            toggle.put("firmware", "1.2");
//            toggle.put("software", "20221122");
//
//            endpoints.put(toggle);
//
//            JSONObject washer = new JSONObject();
//            washer.put("id", "washer");
//            washer.put("serialNumber", "Washer-333");
//            washer.put("friendlyName", "Washee");
//            washer.put("description", "Mode Controller");
//            washer.put("firmware", "1.2");
//            washer.put("software", "20221127");
//
//            endpoints.put(washer);

//            product.put("id", DeviceInfo.ProductId);
//            product.put("clientId", DeviceInfo.ClientId);
            product.put("serialNumber", DeviceInfo.ProductSerialNumber);
//            product.put("name", "TouchAlexa");
            product.put("friendlyName", "TouchAce");
            product.put("description", "Touch Self");
//            product.put("manufacturer", "TouchManufacturer");
//            product.put("model", "Touch-1");
            product.put("firmware", "1.0");
            product.put("software", "20221020");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject payload = new JSONObject();
        try {
            payload.put("product", product);
            payload.put("endpoints", endpoints);
//            payload.put("manufacturer", manufacturer);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject info = new JSONObject();
        try {
            info.put("type", "action");
            info.put("name", "device.setInfo");
            info.put("version", 1);
            info.put("payload", payload);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sdk.action(info.toString(), null, callback);
    }

    public static void speechStart(OnResultCallback callback) {
        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.speechStart");
        action.addProperty("version", 1);

        sdk.action(action.toString(), null, callback);
    }

    public static void speechStop(String dialogId, OnResultCallback callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("dialogId", dialogId);

        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.speechStop");
        action.addProperty("version", 1);
        action.add("payload", payload);

        sdk.action(action.toString(), null, callback);
    }

    public static void speechRecognize(String dialogId, File file, OnResultCallback callback) {

        try {
            byte[] buffer = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

            JsonObject payload = new JsonObject();
            payload.addProperty("dialogId", dialogId);

            JsonObject action = new JsonObject();
            action.addProperty("type", "action");
            action.addProperty("name", "alexa.speechRecognize");
            action.addProperty("version", 1);
            action.add("payload", payload);


            sdk.action(action.toString(), buffer, callback);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setTimeZone(String timeZone, OnResultCallback callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("timeZone", timeZone);

        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.setTimeZone");
        action.addProperty("version", 1);
        action.add("payload", payload);

        sdk.action(action.toString(), null, callback);
    }

    public static void setDND(Boolean enabled, OnResultCallback callback) {
        JsonObject payload = new JsonObject();
        payload.addProperty("enabled", enabled);

        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.setDoNotDisturb");
        action.addProperty("version", 1);
        action.add("payload", payload);

        sdk.action(action.toString(), null, callback);
    }

    public static void setLocales(String locale, OnResultCallback callback) {
        JsonArray values = new JsonArray();
        values.add(locale);

        JsonObject payload = new JsonObject();
        payload.add("locales", values);

        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.setLocales");
        action.addProperty("version", 1);
        action.add("payload", payload);

        sdk.action(action.toString(), null, callback);
    }

    public static void verifyGateway(OnResultCallback callback) {
        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "alexa.verifyGateway");
        action.addProperty("version", 1);

        sdk.action(action.toString(), null, callback);
    }

    public static void syncState(OnResultCallback callback) {
        JsonObject speakerVolume = new JsonObject();
        speakerVolume.addProperty("name", "volume");
        speakerVolume.addProperty("value", 100);

        JsonObject speakerMuted = new JsonObject();
        speakerMuted.addProperty("name", "muted");
        speakerMuted.addProperty("value", 0);

        JsonArray speakerItems = new JsonArray();
        speakerItems.add(speakerVolume);
        speakerItems.add(speakerMuted);

        JsonObject speaker = new JsonObject();
        speaker.addProperty("name", "speaker");
        speaker.add("items", speakerItems);

        // alert
        JsonArray allAlertsValue = new JsonArray();
        SettingInfo.getInstance().alertInfo.getAlerts().forEach(alert -> {
            JsonObject item = new JsonObject();
            item.addProperty("token", alert.token);
            item.addProperty("type", alert.type);
            item.addProperty("scheduledTime", alert.scheduledTime);

            allAlertsValue.add(item);
        });

        JsonObject allAlerts = new JsonObject();
        allAlerts.addProperty("name", "allAlerts");
        allAlerts.add("value", allAlertsValue);

        JsonArray alertItems = new JsonArray();
        alertItems.add(allAlerts);

        JsonObject alert = new JsonObject();
        alert.addProperty("name", "alert");
        alert.add("items", alertItems);

        JsonArray alexa = new JsonArray();
        alexa.add(speaker);
        alexa.add(alert);

        JsonObject payload = new JsonObject();
        payload.add("alexa", alexa);

        JsonObject action = new JsonObject();
        action.addProperty("type", "action");
        action.addProperty("name", "device.syncState");
        action.addProperty("version", 1);
        action.add("payload", payload);

        sdk.action(action.toString(), null, callback);
    }
}
