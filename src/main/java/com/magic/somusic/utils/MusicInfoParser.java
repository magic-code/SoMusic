package com.magic.somusic.utils;

import com.magic.somusic.domain.NetMusicInfo;

import org.xmlpull.v1.XmlPullParser;

/**
 * Created by Administrator on 2015/10/13.
 */
public class MusicInfoParser {
    public static NetMusicInfo parserMusicInfo(String xml){
        NetMusicInfo info = new NetMusicInfo();
        int lowbegin = xml.indexOf("<url>")+5;
        int lowend = xml.indexOf("</url>");
        if (lowbegin<lowend) {
            String lowString = xml.substring(lowbegin, lowend);
            int ilenc = lowString.indexOf("</encode>");
            if(ilenc!=-1) {
                String lenc = lowString.substring(17, ilenc - 3);
                int ildec = lowString.indexOf("</decode>");
                String ldec = lowString.substring(ilenc + 17, ildec - 3);
                info.setLowQualityUrl(lenc + ldec);
            }


            int lrcs = lowString.indexOf("<lrcid>");
            int lrce = lowString.indexOf("</lrcid>");
            if (lrcs < lrce) {
                int lrcid = Integer.valueOf(lowString.substring(lrcs + 7, lrce));
                info.setLrcId(lrcid);
            }
        }

            int midbegin = xml.indexOf("<durl>") + 6;
            int midend = xml.indexOf("</durl>");
            if (midbegin < midend) {
                String midString = xml.substring(midbegin, midend);
                int imenc = midString.indexOf("</encode>");
                String menc = midString.substring(17, imenc - 3);
                int imdec = midString.indexOf("</decode>");
                String mdec = midString.substring(imenc + 17, imdec - 3);
                info.setMidQualityUrl(menc + mdec);

                int lrcs = midString.indexOf("<lrcid>");
                int lrce = midString.indexOf("</lrcid>");
                if (lrcs < lrce) {
                    int lrcid = Integer.valueOf(midString.substring(lrcs+7, lrce));
                    info.setLrcId(lrcid);
                }
            }


        int highbegin = xml.indexOf("<p2p>")+5;
        int highend = xml.indexOf("</p2p>");
        if (highbegin<highend) {
            String highString = xml.substring(highbegin, highend);
            int ihurl = highString.indexOf("<url>");
            int ihurle = highString.indexOf("</url>");
            String hurl = highString.substring(ihurl + 14, ihurle - 3);
            info.setHighQualityUrl(hurl);
        }

        return info;
    }
}
