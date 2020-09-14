package com.pds.sdk.use.realm.data;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-22 11:00
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class UserEntity extends RealmObject {
    @PrimaryKey
    private String id;
    private String name;
    private int age;

    @Ignore // 忽略改字段，不会被保存到Realm数据库中
    private int sessionId;
    private DogEntity dogEntity;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }
    public DogEntity getDogEntity() { return dogEntity; }
    public void setDogEntity(DogEntity dogEntity) { this.dogEntity = dogEntity; }

    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "    \"id\":" + id + ",\n" +
                "    \"name\":"+ name +",\n" +
                "    \"age\":" + age +",\n" +
                "    \"sessionId\":" + sessionId + ",\n" +
                "    \"dogEntity\":" + dogEntity.toString() + ",\n" +
                "}";
    }
}
