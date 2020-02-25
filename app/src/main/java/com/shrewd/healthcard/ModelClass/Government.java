package com.shrewd.healthcard.ModelClass;

public class Government {

    private String governmentid, userid, workplace;

    public Government(String governmentid, String userid, String workplace) {
        this.governmentid = governmentid;
        this.userid = userid;
        this.workplace = workplace;
    }

    public String getGovernmentid() {
        return governmentid;
    }

    public String getUserid() {
        return userid;
    }

    public String getWorkplace() {
        return workplace;
    }
}
