package com.pds.kotlin.study.ui.material

import android.app.Activity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.material_textfields.*

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-29 14:05
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CommonMaterialComponentActivity : Activity() {
    private var title = ""
    private var layoutId= 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutId = intent.getIntExtra("layout_id", R.layout.material_bottomappbar)
        title = intent.getStringExtra("title") ?: "title"
        setContentView(layoutId)
        initMenu()
    }

    private fun initMenu() {
        textField_one?.let {
            val items = listOf("Material", "Design", "Components", "Android")
            val adapter = ArrayAdapter(this, R.layout.list_item, items)
            (it.editText as AutoCompleteTextView).setAdapter(adapter)
        }

        filledTextField?.let {
            // Get input text
            val inputText = it.editText?.text.toString()
//            filledTextField.editText?.doOnTextChanged { _, _, _, _ -> }
        }
    }
}