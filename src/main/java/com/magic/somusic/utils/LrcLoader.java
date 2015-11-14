package com.magic.somusic.utils;

import android.os.Environment;

import com.magic.somusic.domain.LrcRow;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Administrator on 2015/10/14.
 */
public class LrcLoader {
    public ArrayList<LrcRow> loadLrc(String filename){
        ArrayList<LrcRow> rows = updateContent(parserLrc(getFileContent(filename)));
        if (rows!=null){
            Collections.sort(rows,new LrcRowCompator());
            return rows;
        }
        return null;
    }

    class LrcRowCompator implements Comparator<LrcRow>{

        @Override
        public int compare(LrcRow lhs, LrcRow rhs) {
            return lhs.time-rhs.time;
        }
    }

    public ArrayList<LrcRow> updateContent(ArrayList<LrcRow> olist){    /*从后向前遍历*/
        if (olist.size()<=0)
            return null;
        ArrayList<LrcRow> nlist = new ArrayList<LrcRow>();
//        for (int i=1;i<olist.size();i++){
//            LrcRow crow = olist.get(i);
//            LrcRow prow = olist.get(i-1);
//            if (crow.time<prow.time){
//                prow.content = crow.content;
//            }
//            nlist.add(prow);
//        }
        nlist.add(olist.get(olist.size()-1));
        for (int i=olist.size()-1;i>0;i--){
            LrcRow crow = olist.get(i);
            LrcRow prow = olist.get(i-1);
            if (crow.time<prow.time){
                prow.content = crow.content;
            }
            nlist.add(prow);
        }
        Collections.reverse(nlist);
        return nlist;
    }

    public ArrayList<LrcRow> parserLrc(String str){
        ArrayList<LrcRow> list = new ArrayList<LrcRow>();
        if (str==null || str.trim().equals("") )
            return list;
        StringReader sr = new StringReader(str);
        BufferedReader br = new BufferedReader(sr);
        String tmp = null;
        try {
            while ((tmp=br.readLine())!=null){
                int tag = tmp.charAt(1);
                int lastTag = tmp.indexOf("]");
                if (tag>=48 && tag<=57 && lastTag==9) {

                    LrcRow row = new LrcRow();
                    String txt = tmp.substring(lastTag + 1, tmp.length());
//                    int togle= 10;
//                    int line = (int)txt.length()/togle;
//                    StringBuilder tempsb = new StringBuilder();
//                    for (int i=0;i<line;i++){
//                        tempsb.append(txt.substring(i*togle,(i+1)*togle)).append("\r\n");
//                    }
//                    tempsb.append(txt.substring(line*togle,txt.length()));
//                    row.content = tempsb.toString();
                    //if (!txt.trim().equals("")) {
                        row.content = txt;
                        String stime = formatTime(tmp.substring(1, 9));
                        row.stime = stime;
                        row.time = convertTime(stime);
                        list.add(row);
                    //}
                }
            }
            br.close();
            sr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public String formatTime(String stime){
        stime = stime.replace(".",":");
        return stime;
    }
    public int convertTime(String time){
        String[] arrs = time.split(":");
        int ti= Integer.valueOf(arrs[0])*60*1000+Integer.valueOf(arrs[1])*1000+Integer.valueOf(arrs[2]);
        return ti;
    }

    public static String convertTime(int dur){
        int m = dur/1000/60;
        int s = (dur/1000)%60;
        String sm = m<10 ? "0"+m:""+m;
        String ss = s<10 ? "0"+s:""+s;
        return  sm+":"+ss;
    }

    public String getFileContent(String filename){
        String str=null;
        if (filename==null)
            return  str;
        StringBuilder sb = new StringBuilder();

            File lrcFile = new File(filename);
            BufferedReader br = null;
            if (lrcFile.exists()){
                try {
                    br = new BufferedReader(new InputStreamReader(new FileInputStream(lrcFile),"gbk"));
                    String temp=null;
                    while ((temp=br.readLine())!=null){
                        sb.append(temp).append("\r\n");
                    }
                    br.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if (br!=null){
                        try {
                            br.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                str = sb.toString();
            }

        return str;
    }
}
