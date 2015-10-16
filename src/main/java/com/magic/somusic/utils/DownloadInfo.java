package com.magic.somusic.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.magic.somusic.Config;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.domain.NetMusicInfo;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Administrator on 2015/10/13.
 */
public class DownloadInfo {
    private Handler handler;
    private Context context;
    public DownloadInfo(Context context,Handler handler){
        this.handler = handler;
        this.context = context;
    }
    public NetMusicInfo download(MusicItem musicItem){

        String url = "http://box.zhangmen.baidu.com/x?op=12&count=1&title="
                +musicItem.getTitle().replaceAll("</?[^<]+>", "")+"$$"+musicItem.getArtist().replaceAll("</?[^<]+>", "")+"$$$$";
        url = url.trim();
        HttpClient client = new DefaultHttpClient();
        client.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8")
                .setParameter(CoreConnectionPNames.SO_TIMEOUT,5000).setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT,5000);
        HttpGet get = new HttpGet(url);
        StringBuilder xmls = new StringBuilder();
        BufferedReader br = null;
        try {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            if (entity!=null){
                br = new BufferedReader(new InputStreamReader(entity.getContent()));
                String line;
                while ((line=br.readLine())!=null){
                    xmls.append(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (handler!=null){
                Message mes = handler.obtainMessage(Config.DOWNLOAD_INFO_ERR);
                handler.sendMessage(mes);
            }
        }catch (Exception e){
            e.printStackTrace();
            if (handler!=null){
                Message mes = handler.obtainMessage(Config.DOWNLOAD_INFO_ERR);
                handler.sendMessage(mes);
            }
        }finally {
            if (br!=null)
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        NetMusicInfo info= MusicInfoParser.parserMusicInfo(xmls.toString());
        if (info.getLrcId()!=0){
            DownloadLrc downloadLrc = new DownloadLrc(context,handler);
            String path = downloadLrc.downloadLrc(info.getLrcUrl());
            if (path!=null) {
                info.setLrcPath(path);
                if (handler != null) {
                    Message mes = handler.obtainMessage(Config.DOWNLOAD_INFO_SUS, musicItem.get_id(), 0);
                    mes.obj = info;
                    handler.sendMessage(mes);
                }
            }else{
                if (handler!=null) {
                    Message mes = handler.obtainMessage(Config.DOWNLOAD_INFO_ERR);
                    handler.sendMessage(mes);
                }
            }
        }else{
            if (handler!=null) {
                Message mes = handler.obtainMessage(Config.DOWNLOAD_LRC_ERR);
                handler.sendMessage(mes);
            }
        }
        return info;

    }
}
