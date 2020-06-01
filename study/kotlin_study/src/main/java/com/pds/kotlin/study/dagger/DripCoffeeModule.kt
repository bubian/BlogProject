package com.pds.kotlin.study.dagger

import dagger.Module
import dagger.Provides
import javax.inject.Singleton

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:32
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Module(includes = [PumpModule::class])
class DripCoffeeModule {
    @Provides @Singleton fun provideHeater() : Heater {
        return ElectricHeater()
    }
}