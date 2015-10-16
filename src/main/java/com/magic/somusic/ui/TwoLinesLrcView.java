package com.magic.somusic.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.magic.somusic.domain.LrcRow;
import com.magic.somusic.utils.ConvertDensityUtil;

import java.util.ArrayList;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/16.
 */
public class TwoLinesLrcView extends View {

    private ConvertDensityUtil convertDensityUtil = new ConvertDensityUtil(getContext());
    /**当前渲染行在 字符串的偏移，防止本行内容过长，而后面的不见*/
    private int xoffset = 0;
    /**行与行 之间的偏移量*/
    private int mRowPadding = convertDensityUtil.dp2px(10);
    /**渲染行二行的画笔，右对齐*/
    private Paint rightRenderPaint = new Paint();
    /**渲染第一行的画笔，左对齐*/
    private Paint leftRenderPaint = new Paint();


    private enum RenderLine{FIRST_LINE,SECOND_LINE}
    /**当前正在渲染的行，只能是第一行 或第二行（只有两行）*/
    private RenderLine mCurrentRenderLine = RenderLine.FIRST_LINE;
    /**当前显示的list 中 LrcRow的行数*/
    private int mLrcRow = 0;
    /**当前要渲染的字符在 当前行内容中的位置*/
    private int mRenderCharPos = 0;
    /**当前渲染字符的 渐变百分比*/
    private float mRenderGradientPercent = 0;
    /**已经渲染过的字符的 颜色*/
    private final int hasRenderFontColor = Color.BLUE;
    /**还未渲染过的字符的 颜色*/
    private final int notRenderFontColor = Color.YELLOW;
    /**歌词的文本大小*/
    private int lrcFontSize = convertDensityUtil.sp2px(22);
    /**是否在播放*/
    private boolean isPlaying = false;
    /**歌词的内容*/
    private ArrayList<LrcRow> listRows=null;
    /**已经渲染的歌词 画笔*/
    private Paint mHasRenderPaint = new Paint();
    /**还未渲染的歌词 画笔*/
    private Paint notRenderPaint = new Paint();
    /**正在渲染的字符 画笔*/
    private Paint curRenderCharPaint = new Paint();
    /**正在渲染的字符的 渐变sharder*/
    private LinearGradient curRenderCharSharder;
    /**正在渲染字符的 渐变颜色数组*/
    private int[] mCurRenderCharColors = new int[]{hasRenderFontColor,notRenderFontColor,notRenderFontColor};

    public void setmLrcRows(ArrayList<LrcRow> list){
        this.listRows = list;
    }
    public void setIsPlaying(boolean isPlaying){
        this.isPlaying = isPlaying;
    }

    public TwoLinesLrcView(Context context) {
        super(context);
    }

    public TwoLinesLrcView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TwoLinesLrcView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TwoLinesLrcView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        leftRenderPaint.setTextAlign(Paint.Align.LEFT);
        leftRenderPaint.setAntiAlias(true);
        leftRenderPaint.setTextSize(lrcFontSize);
        leftRenderPaint.setColor(hasRenderFontColor);

        rightRenderPaint.setTextAlign(Paint.Align.RIGHT);
        rightRenderPaint.setAntiAlias(true);
        rightRenderPaint.setTextSize(lrcFontSize);
        //rightRenderPaint.setColor(notRenderFontColor);

        //curRenderCharPaint.setTextSize(lrcFontSize);
        //curRenderCharPaint.setAntiAlias(true);
        //curRenderCharPaint.setShader(curRenderCharSharder);

