package com.magic.somusic.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
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
import android.widget.SeekBar;
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
import com.magic.somusic.ui.PagerFrameLayout;
import com.magic.somusic.ui.TwoLinesLrcView;
import com.magic.somusic.utils.DownloadInfo;
import com.magic.somusic.utils.LrcLoader;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/13.
 */
public class LrcViewFragment extends Fragment {
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
    private int num = 0;
    private TextView txCurDuration;
    private TextView txDuration;
    private SeekBar seekBar;
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
                    musicService.updateLrc(musicId,info.getLrcPath());
                    loadData();
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
            txCurDuration.setText(LrcLoader.convertTime(pos));
            seekBar.setProgress(pos);
        }
    };
    private MusicItem musicItem;
    private ImageView mulIvPrev;
    private ImageView mulIvPlay;
    private ImageView mulIvNext;
    private ImageView mulIvList;
    private ImageView mulIvshare;
    private ImageView twoIvPrev;
    private ImageView twoIvPlay;
    private ImageView twoIvNext;
    private ImageView ivCollection;


    @Override
    public void onStart() {
        super.onStart();
        initComponent(getView());
        ((MainActivity)getActivity()).setMusicChangeListener(new MainActivity.MuiscChangeCallBack() {
            @Override
            public void musicChange() {
                loadData();
                musicService.getCurrentPos();
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_lrc,null);
        mulLrcLayout = inflater.inflate(R.layout.mul_line_lrc_layout,null);
        twoLrcLayout = inflater.inflate(R.layout.two_line_lrc_layout,null);
        mulLrcView = (LrcMulityLineView) mulLrcLayout.findViewById(R.id.mulline_lrcview);
        twoLrcView = (TwoLinesLrcView)twoLrcLayout.findViewById(R.id.lrc_fragment_two_line);
        iv_collection = (ImageView) view.findViewById(R.id.lrc_action_collection);
        iv_collection.setOnClickListener(new CollectionClickListener());
        mulIvPrev = (ImageView)mulLrcLayout.findViewById(R.id.mullrc_prev);
        mulIvPlay = (ImageView)mulLrcLayout.findViewById(R.id.mullrc_play);
        mulIvNext = (ImageView)mulLrcLayout.findViewById(R.id.mullrc_next);
        mulIvList = (ImageView)mulLrcLayout.findViewById(R.id.mullrc_list);
        mulIvshare = (ImageView)mulLrcLayout.findViewById(R.id.mullrc_share);

        twoIvPrev = (ImageView) twoLrcLayout.findViewById(R.id.two_lrc_prev);
        twoIvPlay = (ImageView) twoLrcLayout.findViewById(R.id.two_lrc_play);
        twoIvNext = (ImageView) twoLrcLayout.findViewById(R.id.two_lrc_next);

        ControlMusicOnClickListener listener = new ControlMusicOnClickListener();
        mulIvPrev.setOnClickListener(listener);
        mulIvPlay.setOnClickListener(listener);
        mulIvNext.setOnClickListener(listener);
        twoIvPrev.setOnClickListener(listener);
        twoIvPlay.setOnClickListener(listener);
        twoIvNext.setOnClickListener(listener);

        lrcViews.add(twoLrcLayout);
        lrcViews.add(mulLrcLayout);
        txCurDuration = (TextView) view.findViewById(R.id.lrc_frag_cur_duration);
        txDuration = (TextView) view.findViewById(R.id.lrc_frag_total_duration);
        seekBar = (SeekBar) view.findViewById(R.id.lrc_frag_seekbar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser)
                    musicService.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //initComponent(view);

        return view;
    }
    /**点击收藏按钮*/
    private class CollectionClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            MusicItem item = musicService.getMusic(musicService.getCurrentPos());
            if (item.getCollect()==Config.CollecteState.STATE_NOT_COLLECTED) {
                musicService.updateCollection(item.get_id(),true);
                iv_collection.setBackgroundResource(R.mipmap.collection_true);
            }else{
                musicService.updateCollection(item.get_id(),false);
                iv_collection.setBackgroundResource(R.mipmap.collection_false);
            }
        }
    }
    class ControlMusicOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {

            Log.e("LrcViewFragment", "controlmusic onclick");
            int id = v.getId();
            Intent intent = null;
            if (id==R.id.two_lrc_prev || id==R.id.mullrc_prev){
                intent = new Intent(Config.Broadcast.MUSIC_PREVIOUS);
            }else if ((id==R.id.two_lrc_play || id==R.id.mullrc_play)){
                if (musicService.getPlaying()==Config.PlayState.STATE_PLAYING){
                    intent = new Intent(Config.Broadcast.MUSIC_PAUSE);
                    ((ImageView)v).setImageResource(R.mipmap.img_appwidget_pause);
                }else{
                    intent = new Intent(Config.Broadcast.MUSIC_PLAY);
                    ((ImageView)v).setImageResource(R.mipmap.img_appwidget_play);
                }
            }else if (id==R.id.two_lrc_next || id==R.id.mullrc_next){
                intent = new Intent(Config.Broadcast.MUSIC_NEXT);
            }
            LrcViewFragment.this.getActivity().sendBroadcast(intent);
        }
    }
    private void initComponent(View view){
        tx_title = (TextView) view.findViewById(R.id.lrc_action_title);
        tx_artist = (TextView) view.findViewById(R.id.lrc_action_artist);
        iv_back = (ImageView) view.findViewById(R.id.lrc_action_back);
        iv_collection = (ImageView) view.findViewById(R.id.lrc_action_collection);
        iv_changeskin = (ImageView) view.findViewById(R.id.lrc_action_changeskin);
        //mulLrcView = (LrcMulityLineView) view.findViewById(R.id.mulline_lrcview);
        vpLrc = (ViewPager) view.findViewById(R.id.vp_lrc);

        musicService = ((MainActivity)getActivity()).musicService;
        loadData();
    }

    public void loadData(){
        mulLrcView.setLrcRows(null);
        twoLrcView.setmLrcRows(null);
        musicItem = musicService.getMusic(musicService.getCurrentPos());
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
            mulLrcView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDownloadDialog(musicItem);
                }
            });
        }
        if (!musicItem.getLrcpath().equals("")){
            lrcRows = lrcLoader.loadLrc(musicItem.getLrcpath());
            mulLrcView.setLrcRows(lrcRows);
            mulLrcView.setPlaying(musicService.getPlaying()==Config.PlayState.STATE_PLAYING ? true:false);
            twoLrcView.setmLrcRows(lrcRows);
            twoLrcView.setIsPlaying(musicService.getPlaying()==Config.PlayState.STATE_PLAYING ? true:false);
            mulLrcView.post(lrcPosThread);
            twoLrcView.post(lrcPosThread);
        }
        txDuration.setText(LrcLoader.convertTime(musicService.getDuration()));
        seekBar.setMax(musicService.getDuration());
        lrcPageAdapter = new LrcViewsPageAdapter(lrcViews);
        vpLrc.setAdapter(lrcPageAdapter);
        vpLrc.setCurrentItem(1);
    }

    class LrcPosThread implements Runnable{

        @Override
        public void run() {
            Message mes =  lrcPosHandler.obtainMessage(0,musicService.getCurrentDuration(),0);
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
        musicService = ((MainActivity)getActivity()).musicService;

    }


}
