package com.medrn.codepush.entity;

import org.json.JSONException;
import org.json.JSONObject;

public class PendingUpdateEntity extends MetaInfoEntity {

    private boolean isLoading;

    public PendingUpdateEntity() {

    }

    public PendingUpdateEntity(JSONObject jsonObject) {
        super(jsonObject);
        if (jsonObject != null) {
            isLoading = jsonObject.optBoolean("isLoading");
        }
    }

    public JSONObject toJSONObject() {
        JSONObject jsonObject = super.toJSONObject();
        try {
            jsonObject.put("isLoading", isLoading);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }
}
