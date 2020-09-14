package com.pds.sdk.use.realm.data;

import io.realm.RealmModel;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

/**
 * @author: pengdaosong
 * CreateTime:  2020-09-10 13:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */

@RealmClass
public class CatEntity implements RealmModel {

    @PrimaryKey
    public long id;
    public String name;
}
