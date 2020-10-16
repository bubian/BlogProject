package com.pds.hilt;

import dagger.Binds;
import dagger.Module;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ActivityComponent;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/16 2:02 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@Module
@InstallIn(ActivityComponent.class)
public abstract class AnalyticsModule {

    @Binds
    public abstract AnalyticsService bindAnalyticsService(AnalyticsServiceImpl analyticsServiceImpl);
}
