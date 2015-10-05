package com.magic.somusic;

import android.app.Activity;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magic.somusic.db.MusicDBHelper;
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
    private RoundProgressBar roundProgressBar;
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
        roundProgressBar.setProgress(50);

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
         MusicItem item = list.get(pos);
         txTitle.setText(item.getTitle());
         txArtist.setText(item.getArtist());
    }
}
