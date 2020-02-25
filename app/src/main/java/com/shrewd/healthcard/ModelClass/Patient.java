package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Patient implements Parcelable {

    private ArrayList<String> allergy;
    private String weight, userid;
    private String patientid;

    public Patient() {

    }

    public Patient(ArrayList<String> allergy, String weight, String userid, String patientid) {
        this.allergy = allergy;
        this.weight = weight;
        this.userid = userid;
        this.patientid = patientid;
    }

    protected Patient(Parcel in) {
        allergy = in.createStringArrayList();
        weight = in.readString();
        userid = in.readString();
        patientid = in.readString();
    }

    public static final Creator<Patient> CREATOR = new Creator<Patient>() {
        @Override
        public Patient createFromParcel(Parcel in) {
            return new Patient(in);
        }

        @Override
        public Patient[] newArray(int size) {
            return new Patient[size];
        }
    };

    public String getPatientid() {
        return patientid;
    }

    public ArrayList<String> getAllergy() {
        return allergy;
    }

    public String getWeight() {
        return weight;
    }

    public String getUserid() {
        return userid;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(allergy);
        dest.writeString(weight);
        dest.writeString(userid);
        dest.writeString(patientid);
    }

    public void setAllergy(ArrayList<String> allergy) {
        this.allergy = allergy;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }
}
