package com.magic.somusic.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.magic.somusic.R;
import com.magic.somusic.domain.LrcRow;
import com.magic.somusic.ui.TwoLinesLrcView;

import java.util.ArrayList;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/27.
 */
public class LrcDialogUtil {
    private LrcLoader lrcLoader = new LrcLoader();
    private boolean isShowAlertLrc = false;
    private String lrcPath;
    private WindowManager manager;
    private View lrcView;
    private TwoLinesLrcView lrctwo;
    private float downy;
    private WindowManager.LayoutParams params;
    private OnPostionUpdate positionListener;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            lrctwo.setPos(msg.arg1);
        }
    };


    public void showLrcDialog(Context mContext){
        if (!isShowAlertLrc) {
            Context context = mContext.getApplicationContext();
            manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            lrcView = LayoutInflater.from(context).inflate(R.layout.lrc_alert,null);
            params = new WindowManager.LayoutParams();
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
            params.format = PixelFormat.TRANSLUCENT;

            params.width = WindowManager.LayoutParams.MATCH_PARENT;
            params.height = WindowManager.LayoutParams.WRAP_CONTENT;
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x=0;
            params.y=0;
            lrcView.setOnTouchListener(new View.OnTouchListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int action = event.getAction();
                    params.y=(int)event.getRawY()-lrcView.getMeasuredHeight()/2-25;
                    manager.updateViewLayout(lrcView,params);
//                    switch(action){
//                        case MotionEvent.ACTION_DOWN:
//                            downy = event.getRawY();
//                            break;
//                        case MotionEvent.ACTION_MOVE:
//                            Log.e("LrcDialogUtil",lrcView.getY()+"||"+event.getRawY()+"||"+downy);
//                            //lrcView.setTranslationY(lrcView.getY()+(event.getRawY()-downy));
//                            params.y=(int)(event.getRawY()-downy);
//                            manager.updateViewLayout(lrcView,params);
//                            break;
//                        case MotionEvent.ACTION_UP:
//                            params.y=(int)(event.getRawY()-downy);
//                            manager.updateViewLayout(lrcView,params);
//                            break;
//                    }
                    return false;
                }
            });
            isShowAlertLrc = true;



            lrctwo = (TwoLinesLrcView) lrcView.findViewById(R.id.lrc_alert_twolrc);
            lrctwo.setmLrcRows(lrcLoader.loadLrc(lrcPath));
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (isShowAlertLrc) {
                        if (lrcPath!=null) {
//                            lrctwo.setPos(updatePos());
                            if (positionListener!=null){
                                //lrctwo.setPos(positionListener.onPostionUpdate());
                               // Message mes  = handler.obtainMessage(1,positionListener.onPostionUpdate(),0);
                                //handler.sendMessage(mes);
                            }
                        }
                        try {
                            Thread.sleep(20);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
            manager.addView(lrcView, params);

        }
    }
    public void hideLrcDialog(Context mContext){
        if (isShowAlertLrc){
            Context context = mContext.getApplicationContext();
            manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            manager.removeView(lrcView);
            isShowAlertLrc = false;
        }
    }
    public void setLrcList(String lrcPath){
        this.lrcPath = lrcPath;
        lrctwo.setmLrcRows(lrcLoader.loadLrc(lrcPath));
    }
    public interface OnPostionUpdate{
        public int onPostionUpdate();
    }
    public void setOnPostionListener(OnPostionUpdate postionListener){
        this.positionListener = postionListener;
    }
}
