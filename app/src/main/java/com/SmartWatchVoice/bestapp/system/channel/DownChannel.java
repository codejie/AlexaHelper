package com.SmartWatchVoice.bestapp.system.channel;

import android.os.Message;

import com.SmartWatchVoice.bestapp.handler.HandlerConst;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;
import com.SmartWatchVoice.bestapp.utils.Logger;
import com.SmartWatchVoice.bestapp.utils.Utils;

import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import okhttp3.Response;
import okhttp3.internal.Util;
import okio.BufferedSource;

public class DownChannel {
    private static DownChannel instance = null;
    public static DownChannel getInstance() {
        if (instance == null) {
            instance = new DownChannel();
        }
        return instance;
    }

    public void create(Response response) {
        BufferedSource source = response.body().source();

        final DownChannelDirectiveParser directiveParser = new DownChannelDirectiveParser();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!source.exhausted()) {
                        String content = source.getBuffer().readString(source.getBuffer().size(), StandardCharsets.UTF_8);
                        Logger.v("DownChannel - " +content);
                        List<DirectiveParser.Part> parts = directiveParser.parseParts(content);
                        if (parts != null && parts.size() > 0) {
//                            Message.obtain(RuntimeInfo.getInstance().directiveHandler, HandlerConst.MSG_CHANNEL_DIRECTIVE_PARTS, parts).sendToTarget();
                            Utils.sendToHandlerMessage(RuntimeInfo.getInstance().directiveHandler, HandlerConst.MSG_CHANNEL_DIRECTIVE_PARTS, parts);
                        }
                    }
                    response.close();

                } catch (EOFException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    response.close();
//                    Message.obtain(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CLOSED).sendToTarget();
                    Utils.sendToHandlerMessage(RuntimeInfo.getInstance().mainHandler, HandlerConst.MSG_CHANNEL_CLOSED);
                    Logger.e("DownChannel be shutdown.");
                }
            }
        }).start();
    }
}
