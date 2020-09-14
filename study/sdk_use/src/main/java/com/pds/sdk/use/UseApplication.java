package com.pds.sdk.use;

import android.app.Application;

import com.facebook.stetho.Stetho;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import io.realm.Realm;

/**
 * @author: pengdaosong
 * CreateTime:  2020-08-22 11:16
 * Email：pengdaosong@medlinker.com
 * Description:
 */
public class UseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
    }
}
