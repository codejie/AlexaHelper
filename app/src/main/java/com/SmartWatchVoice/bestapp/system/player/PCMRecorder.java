package com.SmartWatchVoice.bestapp.system.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class PCMRecorder {
    private static PCMRecorder instance = null;
    public static PCMRecorder getInstance() {
        if (instance == null) {
            instance = new PCMRecorder();
        }
        return instance;
    }

    private static final int SAMPLE_RATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO; // stereo
    private static final int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord record = null;
    private boolean isRecording = false;

    private String fileName = null;

    public void release() {
        if (record != null) {
            isRecording = false;
            record.stop();
            record.release();
            record = null;
        }
    }

    @SuppressLint("MissingPermission")
    public void start() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, RECORDER_CHANNELS, AUDIO_ENCODING);
        record = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE, RECORDER_CHANNELS, AUDIO_ENCODING, bufferSize);

        byte[] buffer = new byte[bufferSize];

        fileName = System.currentTimeMillis() + ".pcm";
        File file = RuntimeInfo.getInstance().makeSpeechPCMFile(fileName);

        isRecording = true;
        record.startRecording();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FileOutputStream ofs = new FileOutputStream(file);
                    while (isRecording) {
                        int read = record.read(buffer, 0, bufferSize);
                        if (read != AudioRecord.ERROR_INVALID_OPERATION) {
                            ofs.write(buffer);
                        }
                    }
                    ofs.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public String stop() {
        release();
        return fileName;
    }
}
