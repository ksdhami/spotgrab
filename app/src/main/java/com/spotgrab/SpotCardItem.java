package com.spotgrab;

public class SpotCardItem {
    //private int mImageResource;
    private String mName;
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
    private String shift_Id;
    private String doc_Id;


    public SpotCardItem(String jobTitle, String startTime, String endTime, String date,
                        String pay, String location, String description, String filled, String offerSent,
                        String employerUid, String shiftId, String docId){
        //mImageResource = ImageResource;
        //mName = name;
        shift_JobTitle = jobTitle;
        shift_StartTime = startTime;
        shift_EndTime = endTime;
        shift_Date = date;
        shift_Pay = pay;
        shift_Location = location;
        shift_Description = description;
        shift_Filled = filled;
        shift_Offer_Sent = offerSent;
        shift_Spotter_Uid = employerUid;
        shift_Id = shiftId;
        doc_Id = docId;
    }

    public void changeName(String newName){
        mName = newName;
    }


//    public int getImageResource(){
//        return mImageResource;
//    }

    public String getName(){
        return mName;
    }

    public String getShift_JobTitle() { return shift_JobTitle; }

    public String getShift_StartTime() {
        return shift_StartTime;
    }

    public String getShift_EndTime() {
        return shift_EndTime;
    }

    public String getShift_Date() {
        return shift_Date;
    }

    public String getShift_Pay() {
        return shift_Pay;
    }

    public String getShift_Location() {
        return shift_Location;
    }

    public String getShift_Description() {
        return shift_Description;
    }

    public String getShift_Filled() {
        return shift_Filled;
    }

    public String getShift_Offer_Sent() {
        return shift_Offer_Sent;
    }

    public String getShift_Spotter_Uid() {
        return shift_Spotter_Uid;
    }

    public String getShift_Id() {
        return shift_Id;
    }

    public String getDoc_Id() {
        return doc_Id;
    }
}
