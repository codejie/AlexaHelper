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
import com.SmartWatchVoice.bestapp.system.DeviceInfo;

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
}