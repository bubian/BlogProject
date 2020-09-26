package com.pds.web.param;


public class HybridParamShare extends HybridParamCallback {
    public String title;
    public String desc;
    public String image;
    public String topic;
    public String link = "";
    public String[] types;//types的可选值为wechat-微信好友，timeline-朋友圈，qq-qq好友，sina-新浪微博，medlinker-医联，UI展示顺序与参数顺序一致，若不传此参数，则默认填充全部type
    // dataType为分享的数据类型，可选值有：news-资讯，casem-病例，post-帖子，video-视频，live-直播，H5-H5网页，userHome-用户主页，userCard-用户名片，chuzhenOrder-出诊订单，wenzhenAnswer-问诊解答意见，hlMsg-加精消息， groupChat-群聊； tpl-活动模板
    // 当不传types或types包括medlinker时，必传
    public String dataType;
    public String innerlink;//内部分享跳转链接
    public String groupName;
    public String extra;

    public HybridParamShare(int id, String title, String desc, String image, String topic, String link, String[] type) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.image = image;
        this.topic = topic;
        this.link = link;
        this.types = type;
    }
}
