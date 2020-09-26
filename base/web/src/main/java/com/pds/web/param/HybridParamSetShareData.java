package com.pds.web.param;


public class HybridParamSetShareData extends HybridParam {
    public String title;
    public String desc;
    public String image;
    public String topic;
    public String link = "";
    public String[] type;

    public HybridParamShare cover() {
        return new HybridParamShare(id, title, desc, image,topic, link, type);
    }
}