        if (listRows==null || listRows.size()==0){
            return;
        }
        String text = listRows.get(mLrcRow).content;
        float plen = leftRenderPaint.measureText("测",0,1);
        mRenderCharPos = mRenderCharPos>text.length() ? text.length():mRenderCharPos;
        float xlen = leftRenderPaint.measureText(text,xoffset,mRenderCharPos);
        if (xlen>getWidth()){
            xoffset+=2;
        }
        Log.e("----",getWidth()+"-----"+plen);
        int perLineCharNum = 2*(int)(getWidth()/plen);  //一行中最多能显示的字符
        perLineCharNum = perLineCharNum>text.length() ? text.length():perLineCharNum;
        curRenderCharSharder = new LinearGradient(0,0,getWidth(),0,mCurRenderCharColors,new float[]{0,mRenderGradientPercent,1}, Shader.TileMode.CLAMP);//设置渐变
        leftRenderPaint.setShader(null);
        rightRenderPaint.setShader(null);
        if (mCurrentRenderLine==RenderLine.FIRST_LINE){ //如果当前渲染行为第一行
            //画第一行渲染的字符
             leftRenderPaint.setShader(curRenderCharSharder);
             canvas.drawText(text,xoffset,xoffset+perLineCharNum,0,lrcFontSize,leftRenderPaint);

            //画第二行的字符，（如果第一行是list中的最后一行，第二行显示为已经渲染，否则渲染为 还未渲染状态）

            if (mLrcRow==listRows.size()-1) {
                rightRenderPaint.setColor(hasRenderFontColor);
                String tt = listRows.get(mLrcRow-1).content;
                perLineCharNum = perLineCharNum>tt.length() ? tt.length():perLineCharNum;
                canvas.drawText(tt,0,perLineCharNum,getWidth(),2*lrcFontSize+mRowPadding,rightRenderPaint);
            }else{
                rightRenderPaint.setColor(notRenderFontColor);
                String tt = listRows.get(mLrcRow+1).content;
                perLineCharNum = perLineCharNum>tt.length() ? tt.length():perLineCharNum;
                canvas.drawText(tt,0,perLineCharNum,getWidth(),2*lrcFontSize+mRowPadding,rightRenderPaint);
            }
        }else if (mCurrentRenderLine==RenderLine.SECOND_LINE){  //如果当前渲染行为第二行
            //画第二行渲染的字符
            rightRenderPaint.setShader(curRenderCharSharder);
            canvas.drawText(text,xoffset,xoffset+perLineCharNum,getWidth(),2*lrcFontSize+mRowPadding,rightRenderPaint);

            //画第一行的字符（如果第二行是list中最后一行，则第一行渲染为 已经渲染状态，否则渲染为 还未渲染状态）
            if (mLrcRow==listRows.size()-1) {
                leftRenderPaint.setColor(hasRenderFontColor);
                canvas.drawText(listRows.get(mLrcRow-1).content,0,lrcFontSize,leftRenderPaint);
            }else{
                leftRenderPaint.setColor(notRenderFontColor);
                canvas.drawText(listRows.get(mLrcRow+1).content,0,lrcFontSize,leftRenderPaint);
            }
        }
    }
    public void setPos(int pos){
        if (listRows!=null && listRows.size()>0){
            for (int i=0;i<listRows.size();i++){
                if (i!=listRows.size()-1) {
                    int time = listRows.get(i+1).time;
                    LrcRow curRow = listRows.get(i);
                    if (time>pos) {
                        if(mLrcRow!=i){
                            xoffset=0;
                            mCurrentRenderLine = i%2==0 ? RenderLine.FIRST_LINE:RenderLine.SECOND_LINE;
                        }
                        mLrcRow= i;
                        float temp = (float)(pos-curRow.time)*(float)curRow.content.length()/(float)(time-curRow.time);
                        mRenderCharPos = (int)(temp+0.5f);
                        mRenderGradientPercent = temp/10f;
                        postInvalidate();
                        return;
                    }
                }else if (i==listRows.size()-1){
                    if(mLrcRow!=i){
                        xoffset=0;
                        mCurrentRenderLine = i%2==0 ? RenderLine.FIRST_LINE:RenderLine.SECOND_LINE;
                    }
                    mLrcRow = listRows.size()-1;
                    postInvalidate();
                    return;
                }
            }
        }
    }
}
