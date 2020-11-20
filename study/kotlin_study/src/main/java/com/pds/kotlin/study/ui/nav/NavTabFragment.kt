package com.pds.kotlin.study.ui.nav

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pds.base.act.BaseFragment
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.textview.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/20 5:04 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class NavTabFragment : BaseFragment() {

    private var mContent: String? = ""
    private var mLayoutId: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mContent = arguments?.getString("content")
        mLayoutId = arguments?.getInt("layoutId")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(mLayoutId ?: R.layout.textview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        textView.text = mContent
    }
}