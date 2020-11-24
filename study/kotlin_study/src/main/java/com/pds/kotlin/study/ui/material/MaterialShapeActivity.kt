package com.pds.kotlin.study.ui.material

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.google.android.material.shape.*
import com.pds.base.act.BaseActivity
import com.pds.kotlin.study.R
import com.pds.kotlin.study.ui.expand.dp
import kotlinx.android.synthetic.main.material_shape.*

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/25 2:37 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 * 参考：https://mp.weixin.qq.com/s?__biz=MzAxNzMxNzk5OQ==&mid=2649486685&idx=1&sn=f75a63a7396fe5a603406d6c7ec9a56b&chksm=83f83c5db48fb54b71d4f3c2f7203e4b1313e9682cd027323b1632cdbe5ec857efaa6732a376&scene=178&cur_album_id=1567168071180566530#rd
 */
class MaterialShapeActivity : BaseActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val  layoutId = intent.getIntExtra("layout_id", R.layout.material_bottomappbar)
        setContentView(layoutId)
        one()
        two()
        three()
        four()
    }

    private fun one(){
        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(40.dp())
            .build()
        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            setTint(Color.parseColor("#bebebe"))
            paintStyle = Paint.Style.FILL
        }
        shape_one.background = backgroundDrawable
    }

    private fun two(){
        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(40.dp())
            .setAllEdges(TriangleEdgeTreatment(38.dp(), true))
            .build()
        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            setTint(Color.parseColor("#bebebe"))
            paintStyle = Paint.Style.FILL_AND_STROKE
            strokeWidth = 15.dp()
        }
        shape_two.background = backgroundDrawable
    }

    private fun three(){
        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(56.dp())
            .setRightEdge(object : TriangleEdgeTreatment(58.dp(), false) {
                override fun getEdgePath(
                    length: Float,
                    center: Float,
                    interpolation: Float,
                    shapePath: ShapePath
                ) {
                    super.getEdgePath(length, 52.dp(), interpolation, shapePath)
                }
            })
            .build()
        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            setTint(Color.parseColor("#bebebe"))
            paintStyle = Paint.Style.FILL
        }
        (shape_three.parent as? ViewGroup)?.clipChildren = false
        shape_three.background = backgroundDrawable
    }

    /**
     * 阴影的处理
     */
    @SuppressLint("RestrictedApi")
    private fun four(){
        val shapePathModel = ShapeAppearanceModel.builder()
            .setAllCorners(RoundedCornerTreatment())
            .setAllCornerSizes(36.dp())
            .build()
        val backgroundDrawable = MaterialShapeDrawable(shapePathModel).apply {
            setTint(Color.parseColor("#05bebebe"))
            paintStyle = Paint.Style.FILL
            shadowCompatibilityMode = MaterialShapeDrawable.SHADOW_COMPAT_MODE_ALWAYS
            initializeElevationOverlay(this@MaterialShapeActivity)
            shadowRadius = 46.dp().toInt()
            setShadowColor(Color.parseColor("#ff0000"))
            shadowVerticalOffset = 42.dp().toInt()
        }
        (shape_four.parent as? ViewGroup)?.clipChildren = false
        shape_four.background = backgroundDrawable
    }

    class InnerCutCornerTreatment : CornerTreatment() {
        override fun getCornerPath(shapePath: ShapePath, angle: Float, f: Float, size: Float) {
            val radius = size * f
            shapePath.reset(0f, radius, 180f, 180 - angle)
            shapePath.lineTo(radius, radius)
            shapePath.lineTo(radius, 0f)
        }
    }

    class InnerRoundCornerTreatment : CornerTreatment() {
        override fun getCornerPath(shapePath: ShapePath, angle: Float, f: Float, size: Float) {
            val radius = size * f
            shapePath.reset(0f, radius, 180f, 180 - angle)
            shapePath.addArc(-radius, -radius, radius, radius, angle, -90f)
        }
    }

    class ExtraRoundCornerTreatment : CornerTreatment() {
        override fun getCornerPath(shapePath: ShapePath, angle: Float, f: Float, size: Float) {
            val radius = size * f
            shapePath.reset(0f, radius, 180f, 180 - angle)
            shapePath.addArc(-radius, -radius, radius, radius, angle, 270f)
        }
    }

    class ArgEdgeTreatment(val size: Float, val inside: Boolean) : EdgeTreatment() {
        override fun getEdgePath(length: Float, center: Float, f: Float, shapePath: ShapePath) {
            val radius = size * f
            shapePath.lineTo(center - radius, 0f)
            shapePath.addArc(
                center - radius, -radius,
                center + radius, radius,
                180f,
                if (inside) -180f else 180f
            )
            shapePath.lineTo(length, 0f)
        }
    }

    class QuadEdgeTreatment(val size: Float) : EdgeTreatment() {
        @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
        override fun getEdgePath(length: Float, center: Float, f: Float, shapePath: ShapePath) {
            shapePath.quadToPoint(center, size * f, length, 0f)
        }
    }

}
