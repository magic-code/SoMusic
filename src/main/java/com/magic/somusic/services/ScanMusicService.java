package com.magic.somusic.services;

import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.magic.somusic.Config;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.utils.Pinyin4jUtil;

/**
 * 扫描音乐
 * Created by Administrator on 2015/10/2.
 */
public class ScanMusicService extends IntentService {


    public ScanMusicService() {
        super("com.magic.somusic.services.ScanMusicService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        boolean result = false;
        int count = 0;
        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.SIZE
        };
        String where = "mime_type in ('audio/mpeg','audio/x-ms-wma') and is_music > 0 ";
        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,projection,where,null,null);
        if (cursor!=null) {
            while (cursor.moveToNext()) {
                long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));  //byte
                if (size >= 700 * 1024){//700kb
                    int _id = cursor.getInt(cursor.getColumnIndex(MediaStore.Audio.Media._ID));
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    MusicItem music = new MusicItem();
                    music.set_id(_id);
                    music.setTitle(title);
                    music.setFilename(filename);
                    music.setArtist(artist);
                    music.setDuration(duration);
                    music.setCollect(Config.CollecteState.STATE_NOT_COLLECTED);//初始化为 未收藏
                    music.setLrcpath("");
                    music.setImagepath("");
                    music.setLetterTitle(Pinyin4jUtil.getPinYin(title).toLowerCase());
                    music.setLetterArtist(Pinyin4jUtil.getPinYin(artist).toLowerCase());
                    MusicDBHelper.getInstance(this).save(music);
                    count++;
                }
            }
            result = true;
            cursor.close();
        }
        if (!result)
            Log.e("scan","none music");
        Intent broadcast = new Intent();
        String action = result ? Config.Broadcast.SCAN_SUCCESS : Config.Broadcast.SCAN_FAIL;
        broadcast.setAction(action);
        broadcast.putExtra(Config.Broadcast.SCAN_COUNT,count);
        sendBroadcast(broadcast);

    }
}
