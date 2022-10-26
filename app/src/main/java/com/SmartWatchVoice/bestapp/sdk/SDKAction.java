package com.SmartWatchVoice.bestapp.sdk;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import jie.android.alexahelper.smartwatchsdk.SmartWatchSDK;
import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;

public class SDKAction {
    public static SmartWatchSDK sdk = null;

//    public static String dialogId = null;

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
}
