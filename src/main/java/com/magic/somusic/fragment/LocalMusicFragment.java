package com.magic.somusic.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;

import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.adapter.LocalSongAdapter;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.ui.PinYinDragPanel;
import com.magic.somusic.utils.Pinyin4jUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import java.util.HashMap;
import java.util.logging.LogRecord;
import java.util.zip.Inflater;

public class LocalMusicFragment extends Fragment {

    private ViewPager viewPager;
    private PinYinDragPanel pinYinDragPanel;
    private ArrayList<MusicItem> musics;
    private ImageView ivPopUpMenu;
    private static final int ACTION_SORT_COML = 0x0001;

    private ListView listView;
    private PopupWindow popupWindow = null;
    private boolean popupisShow = false;
    private boolean dragingPinyin =  false;

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
        ivPopUpMenu = (ImageView) view.findViewById(R.id.iv_local_popup_menu);
        ivPopUpMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu();
            }
        });
        viewPager.setAdapter(new LocalPageAdapter(getActivity()));
        viewPager.setCurrentItem(0);
        pinYinDragPanel = (PinYinDragPanel) view.findViewById(R.id.pp_list);
        pinYinDragPanel.setOnLetterChangedListener(new PinYinDragPanel.OnLetterChangedListener() {
            @Override
            public void onLetterChangerListener(int pos, String letter) {
                toLetterPos(letter);
            }

            @Override
            public void onStateChangeListener(boolean onPanel) {
                dragingPinyin = onPanel;
            }
        });
        return view;
    }
    private void toLetterPos(String letter){
        letter = letter.toLowerCase();
        for (int i=0;i<musics.size();i++){
            MusicItem item = musics.get(i);
            String h = item.getLetterTitle().substring(0, 1);
            if (!letter.equals("#")) {
                if (h.equals(letter)) {
                    listView.setSelection(i);
                    break;
                }
            }else{
                char c = h.charAt(0);
                if (!(c>=97 && c<=122)){
                    listView.setSelection(i);
                    break;
                }
            }
        }
    }
    private void showPopupMenu(){
        if (popupWindow == null) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_local_popupmenu,null);

            ListView list = (ListView) view.findViewById(R.id.local_pop_list);
            ArrayList<HashMap<String,String>> array = new ArrayList<HashMap<String,String>>();
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("opt", getResources().getString(R.string.scanmusic));
            array.add(map);
            map = new HashMap<String,String>();
            map.put("opt", getResources().getString(R.string.sortbytitle));
            array.add(map);
            map = new HashMap<String,String>();
            map.put("opt", getResources().getString(R.string.sortbyartist));
            array.add(map);

            list.setAdapter(new SimpleAdapter(getActivity(), array, android.R.layout.simple_list_item_1,
                    new String[]{"opt"}, new int[]{android.R.id.text1}));
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    switch (position) {
                        case 0: //…®√Ë“Ù¿÷

                            break;
                        case 1: //∞¥’’∏Ë√˚≈≈–Ú
                            sortTitle();
                            break;
                        case 2: //∞¥’’∏Ë ÷√˚≈≈–Ú
                            sortArtist();
                            break;
                    }
                }
            });
            popupWindow = new PopupWindow(view,ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (!popupisShow){
            popupWindow.showAsDropDown(ivPopUpMenu,-popupWindow.getWidth(),15);
            popupisShow = true;
        }else{
            popupWindow.dismiss();
            popupisShow = false;
        }
    }

    public void sortTitle(){
        //∏Ë√˚≈≈–Ú
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(musics, new TitleCompater());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(new LocalSongAdapter(getActivity(), musics));
                    }
                });
                //handler.sendEmptyMessage(ACTION_SORT_COML);
            }
        }).start();
    }

    public void sortArtist(){
        //∏Ë√˚≈≈–Ú
        new Thread(new Runnable() {
            @Override
            public void run() {
                Collections.sort(musics, new ArtistCOmpater());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        listView.setAdapter(new LocalSongAdapter(getActivity(), musics));
                    }
                });
                //handler.sendEmptyMessage(ACTION_SORT_COML);
            }
        }).start();
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
                    listView = (ListView) viewList.get(0);
                    View emptyView = LayoutInflater.from(context).inflate(R.layout.list_empty_view,null);

                    listView.setAdapter(null);
                    //LinearLayout ll =(LinearLayout) inflater.inflate(R.layout.list_local,null);
                    //listView = (ListView) ll.findViewById(R.id.local_list);
                    musics = MusicDBHelper.getInstance(context).query();

                    //∏Ë√˚≈≈–Ú
                    sortTitle();

                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ((MainActivity) getActivity()).play(musics, position);

                        }
                    });
                    listView.setOnScrollListener(new AbsListView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(AbsListView view, int scrollState) {

                        }

                        @Override
                        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                            if (!dragingPinyin) {
                                String l = musics.get(view.getFirstVisiblePosition()).getLetterTitle().substring(0, 1);
                                Log.e("local",l+".......");
                                pinYinDragPanel.setPos(l);
                            }
                        }
                    });
                    container.addView(listView);
                    ((ViewGroup)listView.getParent()).addView(emptyView);
                    listView.setEmptyView(emptyView);

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

    class ArtistCOmpater implements Comparator<MusicItem>{
        @Override
        public int compare(MusicItem lhs, MusicItem rhs) {

            String ll = lhs.getLetterArtist();
            String lr = rhs.getLetterArtist();

            char lhead = ll.charAt(0);
            char rhead = lr.charAt(0);
            if (!(lhead>=97 && lhead <=122)){
                if (rhead>=97 && rhead <=122){
                    return 1;
                }
            }
            return ll.compareTo(lr);
        }
    }

    class TitleCompater implements Comparator<MusicItem>{

        @Override
        public int compare(MusicItem lhs, MusicItem rhs) {

            String ll = lhs.getLetterTitle();
            String lr = rhs.getLetterTitle();

            char lhead = ll.charAt(0);
            char rhead = lr.charAt(0);
            if (!(lhead>=97 && lhead <=122)){
                if (rhead>=97 && rhead <=122){
                    return 1;
                }
            }
            if (!(rhead>=97 && rhead <=122)){
                if (lhead>=97 && lhead <=122){
                    return -1;
                }
            }
            return ll.compareTo(lr);
        }
    }
}
