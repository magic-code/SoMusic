package com.magic.somusic.domain;

/**
 * Created by Administrator on 2015/10/13.
 */
public class NetMusicInfo {
    private String lowQualityUrl;
    private String midQualityUrl;
    private String highQualityUrl;
    private String lrcUrl;
    private int lrcId;
    private String lrcPath;

    public String getLrcPath() {
        return lrcPath;
    }

    public void setLrcPath(String lrcPath) {
        this.lrcPath = lrcPath;
    }

    public int getLrcId() {
        return lrcId;
    }

    public void setLrcId(int lrcId) {
        this.lrcId = lrcId;
        this.lrcUrl = "http://box.zhangmen.baidu.com/bdlrc/"+((int)lrcId/100)+"/"+lrcId+".lrc";
    }

    public String getLrcUrl() {
        return lrcUrl;
    }

    public void setLrcUrl(String lrcUrl) {
        this.lrcUrl = lrcUrl;
    }

    public String getHighQualityUrl() {
        return highQualityUrl;
    }

    public void setHighQualityUrl(String highQualityUrl) {
        this.highQualityUrl = highQualityUrl;
    }

    public String getMidQualityUrl() {
        return midQualityUrl;
    }

    public void setMidQualityUrl(String midQualityUrl) {
        this.midQualityUrl = midQualityUrl;
    }

    public String getLowQualityUrl() {
        return lowQualityUrl;
    }

    public void setLowQualityUrl(String lowQualityUrl) {
        this.lowQualityUrl = lowQualityUrl;
    }
}
