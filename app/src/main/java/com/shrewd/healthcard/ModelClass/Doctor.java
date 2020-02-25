package com.shrewd.healthcard.ModelClass;

public class Doctor {

    private String licenseno;
    private String doctorid, doctortype, hospitalid, userid;

    public Doctor(){

    }

    public Doctor(String doctorid, String doctortype, String hospitalid, String userid, String licenseno) {
        this.doctorid = doctorid;
        this.doctortype = doctortype;
        this.hospitalid = hospitalid;
        this.userid = userid;
        this.licenseno = licenseno;
    }

    public String getDoctorid() {
        return doctorid;
    }

    public String getDoctortype() {
        return doctortype;
    }

    public String getHospitalid() {
        return hospitalid;
    }

    public String getUserid() {
        return userid;
    }

    public String getLicenseno() {
        return licenseno;
    }
}
