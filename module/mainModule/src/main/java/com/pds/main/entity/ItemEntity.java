package com.pds.main.entity;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 10:22 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class ItemEntity {

    public ItemEntity(int icon, String title) {
        this.iconId = icon;
        this.title = title;
    }

    public ItemEntity(int icon, String title,String url) {
        this.iconId = icon;
        this.title = title;
        this.url = url;
    }

    public int iconId;
    public String title;
    public String url;
}
