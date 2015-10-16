package com.magic.somusic.utils;

import android.content.Context;
import android.media.audiofx.EnvironmentalReverb;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

import com.magic.somusic.Config;
import com.magic.somusic.domain.NetMusicInfo;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by Administrator on 2015/10/14.
 */
public class DownloadLrc {
    private Handler handler;
    private Context context;
    public DownloadLrc(Context context,Handler handler){
        this.handler = handler;
        this.context = context;
    }
    public String downloadLrc(String lrcurl) {
        String path = null;
        if (lrcurl==null || lrcurl.equals("")) {
            return path;
        }
        File saveDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/somusic/lrcfile");
        if (!saveDir.exists()){
            saveDir.mkdirs();
        }
        File saveFile = new File(saveDir,System.currentTimeMillis()+".lrc");
        try {
            URL url = new URL(lrcurl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(5 * 1000);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "image/gif,image/jpeg,image/pjpeg,application/xhtml+xml,application/xml");
            conn.setRequestProperty("Accept-Language", "zh-CN");
            conn.setRequestProperty("Referer", lrcurl);
            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/31.0.1650.63 Safari/537.36");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.connect();
            if (conn.getResponseCode() == 200) {
                InputStream is = conn.getInputStream();
                OutputStream os = new FileOutputStream(saveFile);
                byte[] buf = new byte[1024];
                int len = 0;
                while ((len=is.read(buf))!=-1){
                    os.write(buf,0,len);
                }
                os.close();
                is.close();
                path = saveFile.getAbsolutePath();
                if (handler!=null){
                    Message mes = handler.obtainMessage(Config.DOWNLOAD_LRC_SUC);
                    mes.obj = path;
                    handler.sendMessage(mes);
                }
            }
        } catch (Exception e) {
            if (handler!=null){
                Message mes = handler.obtainMessage(Config.DOWNLOAD_LRC_ERR);
                handler.sendMessage(mes);
            }
            throw new RuntimeException("don't connection this url");
        }

        return path;
    }
}
