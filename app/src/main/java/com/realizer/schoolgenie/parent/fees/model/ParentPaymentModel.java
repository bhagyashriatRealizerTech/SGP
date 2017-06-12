package com.realizer.schoolgenie.parent.fees.model;

/**
 * Created by shree on 4/5/2016.
 */
public class ParentPaymentModel {
    public String semester="";
    public String fees="";
    public String status="";
    public String duedate="";

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getSemester() {
        return semester;
    }

    public void setFees(String fees) {
        this.fees = fees;
    }

    public String getFees() {
        return fees;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setDuedate(String duedate) {
        this.duedate = duedate;
    }

    public String getDuedate() {
        return duedate;
    }
}
