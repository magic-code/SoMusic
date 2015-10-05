package com.magic.somusic.services;

import android.app.Notification;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.TelephonyManager;

import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.utils.Player;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/5.
 */
public class PlayMusicService extends Service {
    private Player mPlayer = Player.getPlayer();
    private StateReciver stateReciver;
    private LocalService localService = new LocalService();
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
        return localService;
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



    @Override
    public void onDestroy() {
        super.onDestroy();
        mPlayer.destroy();
        //stopForeground(true);
        unregisterReceiver(stateReciver);
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
}
