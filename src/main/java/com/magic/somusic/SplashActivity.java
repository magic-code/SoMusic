package com.magic.somusic;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.AppSetting;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.services.PlayMusicService;

import java.util.ArrayList;


public class SplashActivity extends Activity {

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            musicService.initServ();
            musicService.showNotification(musicService,musicService.getMusic(musicService.getCurrentPos()), musicService.getPlaying(),musicService.isShowAlertLrc);
            SplashActivity.this.sendBroadcast(new Intent(Config.Broadcast.MUSIC_CHANGE));
            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
            unbindService(conn);
            startActivity(intent);
            SplashActivity.this.finish();
        }
    };
    private PlayMusicService musicService;
    private  MusicServiceConnection conn = new MusicServiceConnection();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentService = new Intent(this,PlayMusicService.class);
        //startService(intentService);    //为了不让bindService在activity不可见时就结束，用startService先启动，保证生命周期再调用stopService后才结束
        this.bindService(intentService,conn, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.activity_splash);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message mes = handler.obtainMessage(0);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                handler.sendMessage(mes);
            }
        }).start();
    }
    class MusicServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicService = ((PlayMusicService.LocalService)service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicService = null;
        }
    }


}
