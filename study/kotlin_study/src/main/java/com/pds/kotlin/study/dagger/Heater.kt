package com.pds.kotlin.study.dagger

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:33
 * Email：pengdaosong@medlinker.com
 * Description:
 */
interface Heater {
    fun on()
    fun off()
    fun isHot(): Boolean
}