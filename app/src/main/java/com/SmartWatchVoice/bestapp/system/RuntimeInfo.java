package com.SmartWatchVoice.bestapp.system;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;

import com.SmartWatchVoice.bestapp.utils.Utils;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class RuntimeInfo {
    public static class AuthorizationInfo {
        public String code;
        public String redirectUri;
        public String refreshToken;
    }

    public static RuntimeInfo instance = null;
    public static RuntimeInfo getInstance() {
        if (instance == null) {
            instance = new RuntimeInfo();
        }
        return instance;
    }
    
    public Context context = null;
    public AuthorizationInfo authInfo = null;

    public Handler mainHandler = null;
    public Handler directiveHandler = null;

    public Handler loginFragmentHandler = null;
    public Handler speechFragmentHandler = null;
    public Handler homeFragmentHandler = null;

    public String pcmFilename = null;
    public String[] mp3Filenames = null;

    public Date lastActiveDate = new Date();
    
    public void setPcmFilename(String pcmFilename) {
        this.pcmFilename = pcmFilename;
    }

    public File makeSpeechPCMFile(String filename) {
        return new File(context.getCacheDir(), "/audio/" + filename);
    }

    public File makeSpeechMP3File(String filename) {
        return new File(context.getCacheDir(), "/audio/" + filename);
    }

    public File makeSettingFile() {
        // return new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "setting.json");
        return new File(context.getCodeCacheDir(), "setting.json");
    }

    public File makeAuthorizationFile() {
        return new File(context.getCodeCacheDir(), "auth_info.json");
    }

    public void start(Context context) {
        this.context = context;

        File file = new File(context.getCacheDir(), "audio");
        if (file.exists()) {
//            file.delete();
            Utils.deleteAllFiles(file);
        }
        file.mkdir();

        loadAuthInfo();
    }

    public void stop() {
        flushAuthInfo();
    }

    public void updateAuthInfo(String code, String url) {
        if (authInfo == null) {
            authInfo = new AuthorizationInfo();
        }
        authInfo.code = code;
        authInfo.redirectUri = url;

        flushAuthInfo();
    }

    public void updateAuthInfo(String token) {
        if (authInfo == null) {
            authInfo = new AuthorizationInfo();
        }
        authInfo.refreshToken = token;
        flushAuthInfo();
    }

    public void resetAuthInfo() {
        authInfo = null;
        File file = new File(context.getCodeCacheDir(), "auth_info.json");
        if (file.exists()) {
            file.delete();
        }
    }

    private void loadAuthInfo() {
        File file = new File(context.getCodeCacheDir(), "auth_info.json");
        if (file.exists()) {
            StringBuilder content = new StringBuilder();

            try {
                FileInputStream ifs = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(ifs));
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line);
                }
                reader.close();
                ifs.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.authInfo = new Gson().fromJson(content.toString(), AuthorizationInfo.class);
        } else {
            this.authInfo = null;
        }
    }

    private void flushAuthInfo() {
        if (authInfo != null) {
            String content = new Gson().toJson(authInfo);
            try {
                FileOutputStream ofs = new FileOutputStream(new File(context.getCodeCacheDir(), "auth_info.json"));
                ofs.write(content.getBytes());
                ofs.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
