package com.magic.somusic.domain;

/**
 * Created by Administrator on 2015/10/7.
 */
public class AppSetting {
    public AppSetting(int id,int first_install, int last_list, int last_music_id, int last_music_sec, int last_model, int last_music_pos) {
        this.id = id;
        this.last_list = last_list;
        this.last_music_id = last_music_id;
        this.last_music_sec = last_music_sec;
        this.last_model = last_model;
        this.last_music_pos = last_music_pos;
        this.first_install = first_install;
    }

    private int id;
    private int first_install;

    public int getFirst_install() {
        return first_install;
    }

    public void setFirst_install(int first_install) {
        this.first_install = first_install;
    }

    public int getLast_music_pos() {
        return last_music_pos;
    }

    public void setLast_music_pos(int last_music_pos) {
        this.last_music_pos = last_music_pos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLast_list() {
        return last_list;
    }

    public void setLast_list(int last_list) {
        this.last_list = last_list;
    }

    public int getLast_music_id() {
        return last_music_id;
    }

    public void setLast_music_id(int last_music_id) {
        this.last_music_id = last_music_id;
    }

    public int getLast_music_sec() {
        return last_music_sec;
    }

    public void setLast_music_sec(int last_music_sec) {
        this.last_music_sec = last_music_sec;
    }

    public int getLast_model() {
        return last_model;
    }

    public void setLast_model(int last_model) {
        this.last_model = last_model;
    }

    private int last_list;
    private int last_music_id;
    private int last_music_sec;
    private int last_model;
    private int last_music_pos;
}
