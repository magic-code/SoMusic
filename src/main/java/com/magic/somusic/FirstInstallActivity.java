package com.magic.somusic;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.AppSetting;
import com.magic.somusic.services.ScanMusicService;


public class FirstInstallActivity extends Activity {

    private TextView txScan;
    private TextView txTip;
    private ScanMusicReciver reciver = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        AppSetting setting = MusicDBHelper.getInstance(this).getSetting();
        if (setting!=null){
            Intent intent = new Intent(this,SplashActivity.class);
            startActivity(intent);
            this.finish();
        }else {
            setContentView(R.layout.activity_first_install);
            txScan = (TextView) findViewById(R.id.tx_first_scan_music);
            txTip = (TextView) findViewById(R.id.tx_first_scan_tip);
            txScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!flag) {
                        Intent intent = new Intent(FirstInstallActivity.this, ScanMusicService.class);
                        startService(intent);
                        txTip.setText(R.string.scaning_wait);

                    }else{
                        Intent intent = new Intent(FirstInstallActivity.this,SplashActivity.class);
                        startActivity(intent);
                        FirstInstallActivity.this.finish();
                    }
                }
            });
            reciver = new ScanMusicReciver();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(Config.Broadcast.SCAN_SUCCESS);
            intentFilter.addAction(Config.Broadcast.SCAN_FAIL);
            registerReceiver(reciver,intentFilter);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(reciver!=null)
            unregisterReceiver(reciver);
    }

    private boolean flag = false;
    class ScanMusicReciver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Config.Broadcast.SCAN_SUCCESS)){
                int num = intent.getIntExtra(Config.Broadcast.SCAN_COUNT,0);
                txTip.setText(getString(R.string.totalscan)+num+getString(R.string.nummusic));
                txScan.setText(R.string.enterApp);
                flag = true;
                MusicDBHelper.getInstance(FirstInstallActivity.this).initSetting();
            }else{
                Toast.makeText(FirstInstallActivity.this,R.string.scanerror,Toast.LENGTH_LONG).show();
            }
        }
    }

}
