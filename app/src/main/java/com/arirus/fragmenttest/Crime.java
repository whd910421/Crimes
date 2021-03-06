package com.arirus.fragmenttest;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whd910421 on 16/7/21.
 */
/*
* Crime的定义类,包含Crime所有接口
* */
public class Crime {
    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private String mTitle;
    private UUID mId;
    private Date mDate;
    private boolean mSolved;

    public long getContactID() {
        return mContactID;
    }

    public void setContactID(long contactID) {
        mContactID = contactID;
    }

    private long mContactID;

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }

    private String mSuspect;

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public UUID getId() {
        return mId;
    }

    public Crime()
    {
        this(UUID.randomUUID());
    }

    public Crime(UUID id)
    {
        mId = id;
        mDate = new Date();
    }

    public Crime(String title)
    {
        mTitle = title;
    }
}
