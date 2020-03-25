package com.pds.ui.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * @author: pengdaosong CreateTime:  2019/4/10 8:57 AM Email：pengdaosong@medlinker.com Description:
 */
public class EvenItemDecoration extends RecyclerView.ItemDecoration {

    int space;
    int column;

    public EvenItemDecoration(int space, int column) {
        this.space = space;
        this.column = column;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        // 每个span分配的间隔大小
        int spanSpace = space * (column + 1) / column;
        // 列索引
        int colIndex = position % column;
        // 列左、右间隙
        outRect.left = space * (colIndex + 1) - spanSpace * colIndex;
        outRect.right = spanSpace * (colIndex + 1) - space * (colIndex + 1);
        // 行间距
        if (position >= column) {
            outRect.top = space;
        }
    }
}
