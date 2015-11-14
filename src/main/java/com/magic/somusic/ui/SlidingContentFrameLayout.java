package com.magic.somusic.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewParent;
import android.widget.FrameLayout;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/18.
 */
public class SlidingContentFrameLayout extends FrameLayout {

    private boolean isOpen=false;

    public SlidingContentFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (isOpen) //当slidingmenu 歌词菜单打开时，无条件拦截此时处在下面的contentframe即本view的事件，并在onTouchEvent返回false，对相同动作不作处理，以免歌词界面的事件穿透到本view中
            return true;
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isOpen)
            return false;   //对打开菜单以后的 触摸事件不作处理（防止事件穿透菜单）
        return super.onTouchEvent(event);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }
    public void setSlidingState(boolean isOpen){
        this.isOpen = isOpen;
    }
}
