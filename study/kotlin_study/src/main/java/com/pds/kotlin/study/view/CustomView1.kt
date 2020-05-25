package com.pds.kotlin.study.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.pds.kotlin.study.R

/**
 * @author: pengdaosong
 * CreateTime:  2020-05-25 10:54
 * Email：pengdaosong@medlinker.com
 * Description:
 */

// @JvmOverloads 是一个 Kotlin 注解, 作用是替换构造函数的默认值生成多个版本的重载构造方法.
class CustomView1 @JvmOverloads constructor( context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    View(context, attrs, defStyleAttr) {

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),1600)
    }

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    init {
        // 加上会报：CustomView1 not displayed because it is too large to fit into a software layer (or drawing cache), needs 10368000 bytes, only 8294400 available
//        setLayerType(LAYER_TYPE_SOFTWARE, null)
        paintText.color = Color.WHITE
        paintText.textSize = 40f
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
    }

    // Path.setFillType(Path.FillType ft) 设置填充方式，参考：https://hencoder.com/ui-1-1/

    // 在 Android 的绘制里使用 Shader ，并不直接用 Shader 这个类，而是用它的几个子类。具体来讲有 LinearGradient RadialGradient SweepGradient BitmapShader ComposeShader
    // 在设置了 Shader 的情况下， Paint.setColor/ARGB() 所设置的颜色就不再起作用。
    // 参数：
    // x0 y0 x1 y1：渐变的两个端点的位置
    // color0 color1 是端点的颜色
    // tile：端点范围之外的着色规则，类型是 TileMode。TileMode 一共有 3 个值可选： CLAMP, MIRROR 和 REPEAT。如果没有端点之外之外的空间，三种模式看起来都差不多
    // CLAMP:端点之外延续端点处的颜色
    private val CLAMP = LinearGradient(0f, 500f, 300f, 800f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.CLAMP)
    // MIRROR:像模式
    private val MIRROR = LinearGradient(450f, 600f, 500f, 700f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.MIRROR)
    // REPEAT: 是重复模式
    private val REPEAT = LinearGradient(800f, 600f, 900f, 700f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.REPEAT)

    // 参数：
    // centerX centerY：辐射中心的坐标
    // radius：辐射半径
    // centerColor：辐射中心的颜色
    // edgeColor：辐射边缘的颜色
    // tileMode：辐射范围之外的着色模式。如果没有端点之外之外的空间，三种模式看起来都差不多
    // 辐射半径130f，130f ～ 150f是端点之外，CLAMP模式端点之外会延续端点处的颜色，也就是#2196F3
    private val radialGradient = RadialGradient(150f, 1050f, 100f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.CLAMP)
    private val radialGradientM = RadialGradient(500f, 1050f, 50f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.MIRROR)
    private val radialGradientR = RadialGradient(850f, 1050f, 50f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"), Shader.TileMode.REPEAT)

    // 扫描渐变
    private val sweepGradient = SweepGradient(150f, 1450f, Color.parseColor("#E91E63"), Color.parseColor("#2196F3"))

    // 用 Bitmap 来着色
    private var bitmap = BitmapFactory.decodeResource(resources, R.drawable.batman)
    private val bitmapShader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

    override fun onDraw(canvas: Canvas) {
        // 绘制bitmap是从控件的起点位置（这里就是0，0），所以如果drawCircle的x,y位置指定不在图片范围，填充后可能看不到图片。这里可以将半径增大到大于图片，可以试一试其它TileMode
        canvas.drawText("shader：BitmapShader",50f,65f,paintText)
        paint.shader = bitmapShader
        canvas.drawCircle(150f, 250f, 150f, paint)
        canvas.drawRect(350f, 100f, 1300f, 400f,paint)

        canvas.drawText("shader：LinearGradient",50f,465f,paintText)

        paint.shader = CLAMP
        canvas.drawRect(0f,500f,300f,800f,paint)

        paint.shader = MIRROR
        canvas.drawRect(350f,500f,650f,800f,paint)

        paint.shader = REPEAT
        canvas.drawRect(700f,500f,1000f,800f,paint)

        canvas.drawText("shader：RadialGradient",50f,865f,paintText)
        paint.shader = radialGradient
        canvas.drawCircle(150f, 1050f, 150f, paint)

        paint.shader = radialGradientM
        canvas.drawCircle(500f, 1050f, 150f, paint)

        paint.shader = radialGradientR
        canvas.drawCircle(850f, 1050f, 150f, paint)

        canvas.drawText("shader：SweepGradient, BitmapShader",50f,1265f,paintText)
        paint.shader = sweepGradient
        canvas.drawCircle(150f, 1450f, 150f, paint)

    }
}