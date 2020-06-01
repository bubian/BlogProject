package com.pds.kotlin.study.dagger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pds.kotlin.BaseApplication
import javax.inject.Inject

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 17:44
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class LoginActivity : AppCompatActivity() {
    // Reference to the Login graph
    lateinit var loginComponent: LoginComponent

    // Fields that need to be injected by the login graph
    @Inject
    lateinit var loginViewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        // Creation of the login graph using the application graph
        loginComponent = (applicationContext as BaseApplication)
            .appComponent.loginComponent().create()

        // Make Dagger instantiate @Inject fields in LoginActivity
        loginComponent.inject(this)

        // Now loginViewModel is available

        super.onCreate(savedInstanceState)
    }
}