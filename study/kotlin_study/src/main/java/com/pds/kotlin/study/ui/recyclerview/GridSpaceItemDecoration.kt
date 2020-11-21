package com.pds.kotlin.study.ui.recyclerview

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * @author: pengdaosong
 * @CreateTime:  2020/11/21 4:01 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
class GridSpaceItemDecoration
/**
 * @param spanCount     列数
 * @param rowSpacing    行间距
 * @param columnSpacing 列间距
 */(spanCount: Int, rowSpacing: Int, columnSpacing: Int) : RecyclerView.ItemDecoration() {

    private var mSpanCount //横条目数量
            = spanCount
    private var mRowSpacing //行间距
            = rowSpacing
    private var mColumnSpacing // 列间距
            = columnSpacing

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view!!) // 获取view 在adapter中的位置。
        val column = position % mSpanCount // view 所在的列
        outRect.left = column * mColumnSpacing / mSpanCount // column * (列间距 * (1f / 列数))
        outRect.right =
            mColumnSpacing - (column + 1) * mColumnSpacing / mSpanCount // 列间距 - (column + 1) * (列间距 * (1f /列数))
        // 如果position > 行数，说明不是在第一行，则不指定行高，其他行的上间距为 top=mRowSpacing
        if (position >= mSpanCount) {
            outRect.top = mRowSpacing // item top
        }
    }
}