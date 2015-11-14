package com.magic.somusic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/18.
 */
public class PagerSeekFrameLayout extends FrameLayout {
    private View seekView;
    private View seekbar = null;

    public PagerSeekFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

//    @Override
//    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        Log.e("----", "--" + ev.getRawY() + "----" + seekView.getTop());
//        if (ev.getRawY()>seekView.getTop())
//            return true;
//        return false;
//    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        seekView = this.getChildAt(1);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (seekbar==null)
            seekbar = ((LinearLayout)seekView).getChildAt(1);
        Log.e("---","seek top"+seekbar.getTop());
        //seekbar.onTouchEvent(event);

        return true;
    }
}
