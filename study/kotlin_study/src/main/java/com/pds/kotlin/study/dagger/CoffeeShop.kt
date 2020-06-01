package com.pds.kotlin.study.dagger

import dagger.Component
import javax.inject.Singleton

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:29
 * Email：pengdaosong@medlinker.com
 * Description:
 */
@Singleton
@Component(modules = [DripCoffeeModule::class])
interface CoffeeShop {
    fun maker(): CoffeeMaker?
}