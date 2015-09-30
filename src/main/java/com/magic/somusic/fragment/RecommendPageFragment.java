package com.magic.somusic.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.magic.somusic.R;

/**
 * Created by Administrator on 2015/9/30.
 */
public class RecommendPageFragment extends Fragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recommend_page_fragment,null);
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
