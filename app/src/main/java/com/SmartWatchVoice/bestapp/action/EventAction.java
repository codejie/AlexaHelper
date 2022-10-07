package com.SmartWatchVoice.bestapp.action;

import androidx.annotation.NonNull;

import com.SmartWatchVoice.bestapp.alexa.api.Event;
import com.SmartWatchVoice.bestapp.alexa.api.Payload;
import com.SmartWatchVoice.bestapp.system.channel.HttpChannel;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

public abstract class EventAction {
    public interface OnChannelResponse {
        public void OnResponse(@NonNull Response response);
    }

    public static class Attachment {
        public String name;
        public RequestBody body;

        public Attachment(String name, RequestBody body) {
            this.name = name;
            this.body = body;
        }
    }

    protected Event.EventWrapper wrapper = new Event.EventWrapper();

    protected List<Attachment> attachments = new ArrayList<>();

    public abstract EventAction create();

    public EventAction setPayload(Payload payload) {
            wrapper.event.payload = payload;
            return this;
    }

    public EventAction addAttachment(String name, File file, String mediaType) {
        RequestBody body = RequestBody.create(file, MediaType.parse(mediaType));
        attachments.add(new Attachment(name, body));

        return this;
    }

    public void post() {
        post(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Logger.w("post " + wrapper.event.toString() + " failed - \n" + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Logger.d("post " + wrapper.event.toString() + " response - " + response.code());
                response.close();
            }
        });
    }

    public void post(OnChannelResponse onResponse) {
        post(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Logger.w("post " + wrapper.event.toString() + " failed - " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                onResponse.OnResponse(response);
                response.close();
            }
        });
    }

    public void post(Callback callback) {
        RequestBody body = RequestBody.create(new Gson().toJson(wrapper), MediaType.parse("application/json;charset=utf-8"));
        Map<String, RequestBody> bodies = new HashMap<>();
        bodies.put("metadata", body);

        attachments.forEach(attachment -> {
            bodies.put(attachment.name, attachment.body);
        });

        HttpChannel.getInstance().postRequests(bodies, callback);
    }

    protected String makeMessageId() {
        return UUID.randomUUID().toString();
    }
}
