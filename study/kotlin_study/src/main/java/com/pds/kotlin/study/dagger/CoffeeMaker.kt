package com.pds.kotlin.study.dagger

import javax.inject.Inject

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */

class CoffeeMaker @Inject constructor(heater: dagger.Lazy<Heater>?, pump: Pump?){
    private var heater: dagger.Lazy<Heater>? = null
    private var pump: Pump? = null

    init {
        this.heater = heater
        this.pump = pump
    }

    fun brew() {
        heater?.get()?.on()
        pump?.pump()
        println(" [_]P coffee! [_]P ")
        heater?.get()?.off()
    }
}