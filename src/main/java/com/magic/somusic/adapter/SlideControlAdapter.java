package com.magic.somusic.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.magic.somusic.R;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.services.PlayMusicService;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/8.
 */
public class SlideControlAdapter extends PagerAdapter {
    private PlayMusicService musicService;
    private Context context;
    private LayoutInflater inflater;
    private ArrayList<View> views = new ArrayList<View>();
    public SlideControlAdapter(Context context, PlayMusicService musicService){
        this.musicService = musicService;
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        for (int i=0;i<3;i++) {
            View view = inflater.inflate(R.layout.item_page_control, null);
//            TextView title = (TextView) view.findViewById(R.id.tx_main_title);
//            TextView artist = (TextView) view.findViewById(R.id.tx_main_artist);
//            if (musicService != null) {
//                int cpos = musicService.getCurrentPos();
//                MusicItem item = musicService.getMusic(cpos + i - 1);
//                title.setText(item.getTitle());
//                artist.setText(item.getArtist());
//            }
            views.add(view);
        }
    }
    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        if (view.getParent()==null)
            container.addView(view);
        return view;
    }

    public View getView(int pos){
        return views.get(pos);
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(container.getChildAt(position));
    }
}
