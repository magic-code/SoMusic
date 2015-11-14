package com.magic.somusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.magic.somusic.Config;
import com.magic.somusic.domain.AppSetting;
import com.magic.somusic.domain.MusicItem;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/9/28.
 */
public class MusicDBHelper extends SQLiteOpenHelper {
    private static MusicDBHelper _instance;

    private static final String DB_NAME="somusic";
    private static final String TABLE_SET_NAME = "somusic_set";
    private static final int DB_VERSION = 1;
    private static final String TABLE_LOCAL_LIST = "local_list";
    private static final String CREATE_TABLE_LOCAL_LIST="CREATE TABLE "+TABLE_LOCAL_LIST+" (_id INTEGER PRIMARY KEY" +
            ",title VARCHAR(100),duration INTEGER,artist VARCHAR(50),album VARCHAR(100)," +
            "filename VARCHAR(100),collect INTEGER,lrcpath VARCHAR(100),imagepath VARCHAR(100))";
    private static final String CREATE_TABLE_SET = "CREATE TABLE "+TABLE_SET_NAME+" (id INTEGER PRIMARY KEY"+
            ",last_list INTEGER,last_music_pos INTEGER,last_music_id INTEGER,last_music_sec INTEGER" +
            ",last_model INTEGER,first_install INTEGER)";
    private MusicDBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static synchronized MusicDBHelper getInstance(Context context){
        if (_instance==null)
            _instance= new MusicDBHelper(context);
        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCAL_LIST);
        db.execSQL(CREATE_TABLE_SET);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void updateLrc(int id,String path){
        SQLiteDatabase db = _instance.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("lrcpath",path);
        db.update(TABLE_LOCAL_LIST,values,"_id=?",new String[]{id+""});
        db.close();
    }
    public void save(MusicItem item){
       SQLiteDatabase db =  _instance.getWritableDatabase();
        Cursor cursor = db.query(TABLE_LOCAL_LIST, new String[]{"_id"}, "_id=?", new String[]{String.valueOf(item.get_id())}, null, null, null);
        if (cursor.getCount()<=0) {
            ContentValues content = new ContentValues();
            content.put("_id", item.get_id());
            content.put("title", item.getTitle());
            content.put("filename", item.getFilename());
            content.put("artist", item.getArtist());
            content.put("album", item.getAlbum());
            content.put("duration", item.getDuration());
            content.put("collect", item.getCollect());
            content.put("imagepath",item.getImagepath());
            content.put("lrcpath",item.getLrcpath());
            db.insert(TABLE_LOCAL_LIST, null, content);
            db.close();
        }
    }

    public ArrayList<MusicItem> query(){
        SQLiteDatabase db =  _instance.getWritableDatabase();
        Cursor cursor = db.query(TABLE_LOCAL_LIST, null, null, null, null, null, null);
        ArrayList<MusicItem> list=null;
        if(cursor!=null) {
            list = new ArrayList<MusicItem>();
            while (cursor.moveToNext()) {
                MusicItem item = new MusicItem();
                int id = cursor.getInt(cursor.getColumnIndex("_id"));
                int  collecte = cursor.getInt(cursor.getColumnIndex("collect"));
                long duration = cursor.getLong(cursor.getColumnIndex("duration"));
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String filename = cursor.getString(cursor.getColumnIndex("filename"));
                String artist = cursor.getString(cursor.getColumnIndex("artist"));
                String album = cursor.getString(cursor.getColumnIndex("album"));
                String lrcpath = cursor.getString(cursor.getColumnIndex("lrcpath"));
                String imagepath = cursor.getString(cursor.getColumnIndex("imagepath"));
                item.set_id(id);
                item.setTitle(title);
                item.setArtist(artist);
                item.setAlbum(album);
                item.setDuration(duration);
                item.setFilename(filename);
                item.setCollect(collecte);
                item.setLrcpath(lrcpath);
                item.setImagepath(imagepath);
                list.add(item);
            }
        }
        return list;
    }

    public void initSetting(){
        if (getSetting()==null) {
            AppSetting setting = new AppSetting(1, 1, Config.PlayList.LIST_LOCAL, -1, 0, Config.PlayModel.LIST_REPEAT_MODEL, 0);
            saveSetting(setting);
        }
    }
    public void saveSetting(AppSetting setting){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",setting.getId());
        values.put("last_list",setting.getLast_list());
        values.put("last_music_id",setting.getLast_music_id());
        values.put("last_music_pos",setting.getLast_music_pos());
        values.put("last_music_sec",setting.getLast_music_sec());
        values.put("last_model",setting.getLast_model());
        values.put("first_install",setting.getFirst_install());
        db.insert(TABLE_SET_NAME,null,values);
        db.close();
    }
    public void updateSetting(AppSetting setting){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("id",setting.getId());
        values.put("last_list",setting.getLast_list());
        values.put("last_music_id",setting.getLast_music_id());
        values.put("last_music_pos",setting.getLast_music_pos());
        values.put("last_music_sec",setting.getLast_music_sec());
        values.put("last_model",setting.getLast_model());
        values.put("id",setting.getId());
        values.put("first_install",setting.getFirst_install());
        db.update(TABLE_SET_NAME, values, "id=?", new String[]{1 + ""});
        db.close();
    }
    public void updateMusicRecord(int id,int list,int sec,int model){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_music_id",id);
        values.put("last_music_sec",sec);
        values.put("last_list",list);
        values.put("last_model",model);
        db.update(TABLE_SET_NAME,values,null,null);
        db.close();
    }
    public int getLocalCount(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM "+TABLE_LOCAL_LIST,null);
        if (cursor!=null){
            cursor.moveToNext();
            return  cursor.getInt(0);
        }
        return 0;
    }
    public AppSetting getSetting(){
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.query(TABLE_SET_NAME,null,null,null,null,null,null);
        AppSetting appSetting=null;
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            int last_list = cursor.getInt(cursor.getColumnIndex("last_list"));
            int last_music_id = cursor.getInt(cursor.getColumnIndex("last_music_id"));
            int last_music_pos = cursor.getInt(cursor.getColumnIndex("last_music_pos"));
            int last_music_sec = cursor.getInt(cursor.getColumnIndex("last_music_sec"));
            int last_model = cursor.getInt(cursor.getColumnIndex("last_model"));
            int first_install = cursor.getInt(cursor.getColumnIndex("first_install"));
            appSetting = new AppSetting(id,first_install,last_list,last_music_id,last_music_sec,last_model,last_music_pos);
        }
        cursor.close();
        db.close();
        return appSetting;
    }
}
