package com.realizer.schoolgenie.parent.chat.model;

/**
 * Created by Win on 11/17/2015.
 */
public class TeacherQueriesTeacherNameListModel {


    private String name = "";
    private String subname = "";
    private boolean selected;

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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
