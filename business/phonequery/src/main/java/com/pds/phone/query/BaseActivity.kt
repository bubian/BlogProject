package com.pds.phone.query

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import android.view.*
import androidx.annotation.NonNull
import com.pds.splugin.model.InterfaceActivity

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/15 2:11 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
@SuppressLint("MissingSuperCall")
open class BaseActivity : Activity(), InterfaceActivity {

    protected var mThat: Activity? = null

    override fun attach(proxyActivity: Activity) {
        mThat = proxyActivity
    }

    override fun setContentView(view: View?) {
        if (mThat != null) {
            mThat?.setContentView(view)
        } else {
            super.setContentView(view)
        }
    }

    override fun setContentView(layoutResID: Int) {
        if (mThat != null) {
            mThat?.setContentView(layoutResID)
        } else {
            super.setContentView(layoutResID)
        }
    }

    override fun startService(service: Intent): ComponentName? {
        val m = Intent()
        m.putExtra("serviceName", service.component.className)
        return mThat?.startService(m)
    }

    override fun <T : View> findViewById(id: Int): T? {
        return if (null != mThat) mThat?.findViewById(id) else super.findViewById(id)
    }

    override fun getIntent(): Intent? {
        return if (null != mThat) mThat?.intent else super.getIntent()

    }

    override fun getClassLoader(): ClassLoader?{
        return if (null != mThat) mThat?.classLoader else super.getClassLoader()
    }

    override fun startActivity(intent: Intent) {
        val m = Intent()
        m.putExtra("className", intent.component.className)
        mThat?.startActivity(m)
    }

    @NonNull
    override fun getLayoutInflater(): LayoutInflater? {
        return if (null != mThat) mThat?.layoutInflater else super.getLayoutInflater()
    }

    override fun getApplicationInfo(): ApplicationInfo? {
        return if (null != mThat) mThat?.applicationInfo else super.getApplicationInfo()
    }
    override fun getWindow(): Window? {
        return if (null != mThat) mThat?.window else super.getWindow()
    }

    override fun getWindowManager(): WindowManager?{
        return if (null != mThat) mThat?.windowManager else super.getWindowManager()
    }


    override fun onCreate(savedInstanceState: Bundle) {}

    override fun onResume() {
    }

    override fun onPause() {
    }

    override fun onSaveInstanceState(outState: Bundle) {
    }

    override fun onStop() {
    }

    override fun onBackPressed() {
    }

    override fun onStart() {
    }

    override fun onDestroy() {
    }

}