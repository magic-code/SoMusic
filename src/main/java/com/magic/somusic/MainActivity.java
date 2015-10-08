package com.magic.somusic;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.fragment.LocalMusicFragment;
import com.magic.somusic.fragment.MainFragment;
import com.magic.somusic.services.PlayMusicService;
import com.magic.somusic.services.ScanMusicService;
import com.magic.somusic.ui.RoundProgressBar;

import java.util.ArrayList;


public class MainActivity extends FragmentActivity {
    private MusicServiceConnection conn = new MusicServiceConnection();
    public PlayMusicService musicService;
    private TextView txTitle;
    private TextView txArtist;
    private ImageView mPlayPause;
    private RoundProgressBar roundProgressBar;
    private MusicChangeReciver musicChangeReciver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intentService = new Intent(this,PlayMusicService.class);
        startService(intentService);    //为了不让bindService在activity不可见时就结束，用startService先启动，保证生命周期再调用stopService后才结束
        this.bindService(intentService,conn, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);
        //MusicDBHelper.getInstance(this).getWritableDatabase();
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        toMainFragment();

        Intent intent = new Intent();
        intent.setClass(this,ScanMusicService.class);
        startService(intent);
        txTitle = (TextView) findViewById(R.id.tx_main_title);
        txArtist = (TextView) findViewById(R.id.tx_main_artist);
        roundProgressBar = (RoundProgressBar) findViewById(R.id.main_rpb);
        //roundProgressBar.setProgress(50);
        initRoundProgress();
        IntentFilter filter = new IntentFilter(Config.Broadcast.MUSIC_CHANGE);
        registerReceiver(new MusicChangeReciver(),filter);
        mPlayPause = (ImageView) findViewById(R.id.iv_main_play);
        mPlayPause.setOnClickListener(new PlayAndPauseClickListener());


    }

    class PlayAndPauseClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (musicService.getPlaying()==Config.PlayState.STATE_PLAYING){
                mPlayPause.setImageResource(R.mipmap.img_appwidget_pause);
                MainActivity.this.sendBroadcast(new Intent(Config.Broadcast.MUSIC_PAUSE));
                //musicService.pause();
            }else if (musicService.getPlaying()==Config.PlayState.STATE_PAUSE){
                mPlayPause.setImageResource(R.mipmap.img_appwidget_play);
                MainActivity.this.sendBroadcast(new Intent(Config.Broadcast.MUSIC_PLAY));
                //musicService.rePlay();
            }else if (musicService.getPlaying()==Config.PlayState.STATE_STOP){
                mPlayPause.setImageResource(R.mipmap.img_appwidget_play);
                MainActivity.this.sendBroadcast(new Intent(Config.Broadcast.MUSIC_PLAY));
                //musicService.play();
            }

        }
    }
    public void initRoundProgress(){
        roundProgressBar.post(mProgressThread);
    }
    class ProgressThread implements Runnable{

        @Override
        public void run() {
            int max = musicService.getDuration();
            int pro = musicService.getCurrentDuration();
            Message msg = mProgressBar.obtainMessage(0,max,pro);
            mProgressBar.sendMessage(msg);
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }




    private RoundProgressHandler mProgressBar = new RoundProgressHandler();
    private ProgressThread mProgressThread = new ProgressThread();
    class RoundProgressHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            roundProgressBar.setMax(msg.arg1);
            roundProgressBar.setProgress(msg.arg2);
            mProgressBar.post(mProgressThread);
            if (musicService!=null){
                MusicItem item = musicService.getMusic(-1);
                if (item!=null) {
                    txTitle.setText(item.getTitle());
                    txArtist.setText(item.getArtist());
                }
                if (musicService.getPlaying()==Config.PlayState.STATE_PLAYING)
                    mPlayPause.setImageResource(R.mipmap.img_appwidget_pause);
                else{
                    mPlayPause.setImageResource(R.mipmap.img_appwidget_play);
                }
            }
        }
    }
    public void toMainFragment(){
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new MainFragment();
        ft.add(fragment,"main_fragment");
        ft.replace(R.id.main_content_fragment,fragment);
        ft.commit();
    }

    public void toLocalMusicView(){
        LocalMusicFragment fragment = new LocalMusicFragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.main_content_fragment, fragment);
        ft.addToBackStack(null);
        ft.commit();
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
    public void play(ArrayList<MusicItem> list,int pos){
         musicService.play(list,pos);
         //MusicItem item = list.get(pos);
         //txTitle.setText(item.getTitle());
         //txArtist.setText(item.getArtist());
    }
    class MusicChangeReciver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
             MusicItem item = musicService.getMusic(-1);
            txTitle.setText(item.getTitle());
            txArtist.setText(item.getArtist());
            if (musicService.getPlaying()==Config.PlayState.STATE_PLAYING){
                mPlayPause.setImageResource(R.mipmap.img_appwidget_pause);
            }else{
                mPlayPause.setImageResource(R.mipmap.img_appwidget_play);
            }
            //showNotification(item,musicService.getPlaying());
        }
    }

}
