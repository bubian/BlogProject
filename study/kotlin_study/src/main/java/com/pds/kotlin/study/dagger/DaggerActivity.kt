package com.pds.kotlin.study.dagger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.BaseApplication
import com.pds.kotlin.study.MainActivity
import dagger.Component
import javax.inject.Inject

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 15:49
 * Email：pengdaosong@medlinker.com
 * Description:
 * 参考：https://developer.android.google.cn/training/dependency-injection/dagger-android#kotlin
 */
class DaggerActivity : AppCompatActivity() {

    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {

        inject()
        super.onCreate(savedInstanceState)
        val coffeeShop: CoffeeShop = DaggerCoffeeShop.builder().build()
        coffeeShop.maker()?.brew()
    }

    // Make Dagger instantiate @Inject fields in DaggerActivity
    private fun inject(){
        (applicationContext as BaseApplication).apply {
            appComponent.inject(this@DaggerActivity)
        }
    }

}