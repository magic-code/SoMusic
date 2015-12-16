package com.magic.somusic.domain;

/**
 * Created by Administrator on 2015/9/28.
 */
public class MusicItem {
    private int _id;
    private String filename;
    private String title;
    private long duration;
    private String artist;
    private String album;
    private int collect;
    private String lrcpath;
    private String imagepath;
    private String letterTitle;

    public String getLetterArtist() {
        return letterArtist;
    }

    public void setLetterArtist(String letterArtist) {
        this.letterArtist = letterArtist;
    }

    public String getLetterTitle() {
        return letterTitle;
    }

    public void setLetterTitle(String letterTitle) {
        this.letterTitle = letterTitle;
    }

    private String letterArtist;
    public String getLrcpath() {
        return lrcpath;
    }

    public void setLrcpath(String lrcpath) {
        this.lrcpath = lrcpath;
    }

    public String getImagepath() {
        return imagepath;
    }

    public void setImagepath(String imagepath) {
        this.imagepath = imagepath;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public int getCollect() {
        return collect;
    }

    public void setCollect(int collect) {
        this.collect = collect;
    }


}
