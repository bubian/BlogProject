package com.pds.kotlin.study.ui.damp

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.dynamicanimation.animation.SpringForce
import com.pds.base.act.BaseActivity
import com.pds.kotlin.study.R
import kotlinx.android.synthetic.main.acticity_damp.*
import kotlin.math.atan2

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/25 12:59 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 参考：
 * 1. https://mp.weixin.qq.com/s/jIdBg3RQvs0YvD1T-q0wbA
 * 2. https://developer.android.google.cn/guide/topics/graphics/spring-animation#add-support-library
 */
@SuppressLint("ClickableViewAccessibility")
class DampActivity : BaseActivity() {

    var offsetX: Float = 0f
    var offsetY: Float = 0f

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acticity_damp)
        // 弹性与阻尼
        springAnimation(tv_damp)
        // 旋转动画
        rotationAnimation(tv_rotation)
        // 缩放动画
        scaleAnimation(tv_scale)
        // 平移动画
        translationAnimation(tv_translation)
    }

    private fun springAnimation(view: View) {
        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    offsetX = event.rawX
                    offsetY = event.rawY
                }

                MotionEvent.ACTION_MOVE -> {
                    // 增加阻尼运动
                    tv_damp.translationX = doDamping(event.rawX - offsetX)
                    tv_damp.translationY = doDamping(event.rawY - offsetY)

                    // tv_damp.translationX = event.rawX - offsetX
                    // tv_damp.translationY = event.rawY - offsetY
                }

                MotionEvent.ACTION_UP -> {
                    SpringAnimation(tv_damp, DynamicAnimation.TRANSLATION_Y).apply {
                        // SpringForce是描述该弹性系统的各种参数的封装
                        spring = SpringForce().apply {
                            // dampingRatio = DAMPING_RATIO_NO_BOUNCY
                            // stiffness = SpringForce.STIFFNESS_VERY_LOW
                        }
                        animateToFinalPosition(0f)
                    }
                    SpringAnimation(tv_damp, DynamicAnimation.TRANSLATION_X).apply {
                        spring = SpringForce().apply {
                            // dampingRatio = DAMPING_RATIO_NO_BOUNCY
                            // stiffness = SpringForce.STIFFNESS_VERY_LOW
                        }
                        animateToFinalPosition(0f)
                    }
                }
            }
            true
        }
    }

    /**
     * 旋转动画
     */
    private fun rotationAnimation(view: View) {
        view.setOnTouchListener { view, event ->
            val centerX = view.width / 2.0
            val centerY = view.height / 2.0
            val x = event.x
            val y = event.y
            var currentRotation = 0F
            var previousRotation: Float
            fun updateRotation() {
                currentRotation =
                    view.rotation + Math.toDegrees(atan2(x - centerX, centerY - y)).toFloat()
            }

            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> updateRotation()
                MotionEvent.ACTION_MOVE -> {
                    previousRotation = currentRotation
                    updateRotation()
                    val angle = currentRotation - previousRotation
                    view.rotation += angle
                }
                MotionEvent.ACTION_UP -> SpringAnimation(
                    view,
                    DynamicAnimation.ROTATION
                ).animateToFinalPosition(0f)
            }
            true
        }
    }

    /**
     * 缩放动画
     */
    private fun scaleAnimation(view: View) {
        var scaleGestureDetector = ScaleGestureDetector(this,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    var scaleFactor = detector.scaleFactor
                    view.scaleX *= scaleFactor
                    view.scaleY *= scaleFactor
                    return true
                }
            })
        view.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                SpringAnimation(view, DynamicAnimation.SCALE_X).animateToFinalPosition(1f)
                SpringAnimation(view, DynamicAnimation.SCALE_Y).animateToFinalPosition(1f)
            } else {
                scaleGestureDetector.onTouchEvent(event)
            }
            true
        }
    }

    private fun translationAnimation(view: View) {
        view.translationY = 1600f
        SpringAnimation(view, SpringAnimation.TRANSLATION_Y, 0f).apply {
            spring.stiffness = SpringForce.STIFFNESS_VERY_LOW
            spring.dampingRatio = SpringForce.DAMPING_RATIO_LOW_BOUNCY
            setStartVelocity(-2000f)
            start()
        }
    }

    /**
     * 阻尼
     */
    private fun doDamping(value: Float): Float {
        return if (value < 0)
            -kotlin.math.sqrt((100f * kotlin.math.abs(value)).toDouble()).toFloat()
        else
            kotlin.math.sqrt((100f * value).toDouble()).toFloat()
    }
}