package com.shrewd.healthcard.ModelClass;

public class LabAssistant {

    private String labassistantid, labid, userid;

    public LabAssistant(String labassistantid, String labid, String userid) {
        this.labassistantid = labassistantid;
        this.labid = labid;
        this.userid = userid;
    }

    public String getLabassistantid() {
        return labassistantid;
    }

    public String getLabid() {
        return labid;
    }

    public String getUserid() {
        return userid;
    }
}
