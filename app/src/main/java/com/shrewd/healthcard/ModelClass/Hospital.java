package com.shrewd.healthcard.ModelClass;

public class Hospital {

    private String address, hospitalid, name;
    private long contactno;

    public Hospital(){

    }

    public Hospital(String address, long contactno, String hospitalid, String name) {
        this.address = address;
        this.contactno = contactno;
        this.hospitalid = hospitalid;
        this.name = name;
    }

    @Override
    public String toString() {
        if (contactno == -1) {
            return "-- Select Hospital --";
        } else {
            return name + ", " + address + ", " + contactno;
        }
    }

    public String getAddress() {
        return address;
    }

    public long getContactno() {
        return contactno;
    }

    public String getHospitalid() {
        return hospitalid;
    }

    public String getName() {
        return name;
    }
}
