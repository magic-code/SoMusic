package com.magic.somusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.magic.somusic.R;

/**
 * Created by Administrator on 2015/9/30.
 */
public class MyPagerFragment extends Fragment {
    private ScrollView m_scrollView;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_pager_fragment,null);
        m_scrollView = (ScrollView) view.findViewById(R.id.myfg_scrview);
        View linear = m_scrollView.getChildAt(0);
        linear.setPadding(linear.getPaddingLeft(),-200,linear.getPaddingRight(),linear.getPaddingBottom());

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
