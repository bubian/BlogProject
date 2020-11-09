package com.pds.kotlin.study.ui.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/3 7:49 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class NavFragment : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.nav_tab,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }
}