package com.arirus.fragmenttest;

import java.util.Date;
import java.util.UUID;

/**
 * Created by whd910421 on 16/7/21.
 */
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
//        mId = UUID.randomUUID();
//        mDate = new Date();
//
//        mTitle = "";
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
