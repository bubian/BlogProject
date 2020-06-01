package com.pds.kotlin.study.dagger

import javax.inject.Inject


/**
 * @author: pengdaosong
 * CreateTime:  2020-06-01 17:02
 * Email：pengdaosong@medlinker.com
 * Description:
 */

// @Inject tells Dagger how to create instances of LoginViewModel
class LoginViewModel @Inject constructor(private val userRepository: UserRepository)