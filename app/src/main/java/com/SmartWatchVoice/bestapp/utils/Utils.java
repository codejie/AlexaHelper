package com.SmartWatchVoice.bestapp.utils;

import android.os.Handler;
import android.os.Message;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class Utils {

    public static void sendToHandlerMessage(Handler handler, int what) {
        Message message = new Message();
        message.what = what;
        sendToHandlerMessage(handler, message);
    }

    public static void sendToHandlerMessage(Handler handler, int what, Object obj) {
        Message message = new Message();
        message.what = what;
        message.obj = obj;
        sendToHandlerMessage(handler, message);
    }

    public static void sendToHandlerMessage(Handler handler, Message message) {
        if (handler != null) {
            handler.sendMessage(message);
        } else {
            Logger.d("Handler is invalid now.");
        }
    }

    public static final Date stringToDate(final String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        try {
            return format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static final String dateToLocalDateTime(final Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }

    public static final void deleteAllFiles(final File file) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (final File f : files) {
                deleteAllFiles(f);
            }
        } else if (file.isFile()) {
            file.delete();
        }
    }

    public static final String makeToken() {
        return UUID.randomUUID().toString();
    }
    public static final String makeDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        return sdf.format(date);
    }
}
