package com.magic.somusic.fragment;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.magic.somusic.MainActivity;
import com.magic.somusic.R;
import com.magic.somusic.db.MusicDBHelper;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2015/9/30.
 */
public class MyPagerFragment extends Fragment {
    private ScrollView m_scrollView;
    private LinearLayout ll_local_music;
    private TextView txLocaleCount;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_pager_fragment,null);
        m_scrollView = (ScrollView) view.findViewById(R.id.myfg_scrview);
        txLocaleCount = (TextView) view.findViewById(R.id.tx_local_count);
        txLocaleCount.setText(MusicDBHelper.getInstance(getActivity()).getLocalCount()+" 首");
        View linear = m_scrollView.getChildAt(0);
        linear.setPadding(linear.getPaddingLeft(),-200,linear.getPaddingRight(),linear.getPaddingBottom());
        ll_local_music = (LinearLayout)view.findViewById(R.id.ll_local_music);
        ll_local_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                activity.toLocalMusicView();
            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
