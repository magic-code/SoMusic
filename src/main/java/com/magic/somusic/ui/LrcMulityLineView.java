package com.magic.somusic.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.magic.somusic.R;
import com.magic.somusic.domain.LrcRow;
import com.magic.somusic.utils.ConvertDensityUtil;

import java.util.ArrayList;

/**
 * Created by Administrator on 2015/10/15.
 */
public class LrcMulityLineView extends View {
    private Context context;
    private ConvertDensityUtil densityutil = new ConvertDensityUtil(getContext());
    /**高亮显示的行 num */
    private int mHighLightRow=0;
    /**高亮行显示的字体大小*/
    private int mHighLightFontSize = densityutil.sp2px(20);
    /**高亮行显示的字体颜色*/
    private int mHighLightFontColor = Color.WHITE;
    /**拖动线的颜色*/
    private int mSeekLineColor = Color.rgb(255,116,253);
    /**拖动线的 粗细*/
    private int mSeekLineWidth = 3;
    /**拖动时显示的时间的 文字颜色*/
    private int mSeekTimeFontColor = Color.rgb(255,116,253);
    /**拖动时显示的时间的 文字大小*/
    private int mSeekTimeFontSize = densityutil.sp2px(16);
    /**拖动线的Y坐标*/
    private int mHighRowY;
    /**拖动线的X偏移*/
    private int mSeekLinePaddingX = densityutil.dp2px(10);
    /**每行之间的间隔*/
    private int mRowPadding = densityutil.dp2px(8);
    /**其他各行的 文字大小*/
    private int mRowFontSize = densityutil.sp2px(16);
    /**其他各行的 文字颜色*/
    private int mRowFontColor = Color.GRAY;
    /**标记其他行的 迭代变量*/
    private int mRowNum = 0;

    /**存放歌词LrcRow的 list*/
    private ArrayList<LrcRow> list;
    /**画笔*/
    private Paint mPaint = new Paint();

    /**当没有歌词或歌词错误时用来显示提示信息的文本大小*/
    private int mTipFontSize = densityutil.sp2px(24);
    /**当没有歌词或歌词错误时用来显示提示信息的文本颜色*/
    private int mTipFontColor = Color.RED;

    /**控件的高度*/
    private int height;
    /**控件的宽度*/
    private int width;
    /**提示信息*/
    private String mTipContent = getContext().getResources().getString(R.string.lrc_tip);
    /**正常模式*/
    private static final int NORMAL_MODE = 0x0001;
    /**拖动模式*/
    private static final int SEEK_MODE = 0x0002;
    /**缩放模式*/
    private static final int SCALE_MODE = 0x0003;
    /**View的显示模式*/
    private int mViewMode = NORMAL_MODE;
    /**高亮行的Y坐标*/
    private int highLightRowY=0;
    private int delay=-1;

    private TextPaint textPaint = new TextPaint();
    private float downY=0;
    private boolean isPlaying = false;
    private boolean flag = true;
    private Thread thread=null;
    private float downx;
    private float downtime;
    private float presstime=0;

    public void setLrcRows(ArrayList<LrcRow> list){
        this.list = list;
        postInvalidate();
        flag = false;
        flag = true;
        delay=-1;
        if (list!=null && list.size()>0) {
            mHighLightRow=0;
            moveHighLight();
        }
    }

    public LrcMulityLineView(Context context) {
        super(context);
        this.context = context;
    }

