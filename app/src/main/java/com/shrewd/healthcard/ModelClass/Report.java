package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class Report implements Parcelable {

    private String reportid, labid, type, patientid;
    private ArrayList<String> image;
    private Date date;

    public Report() {

    }

    public Report(String reportid, String labid, String type, ArrayList<String> image, Date date, String patientid) {
        this.reportid = reportid;
        this.labid = labid;
        this.type = type;
        this.image = image;
        this.date = date;
        this.patientid = patientid;
    }

    protected Report(Parcel in) {
        reportid = in.readString();
        labid = in.readString();
        type = in.readString();
        patientid = in.readString();
        image = in.createStringArrayList();
        date = (Date) in.readSerializable();
    }

    public static final Creator<Report> CREATOR = new Creator<Report>() {
        @Override
        public Report createFromParcel(Parcel in) {
            return new Report(in);
        }

        @Override
        public Report[] newArray(int size) {
            return new Report[size];
        }
    };

    public String getPatientid() {
        return patientid;
    }

    public String getReportid() {
        return reportid;
    }

    public String getLabid() {
        return labid;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getImage() {
        return image;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reportid);
        dest.writeString(labid);
        dest.writeString(type);
        dest.writeString(patientid);
        dest.writeStringList(image);
        dest.writeSerializable(date);
    }
}
