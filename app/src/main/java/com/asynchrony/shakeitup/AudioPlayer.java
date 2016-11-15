package com.asynchrony.shakeitup;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.RawRes;

public class AudioPlayer {
    private MediaPlayer mediaPlayer;

    public void create(Context context, @RawRes int resourceId){
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(context, resourceId);
        }
    }

    public void play(){
        if(!mediaPlayer.isPlaying()){
            mediaPlayer.start();
        }

    }

    public void pause(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.pause();
        }
    }

    public void destroy(){
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;

        }
    }
}
