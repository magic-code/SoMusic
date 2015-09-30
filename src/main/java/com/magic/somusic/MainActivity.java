package com.magic.somusic;

import android.app.Activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;

import android.view.WindowManager;

import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.fragment.MainFragment;


public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //MusicDBHelper.getInstance(this).getWritableDatabase();
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        Fragment fragment = new MainFragment();
        ft.replace(R.id.main_content_fragment,fragment);
        ft.commit();
    }



}
