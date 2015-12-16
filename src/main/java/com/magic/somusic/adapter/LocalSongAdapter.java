package com.magic.somusic.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.magic.somusic.Config;
import com.magic.somusic.R;
import com.magic.somusic.db.MusicDBHelper;
import com.magic.somusic.domain.MusicItem;
import com.magic.somusic.utils.Player;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/4.
 */
public class LocalSongAdapter extends BaseAdapter {

    private ArrayList<MusicItem> data;
    private Context context;
    private LayoutInflater inflater;
    public LocalSongAdapter(Context context,ArrayList<MusicItem> data){
        this.context = context;
        this.data = data;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).get_id();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null){
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_local_music_song,null);
            holder.num = (TextView) convertView.findViewById(R.id.tx_item_local_num);
            holder.title = (TextView) convertView.findViewById(R.id.tx_item_local_title);
            holder.artist = (TextView) convertView.findViewById(R.id.tx_item_local_artist);
            holder.menu = (ImageView) convertView.findViewById(R.id.iv_item_local_menu);
            holder.ll_menuContainr = (LinearLayout) convertView.findViewById(R.id.ll_item_menu_containr);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        final MusicItem item = data.get(position);
        String title = item.getTitle();
        String artist = item.getArtist();
        holder.num.setText((position+1)+"");
        holder.title.setText(title);
        holder.artist.setText(artist);
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View menu = LayoutInflater.from(context).inflate(R.layout.song_item_menu,null);
                LinearLayout llColl = (LinearLayout) menu.findViewById(R.id.ll_menu_collect);
                LinearLayout llDel = (LinearLayout) menu.findViewById(R.id.ll_menu_delete);
                LinearLayout llAlerm = (LinearLayout) menu.findViewById(R.id.ll_menu_alerm);
                llColl.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean flag = item.getCollect()==Config.CollecteState.STATE_COLLECTED ? false:true;
                        MusicDBHelper.getInstance(context).collection(item.get_id(),flag);
                    }
                });
                holder.ll_menuContainr.addView(menu);
            }
        });
        return convertView;
    }


    class ViewHolder{
        public TextView num;
        public TextView title;
        public TextView artist;
        public ImageView menu;
        public LinearLayout ll_menuContainr;
    }
}
