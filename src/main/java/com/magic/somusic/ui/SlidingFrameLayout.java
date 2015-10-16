package com.magic.somusic.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.magic.somusic.R;

/**
 * Created by Administrator on 2015/10/11.
 */
public class SlidingFrameLayout extends FrameLayout{
    private View contentView;
    private View slidingView;
    private int width;
    private int height;
    private boolean isOpen = false;
    private float downx;
    private  float downy;
    private int startOffset = 100;//能打开menu离屏幕下方的起始滑动坐标偏移
    private int openoffset = 300;//松手后如果menu显示出openoffset的高度就自动弹出menu
    private boolean flag=false;
    private boolean interceptFlag;
    private StateChangeListener listener=null;
    private float oldoffset=0;
    private boolean visibility = false;

    public SlidingFrameLayout(Context context) {
        super(context);
    }

    public SlidingFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        height = getBottom();

    }
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (bottom>0 && !flag) {
            height = bottom;
            Log.e("sliding frame", bottom + "---");
            slidingView.setTranslationY(bottom);
            flag = true;
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        width = getWidth();
        height = getHeight();
        contentView  = getChildAt(0);
        slidingView  = getChildAt(1);
    }



    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN){
//            float top = (float) (((View) contentView.findViewById(R.id.ll_control_panel)).getTop());
//            if(ev.getRawY() >= top){
//                interceptFlag = true;
//            }else{
//                interceptFlag = false;
//            }
//        }
//        return interceptFlag;

        if (interceptFlag){     //如果是在规定位置按下的动作才判断，否则不做处理直接抛给子控件处理
            if (ev.getAction() == MotionEvent.ACTION_MOVE) {
                float abx = Math.abs(downx - ev.getRawX());
                float aby = Math.abs(downy - ev.getRawY());
                if (abx< aby && (abx>10 || aby>10)) {   //判断是否是上下滑动的动作，并且设置一个阀值，提高可操作性
                    return true;
                }
                return false;
            }
        }
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            downx = ev.getRawX();
            downy = ev.getRawY();
            float top = (float) (((View) contentView.findViewById(R.id.ll_control_panel)).getTop());
            if(ev.getRawY() >= top || isOpen){  //如果menu是打开或按下位置是在控制条位置则可以拦截消息
                interceptFlag = true;
            }else{
                interceptFlag = false;
            }
            if (!interceptFlag)
                return false;
        }
        if (ev.getAction()==MotionEvent.ACTION_UP){
            //小于给定的阀值，代表单击事件，直接交给子控件处理
            if (Math.abs(ev.getRawX()-downx)<5 && Math.abs(downy-ev.getRawY())<5){
                return false;
            }
            if (!isOpen)
                interceptFlag = false;
        }
        return false;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        float x = event.getRawX();
        float y = event.getRawY();
        if (interceptFlag) {
            switch (action) {

                case MotionEvent.ACTION_MOVE:
                    float offset = downy - y;
                    if (offset>0 && oldoffset<=0){
                        if (listener!=null && !visibility) {
                            listener.visbilityChange(true);
                            visibility = true;
                        }
                    }
//                    else if (offset<=0&&oldoffset>0){
//                        if (listener!=null){
//                            listener.visbilityChange(false);
//                        }
//                    }
                    oldoffset = offset;
//                    if (!isOpen && (downy > (height - startOffset))) {
                    if (!isOpen)
                        slidingView.setTranslationY(height - offset);
                    else if (isOpen&& offset<0)
                        slidingView.setTranslationY(-offset);

//                    }
                    break;
                case MotionEvent.ACTION_UP:
                    float uoffset = downy - y;
                    if (!isOpen && (downy - y) > openoffset) {
                        slidingView.setTranslationY(0);
                        isOpen = true;
                        visibility = true;
                        if (listener!=null){
                            listener.openstateChange(true);
                        }
                    }else if (!isOpen){
                        visibility = false;
                        slidingView.setTranslationY(height);
                        if (listener!=null && visibility) {
                            listener.visbilityChange(false);
                        }
                    }else if (isOpen && -uoffset>=openoffset){
                        slidingView.setTranslationY(height);
                        isOpen = false;
                        visibility = false;
                        if (listener!=null){
                            listener.openstateChange(false);
                            listener.visbilityChange(false);
                        }
                    }else if (isOpen){
                        slidingView.setTranslationY(0);
                    }
                    interceptFlag = false;
                    break;
            }
        }
        return true;
    }
    public void openMenu(){
        slidingView.setTranslationY(0);
        isOpen = true;
        if (listener!=null)
            listener.openstateChange(true);
    }
    public void closeMenu(){
        slidingView.setTranslationY(height);
        isOpen = false;
        if (listener != null) {
            listener.openstateChange(false);
        }
    }
    public void setStateChangeListener(StateChangeListener listener){
        this.listener = listener;
    }
    public interface StateChangeListener{
        public void visbilityChange(boolean visbility);
        public void openstateChange(boolean open);
    }
}
