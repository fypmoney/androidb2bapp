package com.fypmoney.util.videoplayer;

import android.content.Context;

import com.google.android.exoplayer2.ExoPlayer;

public class Video {

    private String url;
    private Context context;
    Boolean playWhenReady = false;
    int currentWindow = 0;
    long playbackPosition = 0;
    ExoPlayer player;


    public Video(String url, Context context) {
        this.url = url;
        this.context = context;
    }


    public void setCurrentWindow(int currentWindow) {
        this.currentWindow = currentWindow;
    }

    public void setPlaybackPosition(long playbackPosition) {
        this.playbackPosition = playbackPosition;
    }

    public void setPlayWhenReady(Boolean playWhenReady) {
        this.playWhenReady = playWhenReady;
    }

    public void setPlayer(ExoPlayer player) {
        this.player = player;
    }

    public ExoPlayer getPlayer() {
        return player;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;

    }

    public Context getContext() {
        return context;
    }

    public Boolean getPlayWhenReady() {
        return playWhenReady;
    }

    public int getCurrentWindow() {
        return currentWindow;
    }

    public long getPlaybackPosition() {
        return playbackPosition;
    }
}
