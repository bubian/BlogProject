package com.pds.kotlin.study.dagger

import dagger.Component
import javax.inject.Singleton

/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 17:01
 * Email：pengdaosong@medlinker.com
 * Description:
 */
// The "modules" attribute in the @Component annotation tells Dagger what Modules
// to include when building the graph
@Singleton
@Component(modules = [NetworkModule::class,SubcomponentsModule::class])
interface ApplicationComponent {
    fun inject(activity: DaggerActivity)

    // This function exposes the LoginComponent Factory out of the graph so consumers
// can use it to obtain new instances of LoginComponent
    fun loginComponent(): LoginComponent.Factory
}