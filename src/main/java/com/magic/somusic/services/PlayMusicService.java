package com.magic.somusic.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.magic.somusic.Config;
import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.AppSetting;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.utils.Player;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/5.
 */
public class PlayMusicService extends Service {

    private Player mPlayer = Player.getPlayer(this);
    private StateReciver stateReciver;
    private LocalService localService = new LocalService();
    private MusicControlReciver musicControlReciver;
    public class LocalService extends Binder{
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        //startForeground(1,new Notification());
    }

    @Override
    public IBinder onBind(Intent intent) {
        IntentFilter intentFilter = new IntentFilter();
        stateReciver = new StateReciver();
        intentFilter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        intentFilter.addAction("android.intent.action.PHONE_STATE");
        registerReceiver(stateReciver,intentFilter);
        musicControlReciver = new MusicControlReciver();
        IntentFilter controlFilter = new IntentFilter();
        controlFilter.addAction(Config.Broadcast.MUSIC_CHANGE);
        controlFilter.addAction(Config.Broadcast.MUSIC_NEXT);
        controlFilter.addAction(Config.Broadcast.MUSIC_PLAY);
        controlFilter.addAction(Config.Broadcast.MUSIC_PAUSE);
        controlFilter.addAction(Config.Broadcast.MUSIC_PREVIOUS);
        registerReceiver(musicControlReciver, controlFilter);
        showNotification(null,-1);
        return localService;
    }
    public void initServ(){
        if (!mPlayer.isFlag()) {
            AppSetting setting = MusicDBHelper.getInstance(this).getSetting();
            ArrayList<MusicItem> list = null;
            switch (setting.getLast_list()) {
                case Config.PlayList.LIST_LOCAL:
                    list = MusicDBHelper.getInstance(this).query();
                    break;
                case Config.PlayList.LIST_COLLECTION:

                    break;
            }
            this.setList(list);
            this.setPostion(setting.getLast_music_pos());
            this.setModel(setting.getLast_model());
        }
        mPlayer.setFlag(true);
    }

    public void pause(){
        mPlayer.pause();
    }

    public void rePlay(){
        mPlayer.replay();
    }
    public MusicItem next(){
        return mPlayer.next();
    }
    public MusicItem previous(){
        return mPlayer.previous();
    }
    public void play(){mPlayer.play();}
    public void play(ArrayList<MusicItem> list,int position){
        mPlayer.play(this,list,position);
    }
    public void seekTo(int msec){
        mPlayer.seekTo(msec);
    }
    public int getCurrentDuration(){
        return mPlayer.getCurrentDuration();
    }

    public int getDuration(){
        return mPlayer.getDuration();
    }
    public void setList(ArrayList<MusicItem> list){
        mPlayer.setList(list);
    }
    public void setPostion(int position){
        mPlayer.setPosition(position);
    }
    public void setModel(int model){
        mPlayer.setModel(model);
    }
    /**@param  sec -表示取当前音乐*/
    public MusicItem getMusic(int sec){
        return mPlayer.getMusic(sec);
    }
    public int getPlaying(){
        return mPlayer.getPlaying();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
        //stopForeground(true);
        unregisterReceiver(stateReciver);
        unregisterReceiver(musicControlReciver);
    }

    RemoteViews remoteViews;
    public void showNotification(MusicItem item,int playing){
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        remoteViews = new RemoteViews(getPackageName(), R.layout.notification_custom);
        if (item!=null) {
            if (playing == Config.PlayState.STATE_PLAYING) {
                remoteViews.setImageViewResource(R.id.iv_notification_play, R.mipmap.img_appwidget_pause);
            } else {
                remoteViews.setImageViewResource(R.id.iv_notification_play, R.mipmap.img_appwidget_play);
            }
            remoteViews.setTextViewText(R.id.tx_notification_title,item.getTitle());
            remoteViews.setTextViewText(R.id.tx_notification_artist,item.getArtist());
        }
        Intent nextintent = new Intent();
        nextintent.setAction(Config.Broadcast.MUSIC_NEXT);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_NEXT,nextintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playintent = new Intent();
        playintent.setAction(Config.Broadcast.MUSIC_PLAY);
        PendingIntent playPIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_PLAY,playintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseintent = new Intent();
        pauseintent.setAction(Config.Broadcast.MUSIC_PAUSE);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_PAUSE,pauseintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitintent = new Intent();
        exitintent.setAction(Config.Broadcast.APP_EXIT);
        PendingIntent exitPIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_EXIT,exitintent,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next,nextPIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_play,playPIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_exit,exitPIntent);
        builder.setSmallIcon(R.mipmap.img_notification_logo);
        builder.setContent(remoteViews);
        Intent aintent = new Intent(this,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,Config.Code.REQ_TO_ACTIVITY,aintent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        manager.notify(1,notification);
    }

    class StateReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            //
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){

            }else{
                TelephonyManager telephonyManager =
                        (TelephonyManager) context.getSystemService(Service.TELEPHONY_SERVICE);
                switch (telephonyManager.getCallState()){
                    case TelephonyManager.CALL_STATE_RINGING:
                        pause();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        rePlay();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        pause();
                        break;
                }
            }
        }
    }
    class MusicControlReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Config.Broadcast.MUSIC_NEXT)){
                PlayMusicService.this.next();
            }else if (action.equals(Config.Broadcast.MUSIC_PLAY)){
                if (PlayMusicService.this.getPlaying()==Config.PlayState.STATE_PLAYING){
                    PlayMusicService.this.pause();
                }else if (PlayMusicService.this.getPlaying()==Config.PlayState.STATE_PAUSE){
                    PlayMusicService.this.rePlay();
                }else{
                    PlayMusicService.this.play();
                }
            }else if (action.equals(Config.Broadcast.MUSIC_PAUSE)){
                PlayMusicService.this.pause();
            }else if (action.equals(Config.Broadcast.MUSIC_PREVIOUS)){
                PlayMusicService.this.previous();
            }
            showNotification(PlayMusicService.this.getMusic(-1), PlayMusicService.this.getPlaying());
        }
    }
}
