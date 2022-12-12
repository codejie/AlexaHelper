package com.SmartWatchVoice.bestapp.system;

import com.SmartWatchVoice.bestapp.alexa.api.Context;
import com.SmartWatchVoice.bestapp.sdk.TemplateListActionData;
import com.SmartWatchVoice.bestapp.sdk.TemplateWeatherActionData;
import com.SmartWatchVoice.bestapp.system.setting.AlertInfo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SettingInfo {
    private static SettingInfo instance = null;
    public TemplateListActionData templateListData = null;
    public TemplateWeatherActionData templateWeatherActionData = null;

    public static SettingInfo getInstance() {
        if (instance == null) {
            instance = SettingInfo.load();
        }
        return instance;
    }

    public boolean indicator = false;
    public boolean doNotDisturb = false;
    public AlertInfo alertInfo = new AlertInfo();
    public String timeZone = "Asia/Shanghai";
    public List<String> locales = Arrays.asList("en-US");

    public Integer volume = 100;


    public static SettingInfo load () {
        File file = RuntimeInfo.getInstance().makeSettingFile();
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
            return new Gson().fromJson(content.toString(), SettingInfo.class);
        } else {
            return new SettingInfo();
        }
    }

    public void flush () {
        String content = new Gson().toJson(this);
        try {
            FileOutputStream ofs = new FileOutputStream(RuntimeInfo.getInstance().makeSettingFile());
            ofs.write(content.getBytes());
            ofs.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
