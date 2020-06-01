package com.pds.kotlin.study.dagger

import dagger.Module
import dagger.Provides

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 17:32
 * Email：pengdaosong@medlinker.com
 * Description:
 */

@Module
class NetworkModule{
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        // Whenever Dagger needs to provide an instance of type LoginRetrofitService,
        // this code (the one inside the @Provides method) is run.
        return LoginRetrofitService()
    }
}