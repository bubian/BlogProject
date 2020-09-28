package com.pds.kotlin.study.net;

import com.pds.entity.base.BaseEntity;
import com.pds.entity.base.DataEntity;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * @author hmy
 * @time 2020/9/15 17:50
 */
public interface AppApi {

    /**
     * 获取登陆验证码
     *
     * @param noticeWay 通知方式, 默认为"text", "text":普通短信通知， "voice":语音通知
     * @param useType   验证码用途，register:注册, change_phone:手机号换绑, bind_phone:手机号绑定, login:登陆,
     *                  update_password:修改密码
     */
    @GET("/v1/auth/sms-codes")
    Observable<BaseEntity<DataEntity>> getLoginSmsCode(@Query("noticeWay") String noticeWay,
                                                       @Query("phone") String phone, @Query("useType") String useType);
}
