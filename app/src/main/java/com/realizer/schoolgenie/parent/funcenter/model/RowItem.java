package com.realizer.schoolgenie.parent.funcenter.model;

import android.graphics.Bitmap;

/**
 * Created by shree on 8/29/2016.
 */
public class RowItem {
    private Bitmap bitmapImage;
    private String imageName;

    public RowItem(Bitmap bitmapImage, String image) {
        this.bitmapImage =  bitmapImage;
        this.imageName=image;
    }

    public Bitmap getBitmapImage() {
        return bitmapImage;
    }

    public void setBitmapImage(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public String getImageName() {return imageName;
    }

    public void setImageName(String imageName) {this.imageName = imageName;
    }
}
