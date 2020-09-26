package com.pds.entity;

import com.pds.entity.base.DataEntity;

/**
 * @author: pengdaosong
 * CreateTime:  2020/9/19 1:02 PM
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class PagingEntity extends DataEntity {

    private String title;
    private String content;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
