package com.magic.somusic.ui;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import com.magic.somusic.utils.ConvertDensityUtil;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/19.
 */
public class PagerFrameLayout extends FrameLayout {
    private float downx;
    private float downy;

    public PagerFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PagerFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        if (ev.getRawY()>getWidth()- (int)(scale*40+0.5f))
            return true;
        if(ev.getAction()==MotionEvent.ACTION_DOWN){
            downx = ev.getRawX();
            downy = ev.getRawY();
        }else if (ev.getAction()==MotionEvent.ACTION_MOVE){
            float absx = Math.abs(ev.getRawX()-downx);
            float absy = Math.abs(ev.getRawY()-downy);
            if (absx<absy && (absx>10 || absy>10)){
                return true;
            }
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
        if (ev.getRawY()>getWidth()- (int)(scale*40+0.5f))
            return false;
        return super.onTouchEvent(ev);
    }
}
