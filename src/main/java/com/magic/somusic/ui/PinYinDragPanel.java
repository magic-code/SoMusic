package com.magic.somusic.ui;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.magic.somusic.R;
import com.magic.somusic.utils.ConvertDensityUtil;

import org.w3c.dom.Text;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/11/13.
 */
public class PinYinDragPanel extends View {
    private Context context;
    private ConvertDensityUtil convUtil = new ConvertDensityUtil(getContext());
    /**侧边栏的文字大小*/
    private int panelTextSize = convUtil.sp2px(14);
    /**中间显示的对话框的 文字大小*/
    private int dialogTextSize = convUtil.sp2px(30);
    /**侧边栏的未选中文字颜色*/
    private int panelTextColor = Color.LTGRAY;
    /**侧边栏的 选中的文字的颜色*/
    private int panelTextSelColor = Color.WHITE;
    /**侧边栏的 选中文字的 背景颜色*/
    private int panelTextSelBgColor = Color.DKGRAY;
    /**侧边栏的 背景颜色*/
    private int panelBgColor = Color.WHITE;
    /**中间显示的对话框的 文字颜色*/
    private int dialogTextColor = Color.WHITE;
    /**中间显示的对话框的 背景颜色*/
    private int dialogBgColor = Color.DKGRAY;
    /**现行选中的 位置*/
    private int curPos = 0;
    /** 侧边栏字符*/
    private String[] chars={"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P",
            "Q","R","S","T","U","V","W","X","Y","Z","#"};
    private int height;
    private int width;

    private float perHeigh;
    private Paint paint=new Paint();
    private PopupWindow window = null;
    private OnLetterChangedListener listener = null;
    private View view;
    private TextView txt;

    public PinYinDragPanel(Context context) {
        super(context);
        init(context);
    }

    public PinYinDragPanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PinYinDragPanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public PinYinDragPanel(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }
    public void init(Context context){
        this.context = context;
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view = inflater.inflate(R.layout.pinyin_dialog_layout, null);

        view.setAlpha(0.8f);
        view.setVisibility(INVISIBLE);
        txt = new TextView(getContext());
        txt.setGravity(Gravity.CENTER);
        view.setBackgroundColor(dialogBgColor);
        txt.setTextSize(dialogTextSize);
        txt.setTextColor(dialogTextColor);
        LinearLayout linear = (LinearLayout) view;
        ViewGroup.LayoutParams tp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        linear.addView(txt,tp);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
                convUtil.sp2px(80),
                convUtil.sp2px(80),
                WindowManager.LayoutParams.TYPE_APPLICATION,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT);
        WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

        windowManager.addView(view, lp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        height = getMeasuredHeight();
        width = getMeasuredWidth();
        perHeigh = height/chars.length;
        /**画侧边栏背景*/
        paint.setColor(panelBgColor);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(0, 0, width, height, paint);
        /**画侧边栏的字符*/
        paint.setTextSize(panelTextSize);
        for(int i=0;i<chars.length;i++){
            if (curPos==i){ //画选中的字符
                paint.setColor(panelTextSelBgColor);
                canvas.drawRect(0,i*perHeigh,width, (i + 1) * perHeigh, paint);
                paint.setColor(panelTextSelColor);
                canvas.drawText(chars[i],width/2-panelTextSize/4,(i+1)*perHeigh-panelTextSize/4,paint);
            }else{
                paint.setColor(panelTextColor);
                canvas.drawText(chars[i],width/2-panelTextSize/4,(i+1)*perHeigh-panelTextSize/4,paint);
            }
        }
    }

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent event) {
//
//        return true;
//    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        if (action==MotionEvent.ACTION_UP) {
//            if (window != null) {
//                window.dismiss();
//            }
            if (view!=null)
                view.setVisibility(INVISIBLE);
        }else{
            float y = event.getY();
            int temp = (int)(y/perHeigh);
            if (temp!=curPos){
                if (listener!=null){
                    listener.onLetterChangerListener(temp,chars[temp]);
                }
                curPos = temp;
            }
            postInvalidate();
            showDialog(curPos);
        }
        return true;
    }
    public void showDialog(int curPos){

        //window = new PopupWindow(getContext());
        txt.setText(chars[curPos]);
        view.setVisibility(VISIBLE);


//        window.setContentView(view);
//        window.showAtLocation(root, Gravity.CENTER,0,0);
    }
    public interface OnLetterChangedListener{
        public void onLetterChangerListener(int pos,String letter);
    }
    public void setOnLetterChangedListener(OnLetterChangedListener listener){
        this.listener = listener;
    }
}
