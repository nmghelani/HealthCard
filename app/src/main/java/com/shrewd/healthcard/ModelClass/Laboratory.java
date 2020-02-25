package com.shrewd.healthcard.ModelClass;

import androidx.annotation.NonNull;

public class Laboratory {

    private String address, labid, name;
    private long contactno;

    public Laboratory() {

    }

    public Laboratory(String address, long contactno, String labid, String name) {
        this.address = address;
        this.contactno = contactno;
        this.labid = labid;
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public long getContactno() {
        return contactno;
    }

    public String getLabid() {
        return labid;
    }

    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public String toString() {
        if (contactno == -1) {
            return "-- Select Laboratory --";
        } else {
            return name + ", " + address + ", " + contactno;
        }
    }
}
