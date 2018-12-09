package com.spotgrab.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Schedule implements Parcelable {

    private String schedule_StartTime;
    private String schedule_EndTime;
    private String schedule_Date;
    private String shift_Accepted;
    private String shift_Id;
    private String shift_Title;
    private String shift_Pay;
    private String shift_Location;
    private String shift_Description;
    private String offer_received;
    private String employer_Id;
    private String shift_Start_Set;
    private String shift_End_Set;

    public Schedule(String shift_StartTime, String shift_EndTime, String schedule_Date, String shift_Accepted, String shift_Id, String shift_Title, String shift_Pay, String shift_Location, String shift_Description, String offer_received, String employer_Id, String shift_Start_Set, String shift_End_Set) {
        this.schedule_StartTime = shift_StartTime;
        this.schedule_EndTime = shift_EndTime;
        this.schedule_Date = schedule_Date;
        this.shift_Accepted = shift_Accepted;
        this.shift_Id = shift_Id;
        this.shift_Title = shift_Title;
        this.shift_Pay = shift_Pay;
        this.shift_Location = shift_Location;
        this.shift_Description = shift_Description;
        this.offer_received = offer_received;
        this.employer_Id = employer_Id;
        this.shift_Start_Set = shift_Start_Set;
        this.shift_End_Set = shift_End_Set;

    }

    public Schedule() {

    }

    protected Schedule(Parcel in) {
        schedule_StartTime = in.readString();
        schedule_EndTime = in.readString();
        schedule_Date = in.readString();
        shift_Accepted = in.readString();
        shift_Id = in.readString();
        shift_Title = in.readString();
        shift_Pay = in.readString();
        shift_Location = in.readString();
        shift_Description = in.readString();
        offer_received = in.readString();
        employer_Id = in.readString();
        shift_Start_Set = in.readString();
        shift_End_Set = in.readString();
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

    public String getShift_Accepted() {
        return shift_Accepted;
    }

    public void setShift_Accepted(String shift_Accepted) {
        this.shift_Accepted = shift_Accepted;
    }

    public String getShift_Id() {
        return shift_Id;
    }

    public void setShift_Id(String shift_Id) {
        this.shift_Id = shift_Id;
    }

    public String getShift_Title() {
        return shift_Title;
    }

    public void setShift_Title(String shift_Title) {
        this.shift_Title = shift_Title;
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

    public String getOffer_received() {
        return offer_received;
    }

    public void setOffer_received(String offer_received) {
        this.offer_received = offer_received;
    }

    public String getEmployer_Id() {
        return employer_Id;
    }

    public void setEmployer_Id(String employer_Id) {
        this.employer_Id = employer_Id;
    }

    public String getShift_Start_Set() {
        return shift_Start_Set;
    }

    public void setShift_Start_Set(String shift_Start_Set) {
        this.shift_Start_Set = shift_Start_Set;
    }

    public String getShift_End_Set() {
        return shift_End_Set;
    }

    public void setShift_End_Set(String shift_End_Set) {
        this.shift_End_Set = shift_End_Set;
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
        dest.writeString(shift_Accepted);
        dest.writeString(shift_Id);
        dest.writeString(shift_Title);
        dest.writeString(shift_Pay);
        dest.writeString(shift_Location);
        dest.writeString(shift_Description);
        dest.writeString(offer_received);
        dest.writeString(employer_Id);
        dest.writeString(shift_Start_Set);
        dest.writeString(shift_End_Set);
    }

}
