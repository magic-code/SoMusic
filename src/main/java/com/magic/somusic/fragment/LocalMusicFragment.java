package com.magic.somusic.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.adapter.LocalSongAdapter;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.ui.PinYinDragPanel;

import java.util.ArrayList;
import java.util.zip.Inflater;

public class LocalMusicFragment extends Fragment {

    private ViewPager viewPager;
    private PinYinDragPanel pinYinDragPanel;
    public LocalMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_local_music,null);
        viewPager = (ViewPager) view.findViewById(R.id.vp_local_music);
        viewPager.setAdapter(new LocalPageAdapter(getActivity()));
        viewPager.setCurrentItem(0);
        pinYinDragPanel = (PinYinDragPanel) view.findViewById(R.id.pp_list);
        pinYinDragPanel.setOnLetterChangedListener(new PinYinDragPanel.OnLetterChangedListener() {
            @Override
            public void onLetterChangerListener(int pos, String letter) {

            }
        });
        return view;
    }
    private class LocalPageAdapter extends PagerAdapter{
        private ArrayList<View> viewList = new ArrayList<View>();
        private Context context;
        private LayoutInflater inflater;
        LocalPageAdapter(Context context){
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            ListView listView = new ListView(context);
            ViewGroup.LayoutParams pa = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            listView.setLayoutParams(pa);
            listView.setBackgroundColor(Color.WHITE);

            viewList.add(listView);
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //return super.instantiateItem(container, position);
            View view=null;
            switch (position){
                case 0:
                    ListView listView = (ListView) viewList.get(0);
                    //LinearLayout ll =(LinearLayout) inflater.inflate(R.layout.list_local,null);
                    //listView = (ListView) ll.findViewById(R.id.local_list);
                    final ArrayList<MusicItem> musics = MusicDBHelper.getInstance(context).query();

                    listView.setAdapter(new LocalSongAdapter(context, musics));
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((MainActivity)getActivity()).play(musics,position);

                        }
                    });
                    container.addView(listView);
                    return listView;
                //break;
            }
            return  view;
        }

        @Override
        public int getCount() {
            return viewList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view==object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
           // super.destroyItem(container, position, object);
            container.removeView(viewList.get(position));
        }
    }


}
