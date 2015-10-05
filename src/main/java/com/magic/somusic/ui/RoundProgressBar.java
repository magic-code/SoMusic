package com.magic.somusic.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.magic.somusic.R;

/**
 * Created by Administrator on 2015/10/5.
 */
public class RoundProgressBar extends View {
    /**
            * 画笔对象的引用
    */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    private static final int STROKE = 0;
    private static final int FILL = 1;

    public RoundProgressBar(Context context) {
        super(context);
        paint = new Paint();
    }

    public RoundProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }
    public RoundProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public void init(Context context,AttributeSet attrs){
        paint = new Paint();
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);

        roundColor = typedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.WHITE);
        roundProgressColor = typedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor,Color.BLUE);
        textColor = typedArray.getColor(R.styleable.RoundProgressBar_textColor,Color.GREEN);
        textSize = typedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15);
        roundWidth = typedArray.getDimension(R.styleable.RoundProgressBar_textSize,5);
        max = typedArray.getInteger(R.styleable.RoundProgressBar_max,100);
        textIsDisplayable = typedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplay,true);
        style = typedArray.getInt(R.styleable.RoundProgressBar_style,0);

        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //画最外层大圆环


        int center = getWidth()/2;  //圆心X坐标
        int radius = (int)(center-roundWidth/2);    //圆环半径
        paint.setColor(roundColor);//设置圆环颜色
        paint.setStyle(Paint.Style.STROKE);//设置空心
        paint.setStrokeWidth(roundWidth);   //设置圆环的宽度
        paint.setAntiAlias(true);
        canvas.drawCircle(center,center,radius,paint);

        /**画进度百分比*/
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        int percent = (int)((float)progress/(float)max*100);
        float textWidth = paint.measureText(percent+"%");
        if (textIsDisplayable && percent!=0 && style==STROKE){
            canvas.drawText(percent+"%",center-textWidth/2,center+textSize/2,paint);
        }
        /** 画进度
         * 设置进度是实心还是空心*/
        paint.setStrokeWidth(roundWidth);
        paint.setColor(roundProgressColor);
        RectF oval = new RectF(center-radius,center-radius,center+radius,center+radius);
        switch (style){
            case STROKE:
                paint.setStyle(Paint.Style.STROKE);
                if (progress!=0)
                    canvas.drawArc(oval,-90,360*progress/max,false,paint);
                break;
            case FILL:
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress!=0){
                    canvas.drawArc(oval,-90,360*progress/max,true,paint);
                }
                break;
        }
    }

    public int getRoundColor() {
        return roundColor;
    }

    public void setRoundColor(int roundColor) {
        this.roundColor = roundColor;
    }

    public int getRoundProgressColor() {
        return roundProgressColor;
    }

    public void setRoundProgressColor(int roundProgressColor) {
        this.roundProgressColor = roundProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

    public synchronized int getMax() {
        return max;
    }

    public synchronized void setMax(int max) {
        if (max<0)
            throw new IllegalArgumentException("max not less than 0(zero)");
        this.max = max;
    }

    public synchronized int getProgress() {
        return progress;
    }

    public synchronized void setProgress(int progress) {
        if (progress<0)
            throw new IllegalArgumentException("the progress not less than 0(zero)");
        if (progress>max){
            progress = max;
        }else {
            this.progress = progress;
        }
        postInvalidate();//能在非UI线程进行刷新
    }

    public boolean isTextIsDisplayable() {
        return textIsDisplayable;
    }

    public void setTextIsDisplayable(boolean textIsDisplayable) {
        this.textIsDisplayable = textIsDisplayable;
    }
}
