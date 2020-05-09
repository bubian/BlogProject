package com.pds.util.xml.xmlcommon;

public class ParserResultDataList {


    private String uses_sdk;
    private String action;
    private String category;

    public String getUses_sdk() {
        return uses_sdk;
    }

    public void setUses_sdk(String uses_sdk) {
        this.uses_sdk = uses_sdk;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String toString(){
        return uses_sdk +  action + category;
    }
}
