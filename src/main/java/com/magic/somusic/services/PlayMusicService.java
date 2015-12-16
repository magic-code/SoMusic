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
import android.graphics.PixelFormat;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RemoteViews;

import com.magic.somusic.Config;
import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.AppSetting;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.ui.TwoLinesLrcView;
import com.magic.somusic.utils.LrcDialogUtil;
import com.magic.somusic.utils.LrcLoader;
import com.magic.somusic.utils.Player;

import java.util.ArrayList;
import java.util.zip.Inflater;

/**
 * Created by Administrator on 2015/10/5.
 */
public class PlayMusicService extends Service {

    private Player mPlayer = Player.getPlayer(this);
    private StateReciver stateReciver=null;
    private LocalService localService = new LocalService();
    private MusicControlReciver musicControlReciver=null;

    public boolean isShowAlertLrc = false;
    public LrcDialogUtil lrcDialogUtil = new LrcDialogUtil();
    private int listName=Config.PlayList.LIST_LOCAL;

    public ArrayList<MusicItem> getList(){
        return mPlayer.getList();
    }

    public void updateLrc(int musicId, String lrcPath) {
        mPlayer.updateLrc(musicId,lrcPath);
    }

    public void updateCollection(int id, boolean b) {
        mPlayer.updateCollection(id,b);
        MusicDBHelper.getInstance(this).collection(id,b);
    }

    public class LocalService extends Binder{
        public PlayMusicService getService(){
            return PlayMusicService.this;
        }
    }

    @Override
    public void onCreate() {

        super.onCreate();
        //startForeground(1,new Notification());
        lrcDialogUtil.setOnPostionListener(new LrcDialogUtil.OnPostionUpdate() {
            @Override
            public int onPostionUpdate() {
                return getCurrentDuration();
            }
        });
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
        controlFilter.addAction(Config.Broadcast.APP_EXIT);
        controlFilter.addAction(Config.Broadcast.MUSIC_CHANGE);
        controlFilter.addAction(Config.Broadcast.MUSIC_NEXT);
        controlFilter.addAction(Config.Broadcast.MUSIC_PLAY);
        controlFilter.addAction(Config.Broadcast.MUSIC_PAUSE);
        controlFilter.addAction(Config.Broadcast.MUSIC_PREVIOUS);
        controlFilter.addAction(Config.Broadcast.LRC_ALERT_TOGGLE);
        registerReceiver(musicControlReciver, controlFilter);
        //showNotification(null,-1);
        startForeground(1,getNotification(this,null,Config.PlayState.STATE_STOP,isShowAlertLrc));
        return localService;
    }
    public void initServ(){
        if (!mPlayer.isFlag()) {
            AppSetting setting = MusicDBHelper.getInstance(this).getSetting();
            ArrayList<MusicItem> list = null;
            listName = setting.getLast_list();
            switch (listName) {
                case Config.PlayList.LIST_LOCAL:
                    list = MusicDBHelper.getInstance(this).query();
                    break;
                case Config.PlayList.LIST_COLLECTION:

                    break;
            }
            this.setList(list);
            int id = setting.getId();
            for (int i=0;i<list.size();i++){
                MusicItem item = list.get(i);
                if (item.get_id()==id) {
                    this.setPostion(i);
                    break;
                }
            }
            this.setModel(setting.getLast_model());
            this.seekTo(setting.getLast_music_sec());
            sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
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
    public int getCurrentPos(){return mPlayer.getPos();}
    /**@param  sec -5表示取当前音乐*/
    public MusicItem getMusic(Integer sec){
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
        if (stateReciver!=null)
            unregisterReceiver(stateReciver);
        if (musicControlReciver!=null)
            unregisterReceiver(musicControlReciver);
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
        manager.cancel(1);
        stopForeground(true);
        MusicDBHelper.getInstance(PlayMusicService.this).updateMusicRecord(getMusic(getCurrentDuration()).get_id(), listName, getCurrentDuration(), getMode());
    }

    public int getListName(){
        return listName;
    }

    public int getMode(){
        return mPlayer.getModel();
    }
    RemoteViews remoteViews;
    public void showNotification(Context context,MusicItem item,int playing,boolean showLrcAlert){
        NotificationManager manager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);

        manager.notify(1,getNotification(context,item,playing,showLrcAlert));
    }
    private Notification getNotification(Context context,MusicItem item,int playing,boolean showLrcAlert){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
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
        if (showLrcAlert){
            remoteViews.setImageViewResource(R.id.iv_notification_lrc,R.mipmap.img_appwidget_minilyric_on);
        }else{
            remoteViews.setImageViewResource(R.id.iv_notification_lrc,R.mipmap.img_appwidget_minilyric_off);
        }
        Intent nextintent = new Intent();
        nextintent.setAction(Config.Broadcast.MUSIC_NEXT);
        PendingIntent nextPIntent = PendingIntent.getBroadcast(context,Config.Code.REQ_MUSIC_NEXT,nextintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent playintent = new Intent();
        playintent.setAction(Config.Broadcast.MUSIC_PLAY);
        PendingIntent playPIntent = PendingIntent.getBroadcast(context,Config.Code.REQ_MUSIC_PLAY,playintent,PendingIntent.FLAG_UPDATE_CURRENT);
        Intent pauseintent = new Intent();
        pauseintent.setAction(Config.Broadcast.MUSIC_PAUSE);
        PendingIntent pausePIntent = PendingIntent.getBroadcast(context,Config.Code.REQ_MUSIC_PAUSE,pauseintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent exitintent = new Intent();
        exitintent.setAction(Config.Broadcast.APP_EXIT);
        PendingIntent exitPIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_EXIT,exitintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent lrctoggleIntent = new Intent();
        lrctoggleIntent.setAction(Config.Broadcast.LRC_ALERT_TOGGLE);
        PendingIntent lrcPIntent = PendingIntent.getBroadcast(this,Config.Code.REQ_MUSIC_LRC,lrctoggleIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        remoteViews.setOnClickPendingIntent(R.id.iv_notification_next,nextPIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_play,playPIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_exit,exitPIntent);
        remoteViews.setOnClickPendingIntent(R.id.iv_notification_lrc,lrcPIntent);

        builder.setSmallIcon(R.mipmap.img_notification_logo);
        builder.setContent(remoteViews);
        Intent aintent = new Intent(context,MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,Config.Code.REQ_TO_ACTIVITY,aintent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_NO_CLEAR;
        return notification;
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

            }else if(action.equals(Config.Broadcast.APP_EXIT)){
                PlayMusicService.this.onDestroy();
                return;
            }else if(action.equals(Config.Broadcast.LRC_ALERT_TOGGLE)){
                if (isShowAlertLrc){
                    isShowAlertLrc = false;
                    //lrcDialogUtil.setLrcList(getMusic(getCurrentPos()).getLrcpath());
                    lrcDialogUtil.hideLrcDialog(PlayMusicService.this);
                }else{
                    isShowAlertLrc = true;
                    lrcDialogUtil.showLrcDialog(PlayMusicService.this);
                }
            }
            if(isShowAlertLrc)
                lrcDialogUtil.setLrcList(getMusic(getCurrentPos()).getLrcpath());
            showNotification(PlayMusicService.this,PlayMusicService.this.getMusic(getCurrentPos()), PlayMusicService.this.getPlaying(),isShowAlertLrc);
            MusicDBHelper.getInstance(PlayMusicService.this).updateMusicRecord(getMusic(getCurrentDuration()).get_id(),listName,getCurrentDuration(),getMode());
        }
    }

}
