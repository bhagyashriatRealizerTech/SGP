package com.realizer.schoolgenie.parent.chat.model;

/**
 * Created by Win on 11/17/2015.
 */
public class ParentQueriesTeacherNameListModel {

    private String name = "";
    private String subname = "";
    private String teacherid = "";

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    private String thumbnail = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSubname() {
        return subname;
    }

    public void setSubname(String subname) {
        this.subname = subname;
    }

    public String getTeacherid() {
        return teacherid;
    }

    public void setTeacherid(String teacherid) {
        this.teacherid = teacherid;
    }
}
