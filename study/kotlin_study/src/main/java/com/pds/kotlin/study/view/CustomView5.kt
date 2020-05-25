package com.pds.kotlin.study.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 19:42
 * Email：pengdaosong@medlinker.com
 * Description:
 */
class CustomView5 @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        paint.color = Color.WHITE
        paintText.color = Color.WHITE
        paintText.textSize = 40f
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),300)
    }

    private val pathEffect =  DashPathEffect(floatArrayOf(10f,5f), 10f)
    override fun onDraw(canvas: Canvas) {
        // 设置图像的抖动
        paint.isDither = true
        // 图像在放大绘制的时候，默认使用的是最近邻插值过滤，这种算法简单，但会出现马赛克现象；而如果开启了双线性过滤，就可以让结果图像显得更加平滑
        paint.isFilterBitmap = true

        canvas.drawText("shader：setPathEffect",50f,65f,paintText)
        paint.style = Paint.Style.STROKE
        // PathEffect 分为两类，单一效果的 CornerPathEffect DiscretePathEffect DashPathEffect PathDashPathEffect ，和组合效果的 SumPathEffect ComposePathEffect。
        paint.pathEffect = pathEffect
        canvas.drawCircle(150f, 150f, 50f, paint)
    }
}