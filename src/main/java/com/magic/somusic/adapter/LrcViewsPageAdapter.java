package com.magic.somusic.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/16.
 */
public class LrcViewsPageAdapter extends PagerAdapter {

    private ArrayList<View> views;
    public LrcViewsPageAdapter(ArrayList<View> views){
        this.views = views;
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = views.get(position);
        if (view.getParent()==null)
            container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        View view = views.get(position);
        if (view.getParent()!=null)
            container.removeView(view);
    }

    @Override
    public int getCount() {
        return views.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view==object;
    }
}
