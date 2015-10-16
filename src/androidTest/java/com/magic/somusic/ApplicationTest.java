package com.magic.somusic;

import android.app.Application;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.domain.NetMusicInfo;
import com.magic.somusic.services.ScanMusicService;
import com.magic.somusic.utils.DownloadInfo;

import java.util.ArrayList;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }
    public void testScan(){
        Intent intent = new Intent();
        intent.setClass(getContext(),ScanMusicService.class);
        getContext().startService(intent);
    }
    public void testSave(){
        MusicItem item = new MusicItem();
        item.set_id(1);
        item.setTitle("My Love");
        item.setArtist("west life");
        MusicDBHelper.getInstance(getContext()).save(item);
    }
    public void testQuery(){
        ArrayList<MusicItem> list = MusicDBHelper.getInstance(getContext()).query();
        Log.e("scan",list.size()+"");
    }
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Config.DOWNLOAD_INFO_SUS:
                    NetMusicInfo info = (NetMusicInfo) msg.obj;
                    System.out.println(info.getLrcId()+"---///--"+info.getLrcUrl());
                    Log.e("---",info.getLrcId()+"---///--"+info.getLrcUrl());
                    break;
            }
        }
    };
    public void testDownInfo(){
        final DownloadInfo dinfo = new DownloadInfo(null);
        final MusicItem musicItem = new MusicItem();
        musicItem.set_id(1);
        musicItem.setArtist("许嵩");
        musicItem.setTitle("有何不可");
        new Thread(new Runnable() {
            @Override
            public void run() {
                NetMusicInfo info = dinfo.download(musicItem);
            }
        }).start();

    }
}