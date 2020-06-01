package com.pds.kotlin.study.dagger

import javax.inject.Inject

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:35
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class Thermosiphon @Inject constructor(heater : Heater) : Pump {
    private var heater : Heater? = null

    init {
        this.heater = heater
    }

    override fun pump() {
        if (heater?.isHot()!!) {
            println("=> => pumping => =>");
        }
    }
}