package com.pds.web.param;

public class HybridParamShowLoading extends HybridParam {
    public static final String TYPE_PROGRESS = "progress";
    public static final String TYPE_NORMAL = "normal";

    public static final String ACTION_SHOW = "show";
    public static final String ACTION_HIDE = "hide";
    public String type; // normal , progress
    public String action;//show , hide
}