    public LrcMulityLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public LrcMulityLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public LrcMulityLineView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
    }

    public void moveHighLight(){
//        if (thread!=null)
//            return;
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(flag && list!=null && list.size()>0) {
                    if (isPlaying) {
                        if (delay == -1) {
                            int time = list.get(mHighLightRow).time;
                            int ntime = 0;
                            if (mHighLightRow!=list.size()-1)
                                ntime = list.get(mHighLightRow + 1).time;
                            else
                                ntime = time+2000;
                            delay = (int) ((ntime - time) / (mRowPadding+mRowFontSize));
                        }
                        highLightRowY -= 1;
                        try {
                            Thread.sleep((long) delay);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        thread.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();
        if (list==null || list.size()<=0){
            if (mTipContent!=null) {
                mPaint.setColor(mTipFontColor);
                mPaint.setTextSize(mTipFontSize);
                mPaint.setTextAlign(Paint.Align.CENTER);
                canvas.drawText(mTipContent, width / 2, height / 2 - mTipFontSize, mPaint);
            }
            return;
        }
        int rowX= width/2;
        if (highLightRowY<=0)
            highLightRowY = height/2+mRowFontSize;

        //显示 高亮的行
        String highLightContent = list.get(mHighLightRow).content;
        mPaint.setColor(mHighLightFontColor);
        mPaint.setTextSize(mHighLightFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setAntiAlias(true);
//        textPaint.setColor(mHighLightFontColor);
//        textPaint.setTextSize(mHighLightFontSize);
//        textPaint.setTextAlign(Paint.Align.CENTER);
//        StaticLayout layout = new StaticLayout(highLightContent,textPaint,width-densityutil.dp2px(20)
//                ,Layout.Alignment.ALIGN_CENTER,1f,1f,true);
//        canvas.save();
//        canvas.translate(rowX,highLightRowY);
//        layout.draw(canvas);
//        canvas.restore();
        canvas.drawText(highLightContent, rowX, highLightRowY, mPaint);

        //显示拖动线 和时间
        if (mViewMode==SEEK_MODE){
            mPaint.setColor(mSeekLineColor);
            mPaint.setStrokeWidth(mSeekLineWidth);
            canvas.drawLine(mSeekLinePaddingX,height/2,width-mSeekLinePaddingX,height/2,mPaint);

            mPaint.setColor(mSeekTimeFontColor);
            mPaint.setTextSize(mSeekTimeFontSize);
            mPaint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(list.get(mHighLightRow).stime.substring(0,5),mSeekLinePaddingX,height/2-mSeekTimeFontSize,mPaint);
        }
        //显示高亮部分上方的歌词
        mRowNum = mHighLightRow - 1;
        int rowY = highLightRowY - mRowPadding - mRowFontSize;
        mPaint.setColor(mRowFontColor);
        mPaint.setTextSize(mRowFontSize);
        mPaint.setTextAlign(Paint.Align.CENTER);
//        textPaint.setColor(mRowFontColor);
//        textPaint.setTextSize(mRowFontSize);
//        textPaint.setTextAlign(Paint.Align.CENTER);
        while (rowY>= -mRowFontSize && mRowNum>=0){ //超出界面 或 已经是第一行歌词了 就不用再显示了
            String txt = list.get(mRowNum).content;
//            layout = new StaticLayout(txt,textPaint,width-densityutil.dp2px(20)
//                    ,Layout.Alignment.ALIGN_CENTER,1f,1f,true);
//            canvas.save();
//            canvas.translate(rowX,rowY);
//            layout.draw(canvas);
//            canvas.restore();

            canvas.drawText(txt,rowX,rowY,mPaint);
            rowY = rowY-mRowPadding-mRowFontSize;
            mRowNum--;
        }
        //显示高亮部分下方的歌词
        mRowNum = mHighLightRow+1;
        rowY = highLightRowY + mRowPadding + mRowFontSize;
        while(rowY<=height+mRowFontSize && mRowNum<list.size()){
            String txt = list.get(mRowNum).content;
//            layout = new StaticLayout(txt,textPaint,width-densityutil.dp2px(20)
//                    ,Layout.Alignment.ALIGN_CENTER,1f,1f,true);
//            canvas.save();
//            canvas.translate(rowX,rowY);
//            layout.draw(canvas);
//            canvas.restore();
            canvas.drawText(txt,rowX,rowY,mPaint);
            mRowNum++;
            rowY = rowY+mRowPadding+mRowFontSize;
        }


    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()){
//            case MotionEvent.ACTION_DOWN:
//                downY = event.getRawY();
//                downx = event.getRawX();
//                downtime = event.getDownTime();
//                break;
//            case MotionEvent.ACTION_MOVE:
//                float absy = Math.abs(downY-event.getRawY());
//                float absx = Math.abs(downx-event.getRawX());
//                if (absx>10 || absy>10)
//                    presstime = event.getEventTime()-downtime;
//                if (presstime>500) {
//                    mViewMode = SEEK_MODE;
//                    if (mHighLightRow > 0 || mHighLightRow < list.size() - 1)
//                        highLightRowY += (event.getRawY() - downY);
//                    if (highLightRowY <= getHeight() / 2 - mRowPadding - mHighLightFontSize) {
//                        mHighLightRow += 1;
//                    } else if (highLightRowY >= getHeight() / 2 + mRowPadding + mHighLightFontSize) {
//                        mHighLightRow -= 1;
//                    }
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//                mViewMode = NORMAL_MODE;
//                break;
//        }
//        if (presstime>500)
//            return true;
//        if (presstime!=0)
//            return false;
//        return true;
//    }
    public void setPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
    }

    public void setPos(int pos){
        if (list!=null && list.size()>0){
            for (int i=0;i<list.size();i++){
                if (i<list.size()-1) {
                    int time = list.get(i+1).time;
                    if (time>pos) {
                        if (mHighLightRow!=i){
                            delay=-1;
                            highLightRowY = height/2+mRowFontSize;
                        }
                        mHighLightRow = i;
                        postInvalidate();
                        return;
                    }
                }else{
                    if (mHighLightRow!=i-1) {
                        //delay = -1;
                        //highLightRowY = height/2+mRowFontSize;
                    }
                    mHighLightRow = list.size()-1;
                    postInvalidate();
                    return;
                }
            }
        }
    }

}
