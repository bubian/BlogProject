package com.pds.kotlin.study.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-26 15:07
 * Email：pengdaosong@medlinker.com
 * Description:
 */
// Android 里动画是有一些分类的：动画可以分为两类：Animation 和 Transition；
// 其中 Animation 又可以再分为 View Animation 和 Property Animation 两类： View Animation 是纯粹基于 framework 的绘制转变
class PropertyAnimationView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.kkkk)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),400)
    }

    override fun onDraw(canvas: Canvas) {
        canvas.drawBitmap(bitmap,100f,50f,paint)
    }
}