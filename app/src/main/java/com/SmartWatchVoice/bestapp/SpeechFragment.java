package com.SmartWatchVoice.bestapp;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.SmartWatchVoice.bestapp.sdk.SDKAction;
import com.SmartWatchVoice.bestapp.sdk.TemplateCardActionData;
import com.SmartWatchVoice.bestapp.databinding.FragmentSpeechBinding;
import com.SmartWatchVoice.bestapp.sdk.TemplateListActionData;
import com.SmartWatchVoice.bestapp.sdk.TemplateWeatherActionData;
import com.SmartWatchVoice.bestapp.system.HandlerConst;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.system.SettingInfo;
import com.SmartWatchVoice.bestapp.system.player.MPEGPlayer;
import com.SmartWatchVoice.bestapp.system.player.PCMRecorder;
import com.SmartWatchVoice.bestapp.system.player.PCMTracker;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.SmartWatchVoice.bestapp.utils.Utils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jie.android.alexahelper.smartwatchsdk.protocol.sdk.OnResultCallback;

public class SpeechFragment extends Fragment {

    private FragmentSpeechBinding binding;
    private Handler handler = null;

    private String pcmFilename = null;
    private List<String> mp3Filenames = new ArrayList<>();

    private String dialogId = null;

    private final Runnable expectSpeechTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Message.obtain(handler, HandlerConst.MSG_EXPECT_SPEECH_TIMEOUT).sendToTarget();
        }
    };

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
                    case HandlerConst.MSG_RESET_VIEW:
                        resetView();
                        break;
                    case HandlerConst.MSG_WEB_VTT:
                        onWebVTT((String)message.obj);
                        break;
                    case HandlerConst.MSG_AUDIO_FILE:
                        onAudioFile((String)message.obj);
                        break;
                    case HandlerConst.MSG_EXPECT_SPEECH:
                        onExpectSpeech((Long)message.obj);
                        break;
                    case HandlerConst.MSG_EXPECT_SPEECH_TIMEOUT:
                        onExpectSpeechTimeout();
                        break;
                    case HandlerConst.MSG_AUDIO_PLAY_ENQUEUE:
                        onAudioPlay(true, (String)message.obj);
                        break;
                    case HandlerConst.MSG_TEMPLATE_RENDER:
                        onTemplateRender((TemplateCardActionData)message.obj);
                        break;
                    case HandlerConst.MSG_LIGHT_SPOT_STATE:
                        onLightSpotState((String)message.obj);
                        break;
                    case HandlerConst.MSG_TEMPLATE_RENDER_LIST:
                        onTemplateRenderList((TemplateListActionData)message.obj);
                        break;
                    case HandlerConst.MSG_TEMPLATE_RENDER_WEATHER:
                        onTemplateRenderWeather((TemplateWeatherActionData)message.obj);
                        break;

                    default:;
                }
                return true;
            }
        });
        RuntimeInfo.getInstance().speechFragmentHandler = handler;

        binding = FragmentSpeechBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    private void onTemplateRenderWeather(TemplateWeatherActionData data) {
        SettingInfo.getInstance().templateWeatherActionData = data;

        NavHostFragment.findNavController(SpeechFragment.this).navigate(R.id.action_speechFragment_to_templateWeatherFragment);
    }

    private void onTemplateRenderList(TemplateListActionData data) {
        SettingInfo.getInstance().templateListData = data;

        NavHostFragment.findNavController(SpeechFragment.this).navigate(R.id.action_speechFragment_to_templateListFragment);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonSpeechRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        onSpeechRecordStart();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        onSpeechRecordEnd();
                        break;
                    default:;
                }
                return false;
            }
        });

        binding.buttonSpeechPcmPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pcmFilename != null) {
                    PCMTracker.getInstance().play(pcmFilename);
                }
            }
        });

        binding.buttonSpeech1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mp3Filenames.size() > 0) {
                    MPEGPlayer.getInstance().playFile(mp3Filenames.get(0));
                }
            }
        });

        binding.buttonSpeechTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                turnOnSpot();
            }
        });

        resetView();
    }

    private void turnOnSpot() {
        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_LIGHT_SPOT_STATE);
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
                binding.textSpeechLog.setText(info);
            }
        });
    }

    private void onSpeechRecordStart() {
        showLogInfo("Speech start..");
//        Message.obtain(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_RESET_VIEW).sendToTarget();
        Utils.sendToHandlerMessage(RuntimeInfo.getInstance().speechFragmentHandler, HandlerConst.MSG_RESET_VIEW);
        MPEGPlayer.getInstance().stop();

        SDKAction.speechStart(new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                JsonObject result = JsonParser.parseString(data).getAsJsonObject();
                Integer code = result.get("code").getAsInt();
                if (code == 0){
                    JsonObject payload = result.get("payload").getAsJsonObject();
                    dialogId = payload.get("dialogId").getAsString();

                    Logger.w("speechStart dialogId - " + dialogId);
                } else {
                    Logger.w("speechStart failed - " + result.get("message").getAsString());
                }
            }
        });


        PCMRecorder.getInstance().start();
    }

    private void onSpeechRecordEnd() {
        showLogInfo("Speech end.");
        pcmFilename = PCMRecorder.getInstance().stop();
        RuntimeInfo.getInstance().setPcmFilename(pcmFilename);

        File file = RuntimeInfo.getInstance().makeSpeechPCMFile(pcmFilename);


        SDKAction.speechRecognize(dialogId, file, new OnResultCallback() {
            @Override
            public void onResult(@NonNull String data, @Nullable Object extra) {
                Logger.d("speechRecognize result - " + data);
            }
        });

    }

    private void resetView() {
        handler.removeCallbacks(expectSpeechTimeoutRunnable);

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                binding.textSpeech.setText("");
                binding.layoutTemplate.setVisibility(View.GONE);
                binding.videoSpeech.setVisibility(View.GONE);
                binding.layoutButtonSpeech.setVisibility(View.GONE);
                binding.buttonSpeech2.setVisibility(View.GONE);
                binding.buttonSpeechPcmPlay.setVisibility(View.GONE);
                binding.imageExpectSpeech.clearAnimation();
                binding.imageExpectSpeech.setVisibility(View.INVISIBLE);
                binding.textSpeechLog.setText("...");
                binding.imageTemplate.setImageDrawable(null);
            }
        });

        pcmFilename = null;
        mp3Filenames.clear();
    }

    private void onWebVTT(String caption) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (binding != null)
                    binding.textSpeech.setText(caption);
            }
        });
    }

    private void onAudioFile(String filename) {
        MPEGPlayer.getInstance().playFile(filename);
        mp3Filenames.add(filename);
//        if (binding != null)
//            binding.layoutButtonSpeech.setVisibility(View.VISIBLE);
    }

    private void onExpectSpeech(Long timeout) {
        if (timeout != null) {
            handler.postDelayed(expectSpeechTimeoutRunnable, timeout);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.imageExpectSpeech.setVisibility(View.VISIBLE);
                    binding.imageExpectSpeech.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.blink));
                }
            });
        }
    }

    private void onExpectSpeechTimeout() {
//        new ExpectSpeechTimeOutAction().create().post();

        binding.imageExpectSpeech.clearAnimation();
        binding.imageExpectSpeech.setVisibility(View.INVISIBLE);
    }

    private void onAudioPlay(boolean isQueue, String url) {
        MPEGPlayer.getInstance().playUrl(url, new MPEGPlayer.Item.OnPlayEvent() {
            @Override
            public void onStart() {
                showLogInfo("playing audio stream...");
            }

            @Override
            public void onCompleted() {
                showLogInfo("stream audio end.");
                // not completed for api
            }
        });
        showLogInfo("wait for playing audio stream..");
    }

    private void onTemplateRender(TemplateCardActionData payload) {
        if (payload.imageUrl != null) {
            String imageUrl = payload.imageUrl;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream is = (InputStream) new URL(imageUrl).getContent();
                        Drawable drawable = Drawable.createFromStream(is, "image");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.imageTemplate.setVisibility(View.VISIBLE);
                                binding.imageTemplate.setImageDrawable(drawable);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            binding.imageTemplate.setVisibility(View.GONE);
        }

        binding.textTemplateTitle.setText(payload.mainTitle);
        binding.textTemplateSubTitle.setText(payload.subTitle);
        binding.textTemplateText.setText(payload.text);

        binding.layoutTemplate.setVisibility(View.VISIBLE);
    }

    private void onLightSpotState(String state) {
        binding.imageLightSpot.setVisibility(state.equals("ON") ? View.VISIBLE : View.INVISIBLE);
    }

}