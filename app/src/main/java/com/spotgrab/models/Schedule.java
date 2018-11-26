package com.spotgrab.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Schedule implements Parcelable {

    private String schedule_StartTime;
    private String schedule_EndTime;
    private String schedule_Date;

    public Schedule(String shift_StartTime, String shift_EndTime, String schedule_Date) {
        this.schedule_StartTime = shift_StartTime;
        this.schedule_EndTime = shift_EndTime;
        this.schedule_Date = schedule_Date;

    }

    public Schedule() {

    }

    protected Schedule(Parcel in) {
        schedule_StartTime = in.readString();
        schedule_EndTime = in.readString();
        schedule_Date = in.readString();
    }

    public static final Creator<Schedule> CREATOR = new Creator<Schedule>() {
        @Override
        public Schedule createFromParcel(Parcel in) {
            return new Schedule(in);
        }

        @Override
        public Schedule[] newArray(int size) {
            return new Schedule[size];
        }
    };

    public String getSchedule_StartTime() {
        return schedule_StartTime;
    }

    public void setSchedule_StartTime(String schedule_StartTime) {
        this.schedule_StartTime = schedule_StartTime;
    }

    public String getSchedule_EndTime() {
        return schedule_EndTime;
    }

    public void setSchedule_EndTime(String schedule_EndTime) {
        this.schedule_EndTime = schedule_EndTime;
    }

    public String getSchedule_Date() {
        return schedule_Date;
    }

    public void setSchedule_Date(String schedule_Date) {
        this.schedule_Date = schedule_Date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(schedule_StartTime);
        dest.writeString(schedule_EndTime);
        dest.writeString(schedule_Date);
    }

}
