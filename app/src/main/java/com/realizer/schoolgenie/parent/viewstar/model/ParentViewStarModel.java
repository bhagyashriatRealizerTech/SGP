package com.realizer.schoolgenie.parent.viewstar.model;

/**
 * Created by shree on 11/21/2015.
 */
public class ParentViewStarModel {
    private String subjectName;
    private String teachername;
    private String comment;
    private String date;
    private String givenStar;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    private String thumbnail="";

    public void setStudSubject(String subjectName){this.subjectName =subjectName;}
    public String getStudSubject(){return subjectName; }

    public void setteachername(String teachername){this.teachername =teachername;}
    public String getteachername(){return teachername; }

    public void setcomment(String comment){this.comment=comment;}
    public String getcomment(){return comment;}

    public void setgivenStar(String givenStar){this.givenStar = givenStar;}
    public String getgivenStar(){return givenStar;}

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
