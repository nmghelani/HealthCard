package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class User implements Parcelable {

    private String name, address, email, proof, userid;
    long type, gender, contactno;
    boolean verified;
    Date dob,regdate;

    public User() {

    }

    public User(String userid, String name, String address, String email, String proof, long type, long gender, long contactno, boolean verified, Date dob, Date regdate) {
        this.userid = userid;
        this.name = name;
        this.address = address;
        this.email = email;
        this.proof = proof;
        this.type = type;
        this.gender = gender;
        this.contactno = contactno;
        this.verified = verified;
        this.dob = dob;
        this.regdate = regdate;
    }

    protected User(Parcel in) {
        userid = in.readString();
        name = in.readString();
        address = in.readString();
        email = in.readString();
        proof = in.readString();
        type = in.readLong();
        gender = in.readLong();
        contactno = in.readLong();
        verified = in.readByte() != 0;
        dob = (Date) in.readSerializable();
        regdate = (Date) in.readSerializable();
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

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserid() {
        return userid;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public void setType(long type) {
        this.type = type;
    }

    public void setGender(long gender) {
        this.gender = gender;
    }

    public void setContactno(long contactno) {
        this.contactno = contactno;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public void setRegdate(Date regdate) {
        this.regdate = regdate;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getEmail() {
        return email;
    }

    public String getProof() {
        return proof;
    }

    public long getType() {
        return type;
    }

    public long getGender() {
        return gender;
    }

    public long getContactno() {
        return contactno;
    }

    public boolean isVerified() {
        return verified;
    }

    public Date getDob() {
        return dob;
    }

    public Date getRegdate() {
        return regdate;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(userid);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeString(email);
        dest.writeString(proof);
        dest.writeLong(type);
        dest.writeLong(gender);
        dest.writeLong(contactno);
        dest.writeByte((byte) (verified ? 1 : 0));
        dest.writeSerializable(dob);
        dest.writeSerializable(regdate);
    }
}
