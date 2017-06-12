package com.realizer.schoolgenie.parent.funcenter.model;

/**
 * Created by Win on 28/03/2016.
 */
public class ParentFunCenterModel {
    String image,text;
    public String SchoolCode;
    public String Std;
    public String Div;
    public String AcademicYear;
    public  int eventid;
    public String EventDate;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String thumbnail;
    public String EventUUID;

    public String getSchoolCode() {
        return SchoolCode;
    }

    public void setSchoolCode(String schoolCode) {
        SchoolCode = schoolCode;
    }

    public String getStd() {
        return Std;
    }

    public void setStd(String std) {
        Std = std;
    }

    public String getDiv() {
        return Div;
    }

    public void setDiv(String div) {
        Div = div;
    }

    public String getAcademicYear() {
        return AcademicYear;
    }

    public void setAcademicYear(String academicYear) {
        AcademicYear = academicYear;
    }

    public String getEventDate() {
        return EventDate;
    }

    public void setEventDate(String eventDate) {
        EventDate = eventDate;
    }

    public String getEventUUID() {
        return EventUUID;
    }

    public void setEventUUID(String eventUUID) {
        EventUUID = eventUUID;
    }

    public int getEventid()
    {
        return eventid;
    }

    public void setEventid(int eventid)
    {
        this.eventid = eventid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }


}
