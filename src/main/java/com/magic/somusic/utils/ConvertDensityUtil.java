package com.magic.somusic.utils;

import android.content.Context;

/**
 * @author NotFound404
 *         Created by Administrator on 2015/10/15.
 */
public class ConvertDensityUtil {
    private Context context;
    public ConvertDensityUtil(Context context){
        this.context = context;
    }
    public int sp2px(int sp){
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        int px= (int) (sp*density+0.5f);
        return px;
    }
    public int dp2px(int dp){
        float density = context.getResources().getDisplayMetrics().scaledDensity;
        int px= (int) (dp*density+0.5f);
        return px;
    }
}
