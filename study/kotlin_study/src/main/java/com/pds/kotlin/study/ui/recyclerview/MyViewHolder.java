package com.pds.kotlin.study.ui.recyclerview;

import android.graphics.Canvas;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pds.kotlin.study.R;
import com.pds.kotlin.study.ui.recyclerview.helper.ItemTouchHelperViewHolder;
import com.pds.util.unit.UnitConversionUtils;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/21 2:25 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class MyViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {
    public TextView textView;
    public TextView tv;
    public ImageView iv;
    public LinearLayout container;

    //限制ImageView长度所能增加的最大值
    private double ICON_MAX_SIZE;
    //ImageView的初始长宽
    private int fixedWidth = 150;

    public MyViewHolder(View v) {
        super(v);
        container = (LinearLayout) v;
        textView = v.findViewById(R.id.textview);
        tv = v.findViewById(R.id.tv_text);
        iv = v.findViewById(R.id.iv_img);
        ICON_MAX_SIZE = UnitConversionUtils.dip2px(v.getContext(),40F);
    }

    @Override
    public String toString() {
        return super.toString() + " \"" + textView.getText() + "\"";
    }

    @Override
    public void onItemSelected() {

    }

    @Override
    public void onItemClear() {
        //重置改变，防止由于复用而导致的显示问题
        itemView.setScrollX(0);
        tv.setText("左滑删除");
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) iv.getLayoutParams();
        params.width = 150;
        params.height = 150;
        iv.setLayoutParams(params);
        iv.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //仅对侧滑状态下的效果做出改变
        //如果dX小于等于删除方块的宽度，那么我们把该方块滑出来
        if (Math.abs(dX) <= getSlideLimitation(viewHolder)) {
            itemView.scrollTo(-(int) dX, 0);
        }
        //如果dX还未达到能删除的距离，此时慢慢增加“眼睛”的大小，增加的最大值为ICON_MAX_SIZE
        else if (Math.abs(dX) <= recyclerView.getWidth() / 2) {
            double distance = (recyclerView.getWidth() / 2 - getSlideLimitation(viewHolder));
            double factor = ICON_MAX_SIZE / distance;
            double diff = (Math.abs(dX) - getSlideLimitation(viewHolder)) * factor;
            if (diff >= ICON_MAX_SIZE) diff = ICON_MAX_SIZE;
            tv.setText("");   //把文字去掉
            iv.setVisibility(View.VISIBLE);  //显示眼睛
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)iv.getLayoutParams();
            params.width = (int) (ICON_MAX_SIZE);
            params.height = (int) (ICON_MAX_SIZE);
            iv.setLayoutParams(params);
        }
        return true;
    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }
}
