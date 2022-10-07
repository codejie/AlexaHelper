package com.SmartWatchVoice.bestapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.SmartWatchVoice.bestapp.action.EventAction;
import com.SmartWatchVoice.bestapp.action.alexa_do_not_disturb.DoNotDisturbChangedAction;
import com.SmartWatchVoice.bestapp.databinding.FragmentHomeBinding;
import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.utils.Logger;

import okhttp3.Response;

public class HomeFragment extends Fragment {

    private Handler handler = null;
    private FragmentHomeBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        handler = Handler.createAsync(this.getActivity().getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                    case HandlerConst.SETTING_DO_NOT_DISTURB:
                        onSettingDoNotDisturb();
                        break;
                    case HandlerConst.MSG_SETTING_CHANGED:
                        updateSettingUI();
                        break;
                    default:
                        Logger.w("Unknown message - " + message.what);
                }
                return true;
            }
        });
        RuntimeInfo.getInstance().homeFragmentHandler = handler;

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.buttonHomeSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_speechFragment);
            }
        });
        binding.buttonHomeAlerts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_alertsFragment);
            }
        });

        binding.switchHomeDnd.setChecked(SettingInfo.getInstance().doNotDisturb);
        binding.switchHomeDnd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                new DoNotDisturbChangedAction(b).create().post(new EventAction.OnChannelResponse() {
                    @Override
                    public void OnResponse(@NonNull Response response) {
                        Logger.d("change DoNotDisturb - " + b);
                        SettingInfo.getInstance().doNotDisturb = b;
                    }
                });
            }
        });
        binding.buttonHomeLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_settingLocaleFragment);
            }
        });

        binding.buttonHomeTimezone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(HomeFragment.this).navigate(R.id.action_homeFragment_to_settingTimeZoneFragment);
            }
        });

        updateSettingUI();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onSettingDoNotDisturb() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.switchHomeDnd.setChecked(SettingInfo.getInstance().doNotDisturb);
            }
        });
    }

    private void updateSettingUI() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (binding != null) {
                    binding.buttonHomeTimezone.setText("TimeZone:" + SettingInfo.getInstance().timeZone);
                    binding.buttonHomeLanguage.setText("Language:" + SettingInfo.getInstance().locales.get(0));
                }
            }
        });
    }


}