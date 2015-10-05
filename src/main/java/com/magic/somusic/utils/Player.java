package com.magic.somusic.utils;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import com.magic.somusic.Config;
import com.magic.somusic.domain.MusicItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Administrator on 2015/10/5.
 */
public class Player {
    private static Player mPlayer = new Player();
    private MediaPlayer mediaPlayer;
    private Context context;
    private int model;
    private int playing = Config.PlayState.STATE_STOP;
    private ArrayList<MusicItem> list;
    private int position;
    public static Player getPlayer(){
        return mPlayer;
    }
    public MusicItem play(final Context context, final ArrayList<MusicItem> list, final int position){
        if (playing==Config.PlayState.STATE_PLAYING){
            mediaPlayer.reset();
        }
        mediaPlayer = MediaPlayer.create(context, Uri.parse("file://"+list.get(position).getFilename()));
        try {
            //mediaPlayer.prepare();
            mediaPlayer.start();
            this.context = context;
            this.list = list;
            this.position = position;
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Player.this.context.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
                    completion(context,list,position);
                }
            });
            playing = Config.PlayState.STATE_PLAYING;
            Player.this.context.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
            return list.get(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return null;
    }
    public MusicItem completion(Context context,ArrayList<MusicItem> list,int position){
        MusicItem musicItem = null;
        switch (model){
            /**单曲模式*/
            case Config.PlayModel.SINGLE_MODEL:
                stop();
                break;
            /**单曲循环模式*/
            case Config.PlayModel.SINGLE_REPEAT:
                musicItem = play(context,list,position);
                break;
            /**列表循环模式*/
            case Config.PlayModel.LIST_REPEAT_MODEL:
                musicItem = play(context,list,(position+1)%list.size());
                break;
            /**顺序播放模式*/
            case Config.PlayModel.SEQUENCE_MODEL:
                if (position==list.size()-1)
                    stop();
                else musicItem = play(context,list,position+1);
                break;
            /**随机播放模式*/
            case Config.PlayModel.RANDOM_MODEL:
                musicItem = play(context,list,(int)(Math.random()*list.size()));
                break;
        }
        return musicItem;
    }
    public void stop(){
        if (playing == Config.PlayState.STATE_PLAYING){
            mediaPlayer.reset();
        }
        playing = Config.PlayState.STATE_STOP;
        Player.this.context.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
    }

    /**播放前一曲*/
    public MusicItem previous(){
        MusicItem musicItem = null;
        if (list.size()<1){
            this.destroy();
        }else{
            position = (position+list.size()-1)%list.size();
            musicItem = list.get(position);
            mediaPlayer.reset();    //先停止上一首
            play(context,list,position);
        }
        return musicItem;
    }


    /**播放下一曲*/
    public MusicItem next(){
        MusicItem musicItem = null;
        if (list.size()<1){
            this.destroy();
        }else{
            position = (position+list.size()+1)%list.size();
            musicItem = list.get(position);
            mediaPlayer.reset();    //先停止上一首
            play(context,list,position);
        }
        return musicItem;
    }
    /**恢复播放*/
    public MusicItem replay(){
        if(playing!=Config.PlayState.STATE_PLAYING){
            mediaPlayer.start();
            playing = Config.PlayState.STATE_PLAYING;
            context.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
        }
        return list.get(position);
    }
    /**暂停播放*/
    public void pause(){
        if (playing!=Config.PlayState.STATE_PAUSE){
            mediaPlayer.pause();
            playing = Config.PlayState.STATE_PAUSE;
            context.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
        }
    }
    /***/
    public void destroy() {
        if (mediaPlayer!=null){
            mediaPlayer.release();
            playing = Config.PlayState.STATE_STOP;
        }
    }

    /**跳到某个位置播放，毫秒为单位*/
    public void seekTo(int msec){
        if (mediaPlayer!=null){
            mediaPlayer.seekTo(msec);
        }
    }
    /**设置播放模式*/
    public void setModel(int model){
        this.model = model;
    }
    /**取得播放模式*/
    public int getModel(){
        return model;
    }
    /**获取音乐列表*/
    public ArrayList<MusicItem> getList(){
        return list;
    }
    /**取得列表中某一首音乐的信息
     * @param section 为-1表示取当前位置的音乐
     * */
    public MusicItem getMusic(int section){
        if (section==-1){
            return list.get(position);
        }else{
            return list.get(section);
        }
    }
    /**获取当前播放音乐的位置时间*/
    public int getCurrentDuration(){
        if (mediaPlayer!=null){
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }
    /**获取当前音乐的总时间*/
    public int getDuration(){
        if (mediaPlayer!=null){
            return mediaPlayer.getDuration();
        }
        return 0;
    }
 }
