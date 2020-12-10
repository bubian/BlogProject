package com.pds.entity.common;

import com.pds.entity.base.DataEntity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 10:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ItemEntity extends DataEntity {

    public ItemEntity(int icon, String title) {
        this.iconId = icon;
        this.title = title;
    }

    public ItemEntity(int icon, String title, String url) {
        this.iconId = icon;
        this.title = title;
        this.url = url;
    }

    public ItemEntity(int icon, String title, String url, String fileUrl) {
        this.iconId = icon;
        this.title = title;
        this.url = url;
        this.fileUrl = fileUrl;
    }

    public int iconId;
    public String title;
    public String url;
    public String fileUrl;
    public String extra;

    public static ItemEntity buildItemEntity(int icon, String title, String router) {
        ItemEntity entity = new ItemEntity(icon, title, router);
        return entity;
    }

    public static ItemEntity buildItemEntity(int icon, String title) {
        ItemEntity entity = new ItemEntity(icon, title);
        return entity;
    }

    public static ItemEntity buildItemEntity(int icon, String title, String router,String extra) {
        ItemEntity entity = new ItemEntity(icon, title, router,extra);
        entity.extra = extra;
        return entity;
    }
}
