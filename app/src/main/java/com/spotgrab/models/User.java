package com.spotgrab.models;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String account;
    private String email;
    private String password;
    private String fName;
    private String lName;
    private String phone;
    private String address;
    private String franchise;
    private String profile_image;
    private Long rating;
    private Integer numJobs;

    public User(String account, String email, String password, String fName, String lName, String phone, String address, String franchise, String profile_image, long rating, int numJobs) {
        this.account = account;
        this.email = email;
        this.password = password;
        this.fName = fName;
        this.lName = lName;
        this.phone = phone;
        this.address = address;
        this.franchise = franchise;
        this.profile_image = profile_image;
        this.rating = rating;
        this.numJobs = numJobs;
    }

    public User() {

    }

    protected User(Parcel in) {
        email = in.readString();
        password = in.readString();
        account = in.readString();
        fName = in.readString();
        lName = in.readString();
        phone = in.readString();
        address = in.readString();
        franchise = in.readString();
        profile_image = in.readString();
        rating = in.readLong();
        numJobs = in.readInt();
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };


    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getfName() {
        return fName;
    }

    public String getlName() {
        return lName;
    }

    public void setlName(String lName) {
        this.lName = lName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFranchise() {
        return franchise;
    }

    public void setFranchise(String franchise) {
        this.franchise = franchise;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }

    public Long getRating() { return rating; }

    public void setRating(Long rating) {
        this.rating = rating;
    }

    public Integer getNumJobs() {
        return numJobs;
    }

    public void setNumJobs(Integer numJobs) {
        this.numJobs = numJobs;
    }

    @Override
    public String toString() {
        return "User{" +
                ", account='" + account + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", fName='" + fName + '\'' +
                ", lName='" + lName + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", franchise='" + franchise + '\'' +
                ", profile_image='" + profile_image + '\'' +
                ", rating='" + rating + '\'' +
                ", numJobs='" + numJobs + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(email);
        dest.writeString(password);
        dest.writeString(account);
        dest.writeString(fName);
        dest.writeString(lName);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(franchise);
        dest.writeString(profile_image);
        dest.writeLong(rating);
        dest.writeInt(numJobs);
    }
}
