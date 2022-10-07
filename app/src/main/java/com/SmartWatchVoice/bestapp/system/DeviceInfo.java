package com.SmartWatchVoice.bestapp.system;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;

public class DeviceInfo {
    public static final String ClientId = "amzn1.application-oa2-client.a0c1ba72a7d4443bb4436cdc9cf8fa51";
    public static final String ProductId = "VWRK4_Module";
    public static String ProductSerialNumber = null;//"Module_1234567890";
//    public static String VerifierCode = "59117b92-e134-41f0-a720-ccd5be69db23";
    public static List<String> SupportedLocales = Arrays.asList("de-DE", "en-AU", "en-CA", "en-GB", "en-IN", "en-US", "es-ES", "es-MX", "es-US", "fr-CA", "fr-FR", "hi-IN", "it-IT", "ja-JP", "pt-BR", "ar-SA");
    public static List<List<String>> SupportedLocaleCombinations = Arrays.asList(Arrays.asList("en-US", "es-US")
            , Arrays.asList("es-US", "en-US")
            , Arrays.asList("en-US", "fr-FR")
            , Arrays.asList("fr-FR", "en-US")
            , Arrays.asList("en-US", "de-DE")
            , Arrays.asList("de-DE", "en-US")
            , Arrays.asList("en-US", "ja-JP")
            , Arrays.asList("ja-JP", "en-US")
            , Arrays.asList("en-US", "it-IT")
            , Arrays.asList("it-IT", "en-US")
            , Arrays.asList("en-US", "es-ES")
            , Arrays.asList("es-ES", "en-US")
            , Arrays.asList("en-IN", "hi-IN")
            , Arrays.asList("hi-IN", "en-IN")
            , Arrays.asList("fr-CA", "en-CA")
            , Arrays.asList("en-CA", "fr-CA"));

    public static List<String> SupportedTimeZone = Arrays.asList("Asia/Shanghai", "Asia/Urumqi", "Asia/Hong_Kong",
            "Asia/Taipei", "Asia/Macau", "Asia/Singapore", "Asia/Colombo", "Asia/Dubai",
            "Europe/Sofia", "Europe/Paris", "Europe/Berlin", "Europe/Rome", "Europe/London",
            "America/Havana");

    public static boolean init(Context context) {
        File file = new File(context.getCodeCacheDir(), "device.no");
        if (!file.exists()) {
            return false;
        }
        try {
            FileInputStream ifs = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(ifs));
            DeviceInfo.ProductSerialNumber = reader.readLine();
            reader.close();
            ifs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (DeviceInfo.ProductSerialNumber == null) {
            return false;
        }
        return true;
    }

    public static void flush(Context context, String serial) {
        DeviceInfo.ProductSerialNumber = "VWRK4_" + serial;

        try {
            File file = new File(context.getCodeCacheDir(), "device.no");
            FileOutputStream ofs = new FileOutputStream(file);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(ofs));
            writer.write(DeviceInfo.ProductSerialNumber);
            writer.close();
            ofs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
