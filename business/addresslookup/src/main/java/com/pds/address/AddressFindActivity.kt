package com.pds.address

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.android.arouter.facade.annotation.Route
import com.pds.router.module.ModuleGroupRouter

@Route(path = ModuleGroupRouter.ADDRESS_FIND)
class AddressFindActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_address_find)
    }
}