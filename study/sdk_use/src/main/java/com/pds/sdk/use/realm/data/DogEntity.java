package com.pds.sdk.use.realm.data;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-22 11:03
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class DogEntity extends RealmObject {

    @PrimaryKey // 不可以存在多个主键,使用支持索引的属性类型作为主键同时意味着为该字段建立索引。
    private String id;
    @Index // 注解 @Index 会为字段增加搜索索引。这会导致插入速度变慢，同时数据文件体积有所增加，但能加速查询
    private String name;
    private int age;
    @Required  // 告诉 Realm 强制禁止空值（null）被存储, 只有 Boolean、 Byte、 Short、 Integer、 Long、 Float、 Double、 String、 byte[] 以及 Date 可以被 @Required 修饰
    private Integer sex;

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }
    public Integer getSex() { return sex; }
    public void setSex(Integer sex) { this.sex = sex; }

    @NonNull
    @Override
    public String toString() {
        return "{\n" +
                "        \"id\":" + id+ ",\n" +
                "        \"name\":" + name + ",\n" +
                "        \"sex\":" + sex + ",\n" +
                "    }";
    }
}
