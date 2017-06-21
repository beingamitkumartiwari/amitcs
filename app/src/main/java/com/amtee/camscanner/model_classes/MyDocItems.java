package com.amtee.camscanner.model_classes;

import java.util.ArrayList;

/**
 * Created by DEVEN SINGH on 2/9/2015.
 */
public class MyDocItems {
    private int myDocUniqueID;
    private String myDocName = "";
    private String myDocImgPathName = "";
    private String myDocTime = "";
    private String myDocTags = "";
    private String myDocPages = "";
    public boolean isSeleted = false;

    public ArrayList<MyDocItems> listOfMyDoc = new ArrayList<MyDocItems>();

    public boolean isSeleted() {
        return isSeleted;
    }

    public void setSeleted(boolean isSeleted) {
        this.isSeleted = isSeleted;
    }

    public int getMyDocUniqueID() {
        return myDocUniqueID;
    }

    public void setMyDocUniqueID(int myDocUniqueID) {
        this.myDocUniqueID = myDocUniqueID;
    }

    public String getMyDocName() {
        return myDocName;
    }

    public void setMyDocName(String myDocName) {
        this.myDocName = myDocName;
    }

    public String getMyDocImgPathName() {
        return myDocImgPathName;
    }

    public void setMyDocImgPathName(String myDocImgPathName) {
        this.myDocImgPathName = myDocImgPathName;
    }

    public String getMyDocTime() {
        return myDocTime;
    }

    public void setMyDocTime(String myDocTime) {
        this.myDocTime = myDocTime;
    }

    public String getMyDocTags() {
        return myDocTags;
    }

    public void setMyDocTags(String myDocTags) {
        this.myDocTags = myDocTags;
    }

    public String getMyDocPages() {
        return myDocPages;
    }

    public void setMyDocPages(String myDocPages) {
        this.myDocPages = myDocPages;
    }
}
