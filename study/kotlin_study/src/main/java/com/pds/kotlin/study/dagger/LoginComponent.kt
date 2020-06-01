package com.pds.kotlin.study.dagger

import dagger.Subcomponent

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 17:44
 * Email：pengdaosong@medlinker.com
 * Description:
 */

@Subcomponent
interface LoginComponent {
    // Factory that is used to create instances of this subcomponent
    @Subcomponent.Builder
    interface Factory {
        fun create(): LoginComponent
    }
    // This tells Dagger that LoginActivity requests injection from LoginComponent
    // so that this subcomponent graph needs to satisfy all the dependencies of the
    // fields that LoginActivity is injecting
    fun inject(loginActivity: LoginActivity)
}