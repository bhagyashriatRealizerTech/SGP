package com.realizer.schoolgenie.parent.chat.model;

/**
 * Created by Bhagyashri on 9/1/2016.
 */
public class AddedContactModel {

    String userName;
    String userId;
    String profileimage;
    String rollNo;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProfileimage() {
        return profileimage;
    }

    public void setProfileimage(String profileimage) {
        this.profileimage = profileimage;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AddedContactModel)) {
            return false;
        }

        AddedContactModel that = (AddedContactModel) other;

        // Custom equality check here.
        return this.userId.equals(that.userId);
    }
}
