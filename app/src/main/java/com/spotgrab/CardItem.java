package com.spotgrab;

public class CardItem {

    //private int mImageResource;
    private String mName;
    private String mUid, schedId;
    private float mRating;


    public CardItem(String name, String uid, float rating, String sched){
        //mImageResource = ImageResource;
        mName = name;
        mUid = uid;
        mRating = rating;
        schedId = sched;
    }

    public void setName(String newName){
        mName = newName;
    }

//    public int getImageResource(){
//        return mImageResource;
//    }

    public String getName(){
        return mName;
    }

    public void setmUid(String mUid) {
        this.mUid = mUid;
    }

    public String getmUid() {
        return mUid;
    }

    public void setmRating(float mRating) {
        this.mRating = mRating;
    }

    public float getmRating() {
        return mRating;
    }

    public String getSchedId() {
        return schedId;
    }
}
