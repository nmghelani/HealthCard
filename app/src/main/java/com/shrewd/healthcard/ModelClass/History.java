package com.shrewd.healthcard.ModelClass;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;

public class History implements Parcelable {

    private String reportsuggesion;
    private String doctorid, patientid, reportid, area, disease;
    private ArrayList<String> symptoms, vigilance, medicine;
    private Date date;

    public History() {
    }

    public History(String doctorid, String patientid, String reportid, String area, String disease,
                   ArrayList<String> medicine, ArrayList<String> symptoms, ArrayList<String> vigilance,
                   Date date, String reportsuggesion) {
        this.doctorid = doctorid;
        this.patientid = patientid;
        this.reportid = reportid;
        this.area = area;
        this.disease = disease;
        this.medicine = medicine;
        this.symptoms = symptoms;
        this.vigilance = vigilance;
        this.date = date;
        this.reportsuggesion = reportsuggesion;
    }

    protected History(Parcel in) {
        doctorid = in.readString();
        patientid = in.readString();
        reportid = in.readString();
        area = in.readString();
        disease = in.readString();
        symptoms = in.createStringArrayList();
        vigilance = in.createStringArrayList();
        medicine = in.createStringArrayList();
        date = (Date) in.readSerializable();
        reportsuggesion = in.readString();
    }

    public static final Creator<History> CREATOR = new Creator<History>() {
        @Override
        public History createFromParcel(Parcel in) {
            return new History(in);
        }

        @Override
        public History[] newArray(int size) {
            return new History[size];
        }
    };

    public String getDoctorid() {
        return doctorid;
    }

    public String getPatientid() {
        return patientid;
    }

    public String getReportid() {
        return reportid;
    }

    public String getArea() {
        return area;
    }

    public String getDisease() {
        return disease;
    }

    public ArrayList<String> getMedicine() {
        return medicine;
    }

    public ArrayList<String> getSymptoms() {
        return symptoms;
    }

    public ArrayList<String> getVigilance() {
        return vigilance;
    }

    public Date getDate() {
        return date;
    }

    public String getReportsuggesion() {
        return reportsuggesion;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(doctorid);
        dest.writeString(patientid);
        dest.writeString(reportid);
        dest.writeString(area);
        dest.writeString(disease);
        dest.writeStringList(symptoms);
        dest.writeStringList(vigilance);
        dest.writeStringList(medicine);
        dest.writeSerializable(date);
        dest.writeString(reportsuggesion);
    }
}
