package com.spotgrab.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Shift implements Parcelable {

    private String shift_JobTitle;
    private String shift_StartTime;
    private String shift_EndTime;
    private String shift_Date;
    private String shift_Pay;
    private String shift_Location;
    private String shift_Description;
    private String shift_Filled;
    private String shift_Offer_Sent;
    private String shift_Spotter_Uid;
    private String shift_Start_Set;
    private String shift_End_Set;

    public Shift(String shift_JobTitle, String shift_StartTime, String shift_EndTime, String shift_Date, String shift_Pay, String shift_Location, String shift_Description, String shift_Filled, String shift_Offer_Sent, String shift_Spotter_Uid, String shift_Start_Set, String shift_End_Set) {
        this.shift_JobTitle = shift_JobTitle;
        this.shift_StartTime = shift_StartTime;
        this.shift_EndTime = shift_EndTime;
        this.shift_Date = shift_Date;
        this.shift_Pay = shift_Pay;
        this.shift_Location = shift_Location;
        this.shift_Description = shift_Description;
        this.shift_Filled = shift_Filled;
        this.shift_Offer_Sent = shift_Offer_Sent;
        this.shift_Spotter_Uid = shift_Spotter_Uid;
    }

    public Shift () {

    }

    protected Shift(Parcel in) {
        shift_JobTitle = in.readString();
        shift_StartTime = in.readString();
        shift_EndTime = in.readString();
        shift_Date = in.readString();
        shift_Pay = in.readString();
        shift_Location = in.readString();
        shift_Description = in.readString();
        shift_Filled = in.readString();
        shift_Offer_Sent = in.readString();
        shift_Spotter_Uid = in.readString();
        shift_Start_Set = in.readString();
        shift_End_Set = in.readString();
    }

    public static final Creator<Shift> CREATOR = new Creator<Shift>() {
        @Override
        public Shift createFromParcel(Parcel in) {
            return new Shift(in);
        }

        @Override
        public Shift[] newArray(int size) {
            return new Shift[size];
        }
    };

    public String getShift_JobTitle() {
        return shift_JobTitle;
    }

    public void setShift_JobTitle(String shift_JobTitle) {
        this.shift_JobTitle = shift_JobTitle;
    }

    public String getShift_StartTime() {
        return shift_StartTime;
    }

    public void setShift_StartTime(String shift_StartTime) {
        this.shift_StartTime = shift_StartTime;
    }

    public String getShift_EndTime() {
        return shift_EndTime;
    }

    public void setShift_EndTime(String shift_EndTime) {
        this.shift_EndTime = shift_EndTime;
    }

    public String getShift_Date() {
        return shift_Date;
    }

    public void setShift_Date(String shift_Date) {
        this.shift_Date = shift_Date;
    }

    public String getShift_Pay() {
        return shift_Pay;
    }

    public void setShift_Pay(String shift_Pay) {
        this.shift_Pay = shift_Pay;
    }

    public String getShift_Location() {
        return shift_Location;
    }

    public void setShift_Location(String shift_Location) {
        this.shift_Location = shift_Location;
    }

    public String getShift_Description() {
        return shift_Description;
    }

    public void setShift_Description(String shift_Description) {
        this.shift_Description = shift_Description;
    }

    public String getShift_Filled() {
        return shift_Filled;
    }

    public void setShift_Filled(String shift_Filled) {
        this.shift_Filled = shift_Filled;
    }

    public String getShift_Offer_Sent() {
        return shift_Offer_Sent;
    }

    public void setShift_Offer_Sent(String shift_Offer_Sent) {
        this.shift_Offer_Sent = shift_Offer_Sent;
    }

    public String getShift_Spotter_Uid() {
        return shift_Spotter_Uid;
    }

    public void setShift_Spotter_Uid(String shift_Spotter_Uid) {
        this.shift_Spotter_Uid = shift_Spotter_Uid;
    }

    public void setShift_Start_Set(String shift_Start_Set) {
        this.shift_Start_Set = shift_Start_Set;
    }

    public String getShift_Start_Set() {
        return shift_Start_Set;
    }

    public void setShift_End_Set(String shift_End_Set) {
        this.shift_End_Set = shift_End_Set;
    }

    public String getShift_End_Set() {
        return shift_End_Set;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(shift_JobTitle);
        dest.writeString(shift_StartTime);
        dest.writeString(shift_EndTime);
        dest.writeString(shift_Date);
        dest.writeString(shift_Pay);
        dest.writeString(shift_Location);
        dest.writeString(shift_Description);
        dest.writeString(shift_Filled);
        dest.writeString(shift_Offer_Sent);
        dest.writeString(shift_Spotter_Uid);
        dest.writeString(shift_Start_Set);
        dest.writeString(shift_End_Set);
    }

}
