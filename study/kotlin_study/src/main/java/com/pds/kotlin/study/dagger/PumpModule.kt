package com.pds.kotlin.study.dagger

import dagger.Binds
import dagger.Module
import kotlin.reflect.KClass


/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:34
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Module
abstract class PumpModule : KClass<Any> {
    @Binds
    abstract fun providePump(pump: Thermosiphon?): Pump?
}