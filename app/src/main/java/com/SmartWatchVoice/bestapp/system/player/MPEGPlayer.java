package com.SmartWatchVoice.bestapp.system.player;

import android.media.AudioAttributes;
import android.media.MediaPlayer;

import com.SmartWatchVoice.bestapp.system.DeviceInfo;
import com.SmartWatchVoice.bestapp.system.RuntimeInfo;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class MPEGPlayer {

    public static class Item {
        public enum Type {
            FILE,
            URL
        }
        public interface OnPlayEvent {
            void onStart();
            void onCompleted();
        }

        public final Type type;
        public final String url;
        public final OnPlayEvent onPlayEvent;

        Item(Type type, String url) {
            this.type = type;
            this.url = url;
            this.onPlayEvent = null;
        }
        Item(Type type, String url, OnPlayEvent event) {
            this.type = type;
            this.url = url;
            this.onPlayEvent = event;
        }

    }

    private static MPEGPlayer instance = null;
    public static MPEGPlayer getInstance() {
        if (instance == null) {
            instance = new MPEGPlayer();
        }
        return instance;
    }

    private MediaPlayer player = null;
    private List<Item> data = new LinkedList<>();

    private Item currentItem = null;

    public MPEGPlayer () {
        player = new MediaPlayer();
        player.setAudioAttributes(new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build());
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (currentItem != null && currentItem.onPlayEvent != null) {
                    currentItem.onPlayEvent.onCompleted();
                }
                if (data.size() > 0) {
                    play();
                } else {
                    player.stop();
                    currentItem = null;
                }
            }
        });
    }

    private void put(Item item) {
//        files.add(filename);
        data.add(item);
    }

    private final Item pop() {
        if (data.size() == 0)
            return null;
        return data.remove(0);
    }

    public boolean isPlaying() {
        return player.isPlaying();
    }

    public void playFile(String filename) {
        put(new Item(Item.Type.FILE, filename));
        if (!player.isPlaying())
            play();
    }

    public void playUrl(String url) {
        put(new Item(Item.Type.URL, url));
        if (!player.isPlaying())
            play();
    }

    public void playUrl(String url, Item.OnPlayEvent onPlayEvent) {
        put(new Item(Item.Type.URL, url, onPlayEvent));
        if (!player.isPlaying())
            play();
    }

    private void play() {
        currentItem = pop();
        if (currentItem != null) {
            if (currentItem.onPlayEvent != null) {
                currentItem.onPlayEvent.onStart();
            }
            if (currentItem.type == Item.Type.FILE) {
                filePlay(currentItem.url);
            } else if (currentItem.type == Item.Type.URL) {
                urlPlay(currentItem.url);
            }
        }
    }

    private void filePlay(String filename) {
        File file = RuntimeInfo.getInstance().makeSpeechMP3File(filename);
        try {
            player.reset();
            player.setDataSource(file.getAbsolutePath());
            player.prepare();
            player.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void urlPlay(String url) {
        try {
            player.reset();
            player.setDataSource(url);
            player.prepare();
            player.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void stop() {
        data.clear();

        if (player.isPlaying())
            player.stop();
    }

    public void pause() {
        player.pause();
    }
}
