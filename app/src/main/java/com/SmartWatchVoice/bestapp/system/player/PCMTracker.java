package com.SmartWatchVoice.bestapp.system.player;

import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;

import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.utils.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class PCMTracker {
    private static PCMTracker instance = null;
    public static PCMTracker getInstance() {
        if (instance == null) {
            instance = new PCMTracker();
        }
        return instance;
    }

    private static final int SAMPLE_RATE = 16000;
    private static final int TRACK_CHANNELS = AudioFormat.CHANNEL_OUT_MONO; // stereo
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioTrack track = null;

    public void play(String fileName) {
        release();

        int bufferSize = AudioTrack.getMinBufferSize(SAMPLE_RATE, TRACK_CHANNELS, AUDIO_ENCODING);

        AudioAttributes attributes = new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA).build();
        AudioFormat format = new AudioFormat.Builder()
                .setEncoding(AUDIO_ENCODING)
                .setSampleRate(SAMPLE_RATE)
                .setChannelMask(TRACK_CHANNELS).build();
        AudioTrack track = new AudioTrack.Builder()
                .setAudioAttributes(attributes)
                .setAudioFormat(format)
                .setBufferSizeInBytes(bufferSize)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build();

        File file = RuntimeInfo.getInstance().makeSpeechPCMFile(fileName);

        try {
            Logger.d("Track start..");
            track.play();
            FileInputStream ifs = new FileInputStream(file);

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        byte[] buffer = new byte[bufferSize];
                        while (ifs.available() > 0) {
                            int read = ifs.read(buffer);
//                            if (read == AudioTrack.ERROR_INVALID_OPERATION ||
//                                    read == AudioTrack.ERROR_BAD_VALUE) {
//                                continue;
//                            }
                            if (read > 0) {
                                track.write(buffer, 0, read);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            release();
        }
    }

    private void release() {
        if (track != null) {
            track.stop();
            track.release();
            track = null;
        }
    }
}
