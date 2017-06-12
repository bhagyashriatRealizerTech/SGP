package com.realizer.schoolgenie.parent.selectteacherdialog.model;

/**
 * Created by shree on 11/26/2015.
 */
public class TeacherMyClassStudentModel {
    private String srno;
    private String studentname;
    private String userId;
    private boolean selected;

    public String getSrnoStd() {
        return srno;
    }

    public void setSrnoStd(String srno) {
        this.srno = srno;
    }

    public String getStdName() {
        return studentname;
    }

    public void setStdName(String studentname) {
        this.studentname = studentname;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
