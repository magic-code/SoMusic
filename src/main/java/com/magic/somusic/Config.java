package com.magic.somusic;

/**
 * Created by Administrator on 2015/10/2.
 */
public class Config {

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
    public static int PLAY_MODEL;
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
    public static int PLAY_LIST;
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
    }
}
