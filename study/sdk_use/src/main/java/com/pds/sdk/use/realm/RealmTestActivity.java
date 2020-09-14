package com.pds.sdk.use.realm;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.pds.sdk.use.R;
import com.pds.sdk.use.realm.data.DogEntity;
import com.pds.sdk.use.realm.data.UserEntity;

import java.util.UUID;

import io.realm.Realm;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-22 10:59
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class RealmTestActivity extends Activity {

    private static final String TAG = "RealmTestActivity";
    private Realm realm = Realm.getDefaultInstance();

    private static final String ID = "11111111111111111111";

    private TextView mText;

    private UserEntity mUserEntityCache;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_realm);
        mText = findViewById(R.id.text);
        mUserEntityCache = realm.where(UserEntity.class).equalTo("id",ID).findFirst();
    }

    public void insertData(View view) {
        // Changing Realm data can only be done from inside a transaction.
        // 不能直接改变数据库对象，需要在transaction中
        if (realm.isInTransaction()){
            // error:The Realm is already in a write transaction
            return;
        }
        realm.beginTransaction();
        UserEntity userEntity = new UserEntity();
        userEntity.setAge(12);
        userEntity.setId(ID);
        userEntity.setName("唐三");
        userEntity.setSessionId(111);
        DogEntity dogEntity = new DogEntity();
        dogEntity.setAge(2);
        dogEntity.setId("dog:"+ 111);
        dogEntity.setName("旺旺");
        // 不能为null,如果赋值null，将抛异常: Argument: This field(sex) is not nullable.
        dogEntity.setSex(1);
        userEntity.setDogEntity(dogEntity);
        // 使用insert，如果主键已经存在，这会抛异常：Value already exists: dog:111
        realm.insertOrUpdate(userEntity);
        Log.e(TAG,"realm insert start");
        // 如果不执行commitTransaction，将一只在transaction中
        realm.commitTransaction();
    }

    public void findData(View view) {
        UserEntity entity = realm.where(UserEntity.class).findFirst();
        if (null == entity){
            return;
        }
        mText.setText(entity.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != realm){ realm.close(); }
    }

    public void deleteAll(View view) {
        realm.beginTransaction();
        realm.where(UserEntity.class).findAll().deleteAllFromRealm();
        realm.commitTransaction();
    }

    private UserEntity mUserEntity;
    public void transaction(View view) {
        realm.executeTransaction(realm -> {
            mUserEntity = realm.where(UserEntity.class).equalTo("id",ID).findFirst();
            if (null != mUserEntity){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG,"realm transaction 1");
                mUserEntity.setName("虎列拉");
            }
        });

        Log.d(TAG,"realm transaction 2");
        if (null != mUserEntity){
            Log.d(TAG,"realm transaction:name = "+ mUserEntity.getName());
        }

        Log.d(TAG,"============================================");
        // 将在后端线程执行，不阻塞
        realm.executeTransactionAsync(realm -> {
            mUserEntity = realm.where(UserEntity.class).equalTo("id", ID).findFirst();
            if (null != mUserEntity) {
                try {
                    Thread.sleep(1850);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "realm transaction 4:name = " + mUserEntity.getName());
                mUserEntity.setName("小舞");

                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "realm transaction 8");
            }
        }, () -> Log.d(TAG,"realm transaction succeed:name = "+ mUserEntityCache.getName()));

        Log.d(TAG,"realm transaction 3");
        if (null != mUserEntity){
            Log.d(TAG,"realm transaction:name = "+ mUserEntity.getName());
        }

        Log.d(TAG,"============================================");

        realm.executeTransactionAsync(realm -> {
            Log.d(TAG, "realm transaction 5");
            mUserEntity = realm.where(UserEntity.class).equalTo("id", ID).findFirst();
            Log.d(TAG, "realm transaction 15");
            if (null != mUserEntity) {
//                try {
//                    Thread.sleep(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                mUserEntity.setName("蓉蓉");
            }
        }, () -> Log.d(TAG,"realm transaction succeed1:name = "+ mUserEntityCache.getName()));

        Log.d(TAG, "realm transaction 6");
    }

}
