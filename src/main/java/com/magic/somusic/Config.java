package com.magic.somusic;

/**
 * Created by Administrator on 2015/10/2.
 */
public class Config {

    public static final int DOWNLOAD_LRC_SUC =0x11 ;
    public static final int DOWNLOAD_LRC_ERR =0x12 ;

    /**播放状态*/
    //public  int PLAY_STATE;
    public class PlayState{
        /**正在播放*/
        public static final int STATE_PLAYING = 1;
        /**播放停止*/
        public static final int STATE_STOP = 2;
        /**暂停播放*/
        public static final int STATE_PAUSE = 3;
    }
    /**播放模式*/

    public class PlayModel{
        /**单曲循环*/
        public static final int SINGLE_REPEAT = 1;
        /**列表循环*/
        public static final int LIST_REPEAT_MODEL = 2;
        /**顺序播放*/
        public static final int SEQUENCE_MODEL = 3;
        /**随机播放*/
        public static final int RANDOM_MODEL = 4;
        /**单曲播放*/
        public static final int SINGLE_MODEL = 5;
    }

    /**播放列表*/

    public class PlayList{
        /**本地列表*/
        public static final int LIST_LOCAL = 1;
        /**收藏列表*/
        public static final int LIST_COLLECTION = 2;
        /**网络列表*/
        public static final int LIST_NETWORK = 3;
    }
    /**收藏状态*/
    public class CollecteState{
        /**已经收藏*/
        public static final int STATE_COLLECTED = 1;
        /**还未收藏*/
        public static final int STATE_NOT_COLLECTED = 0;
    }

    /**广播消息*/
    public class Broadcast{
        /**扫描成功*/
        public static final String SCAN_SUCCESS = "com.magic.somusic.scan_succ";
        /**扫描失败*/
        public static final String SCAN_FAIL = "com.magic.somusic.scan_fail";
        public static final String SCAN_COUNT = "scan_count";

        /**播放歌曲改变广播*/
        public static final String MUSIC_CHANGE = "com.magic.somusic.music_change";

        /**播放广播（notification）*/
        public static final String MUSIC_PLAY = "com.magic.somusic.music_play";
        /**暂停广播（notification）*/
        public static final String MUSIC_PAUSE = "com.magic.somusic.music_pause";
        /**下一首广播（notification）*/
        public static final String MUSIC_NEXT = "com.magic.somusic.music_next";
        /**上一首广播（notification）*/
        public static final String MUSIC_PREVIOUS = "com.magic.somusic.music_previous";
        /**退出广播（notification）*/
        public static final String APP_EXIT = "com.magic.somusic.exit";
        public static final String LRC_ALERT_TOGGLE = "com.magic.somusic.lrc_alert";
    }
    /**请求响应码*/
    public class Code{
        public static final int REQ_MUSIC_PLAY = 1;
        public static final int REQ_MUSIC_PAUSE = 2;
        public static final int REQ_MUSIC_NEXT = 3;
        public static final int REQ_MUSIC_PREVIOUS = 4;
        public static final int REQ_MUSIC_EXIT = 0;
        public static final int REQ_TO_ACTIVITY = 5;
        public static final int REQ_MUSIC_LRC = 6;
    }
    public static final int DOWNLOAD_INFO_SUS = 0x001;
    public static final int DOWNLOAD_INFO_ERR = 0x002;

//    public class Setting{
//        public static final String SHAREPREFENS = "somusic";
//        public static final String LAST_LIST = "lastlist";
//        public static final String LAST_MUSIC = "lastmusic";
//        public static final String LAST_MUSIC_SEC = "lastmusicsec";
//    }
}
