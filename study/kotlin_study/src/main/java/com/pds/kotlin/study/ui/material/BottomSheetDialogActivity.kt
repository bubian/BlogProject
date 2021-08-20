package com.pds.kotlin.study.ui.material

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.material_bottom_sheet_dialog_fragment.*

class BottomSheetDialogActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layoutId = intent.getIntExtra("layout_id", R.layout.material_bottomappbar)
        setContentView(layoutId)
        bottomSheetDialog.setOnClickListener {
            BottomSheetDialog(this).apply {
                setContentView(R.layout.material_bottomappbar);
                show()
            }
        }

        bottomSheetDialogFragment.setOnClickListener {
            MyBottomSheetDialogFragment().show(supportFragmentManager, "bottomSheetDialogFragment")
        }
    }

    class MyBottomSheetDialogFragment : BottomSheetDialogFragment(){
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            return inflater.inflate(R.layout.material_bottomappbar, container, false)
        }
    }
}