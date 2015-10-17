package com.magic.somusic.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.magic.somusic.Config;
import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.adapter.LrcViewsPageAdapter;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.LrcRow;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.domain.NetMusicInfo;
import com.magic.somusic.services.PlayMusicService;
import com.magic.somusic.ui.LrcMulityLineView;
import com.magic.somusic.ui.TwoLinesLrcView;
import com.magic.somusic.utils.DownloadInfo;
import com.magic.somusic.utils.LrcLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/13.
 */
public class LrcViewFragment extends Fragment {
    private PlayMusicService musicservice;
    private ViewPager vpLrc;
    private TextView tx_title;
    private TextView tx_artist;
    private ImageView iv_back;
    private ImageView iv_collection;
    private ImageView iv_changeskin;
    private DownloadInfo downloadUtil;
    private View mulLrcLayout;
    private View twoLrcLayout;
    private LrcLoader lrcLoader = new LrcLoader();
    private ArrayList<LrcRow> lrcRows;
    private PlayMusicService musicService;
    private LrcViewsPageAdapter lrcPageAdapter;
    private ArrayList<View> lrcViews = new ArrayList<View>();
    private LrcMulityLineView mulLrcView;
    private TwoLinesLrcView twoLrcView;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case Config.DOWNLOAD_INFO_SUS:
                    NetMusicInfo info = (NetMusicInfo) msg.obj;
                    int musicId = msg.arg1;
                    MusicDBHelper.getInstance(getActivity()).updateLrc(musicId,info.getLrcPath());
                    Toast.makeText(getActivity(),R.string.down_lrc_suc,Toast.LENGTH_LONG).show();
                    break;
                case Config.DOWNLOAD_INFO_ERR:
                    Toast.makeText(getActivity(),R.string.down_lrc_err,Toast.LENGTH_LONG).show();
                    break;
            }
        }
    };

    private LrcPosThread lrcPosThread = new LrcPosThread();
    private Handler lrcPosHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int pos = msg.arg1;
            mulLrcView.setPos(pos);
            twoLrcView.setPos(pos);
            lrcPosHandler.post(lrcPosThread);
        }
    };
    private MusicItem musicItem;

    @Override
    public void onStart() {
        super.onStart();
        initComponent(getView());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lrc,null);
        mulLrcLayout = inflater.inflate(R.layout.mul_line_lrc_layout,null);
        twoLrcLayout = inflater.inflate(R.layout.two_line_lrc_layout,null);
        mulLrcView = (LrcMulityLineView) mulLrcLayout.findViewById(R.id.mulline_lrcview);
        twoLrcView = (TwoLinesLrcView)twoLrcLayout.findViewById(R.id.lrc_fragment_two_line);
        lrcViews.add(twoLrcLayout);
        lrcViews.add(mulLrcLayout);
        //initComponent(view);
        return view;
    }
    private void initComponent(View view){
        tx_title = (TextView) view.findViewById(R.id.lrc_action_title);
        tx_artist = (TextView) view.findViewById(R.id.lrc_action_artist);
        iv_back = (ImageView) view.findViewById(R.id.lrc_action_back);
        iv_collection = (ImageView) view.findViewById(R.id.lrc_action_collection);
        iv_changeskin = (ImageView) view.findViewById(R.id.lrc_action_changeskin);
        //mulLrcView = (LrcMulityLineView) view.findViewById(R.id.mulline_lrcview);
        vpLrc = (ViewPager) view.findViewById(R.id.vp_lrc);
        musicservice = ((MainActivity)getActivity()).musicService;
        musicItem = musicservice.getMusic(musicservice.getCurrentPos());
        tx_title.setText(musicItem.getTitle());
        tx_artist.setText(musicItem.getArtist());
        if (musicItem.getCollect()== Config.CollecteState.STATE_COLLECTED){
            iv_collection.setImageResource(R.mipmap.collection_true);
        }else{
            iv_collection.setImageResource(R.mipmap.collection_false);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //关闭歌词界面，返回主界面
            }
        });
        if (musicItem.getLrcpath().equals("") || musicItem.getLrcpath()==null){
            showDownloadDialog(musicItem);
        }
        if (!musicItem.getLrcpath().equals("")){
            lrcRows = lrcLoader.loadLrc(musicItem.getLrcpath());
            mulLrcView.setLrcRows(lrcRows);
            mulLrcView.setPlaying(musicservice.getPlaying()==Config.PlayState.STATE_PLAYING ? true:false);
            twoLrcView.setmLrcRows(lrcRows);
            twoLrcView.setIsPlaying(musicservice.getPlaying()==Config.PlayState.STATE_PLAYING ? true:false);
            mulLrcView.post(lrcPosThread);
            twoLrcView.post(lrcPosThread);
        }

        lrcPageAdapter = new LrcViewsPageAdapter(lrcViews);
        vpLrc.setAdapter(lrcPageAdapter);
    }
    class LrcPosThread implements Runnable{

        @Override
        public void run() {
            Message mes =  lrcPosHandler.obtainMessage(0,musicservice.getCurrentDuration(),0);
            lrcPosHandler.sendMessage(mes);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void showDownloadDialog(final MusicItem musicItem){
        AlertDialog dialog = new  AlertDialog.Builder(getActivity())
            .setTitle(R.string.app_name).setMessage(R.string.no_lrc_download)
                .setNegativeButton(R.string.cancle,new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton(R.string.yes,new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        downloadUtil = new DownloadInfo(getActivity(),handler);
                        downloadUtil.download(musicItem);
                    }
                }).start();
            }
        }).create();
        dialog.show();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicservice = ((MainActivity)getActivity()).musicService;

    }

}
