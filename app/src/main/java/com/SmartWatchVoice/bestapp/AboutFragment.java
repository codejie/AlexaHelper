package com.SmartWatchVoice.bestapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.SmartWatchVoice.bestapp.databinding.FragmentAboutBinding;
import com.SmartWatchVoice.bestapp.databinding.FragmentHomeBinding;
import com.SmartWatchVoice.bestapp.sdk.SDKAction;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;

public class AboutFragment extends Fragment {
    private FragmentAboutBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAboutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.textSerial.setText("Device Serial Number: " + DeviceInfo.ProductSerialNumber);
    }

    private void getSDKInfo() {
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                SDKAction.getSDKInfo(new OnResultCallback() {
                    @Override
                    public void onResult(@NonNull String data, @Nullable Object extra) {
                        JsonObject result = JsonParser.parseString(data).getAsJsonObject();
                        JsonObject payload = result.getAsJsonObject("payload");
                        String version = payload.get("version").getAsString();
                        String manufacturer = payload.get("manufacturer").getAsString();
                        String release = payload.get("release").getAsString();

                        binding.textView6.setText(version);
                        binding.textView7.setText(manufacturer);
                        binding.textView8.setText(release);
                    }
                });
            }
        });
    }
}