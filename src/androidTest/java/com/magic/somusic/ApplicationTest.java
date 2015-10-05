package com.magic.somusic;

import android.app.Application;
import android.content.Intent;
import android.test.ApplicationTestCase;
import android.util.Log;

import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.services.ScanMusicService;

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
}