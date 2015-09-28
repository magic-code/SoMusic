package com.magic.somusic.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.magic.somusic.domain.MusicItem;

/**
 * Created by Administrator on 2015/9/28.
 */
public class MusicDBHelper extends SQLiteOpenHelper {
    private static MusicDBHelper _instance;

    private static final String DB_NAME="somusic";
    private static final int DB_VERSION = 1;
    private static final String TABLE_LOCAL_LIST = "local_list";
    private static final String CREATE_TABLE_LOCAL_LIST="CREATE TABLE "+TABLE_LOCAL_LIST+" (_id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ",name VARCHAR(100),time INTEGER,singer VARCHAR(50),special VARCHAR(100)" +
            "filename VARCHAR(100),direction VARCHAR(100),collect INTEGER";
    private MusicDBHelper(Context context){
        super(context,DB_NAME,null,DB_VERSION);
    }

    public static MusicDBHelper getInstance(Context context){
        if (_instance==null)
            _instance= new MusicDBHelper(context);
        return _instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_LOCAL_LIST);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public void save(MusicItem item){
       SQLiteDatabase db =  _instance.getWritableDatabase();
        ContentValues content = new ContentValues();
        content.put("_id",item.get_id());
        content.put("name",item.getName());
        content.put("filename",item.getFilename());
        content.put("singer",item.getSinger());
        content.put("special",item.getSpecial());
        content.put("direction",item.getDirection());
        content.put("time",item.getTime());
        content.put("collect",item.getCollect());

       db.insert(TABLE_LOCAL_LIST,null,content);
    }
}
