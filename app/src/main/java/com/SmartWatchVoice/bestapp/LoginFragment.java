package com.SmartWatchVoice.bestapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.SmartWatchVoice.bestapp.databinding.FragmentLoginBinding;
import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.HandlerConst;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.utils.Utils;


public class LoginFragment extends Fragment {

    private FragmentLoginBinding binding;

    private Handler handler = null;


    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        handler = Handler.createAsync(this.getActivity().getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what) {
                    case HandlerConst.MSG_LOG:
                        showLogInfo((String)message.obj);
                        break;
                    case HandlerConst.MSG_LOGIN_SUCCESS:
                        navigateSpeechFragment();
                        break;
                    case HandlerConst.MSG_LOGIN_FAIL:
                        onLoginFail();
                        break;
                }
                return true;
            }
        });
        RuntimeInfo.getInstance().loginFragmentHandler = handler;

        binding = FragmentLoginBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DeviceInfo.ProductSerialNumber == null) {
                    binding.loginLayoutSerial.setVisibility(View.VISIBLE);
                    String content = binding.editSerial.getText().toString();
                    if (content.length() >= 4) {
                        binding.loginLayoutSerial.setVisibility(View.GONE);
                        DeviceInfo.flush(getActivity(), content);
                        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_FETCH_TOKEN);
                        showLogInfo("start to fetch token..");
                    }
                } else {
                    //                Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_FETCH_TOKEN).sendToTarget();
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_FETCH_TOKEN);
                    showLogInfo("start to fetch token..");
                }
            }
        });

        binding.buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RuntimeInfo.getInstance().resetAuthInfo();
                showLogInfo("...");
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void showLogInfo(String info) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (binding != null)
                    binding.textLoginLog.setText(info);
            }
        });
    }

    private void navigateSpeechFragment() {
        NavHostFragment.findNavController(LoginFragment.this)
                .navigate(R.id.action_loginFragment_to_homeFragment);
        showLogInfo("...");
    }

    private void onLoginFail() {
        RuntimeInfo.getInstance().resetAuthInfo();
        showLogInfo("Login fail - please retry.");
    }
}