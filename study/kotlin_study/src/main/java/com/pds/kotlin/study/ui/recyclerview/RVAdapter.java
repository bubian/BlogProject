package com.pds.kotlin.study.ui.recyclerview;

import android.app.Activity;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pds.kotlin.study.R;
import com.pds.kotlin.study.ui.recyclerview.helper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/11/21 2:17 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class RVAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {

    ArrayList<Integer> mColors = new ArrayList<>();
    private RecyclerView mRecyclerView;

    public RVAdapter(RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        generateData();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder myHolder = (MyViewHolder) holder;
        int color = mColors.get(position);
        myHolder.container.setBackgroundColor(color);
        if (1 == position){
            myHolder.textView.setText("测滑删除");
        }else if (2 == position){
            myHolder.textView.setText("测滑菜单");
        }else {
            myHolder.textView.setText("第二个Adapter");
        }
    }

    @Override
    public int getItemCount() {
        return mColors.size();
    }

    private void deleteItem(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        if (position != RecyclerView.NO_POSITION) {
            mColors.remove(position);
            notifyItemRemoved(position);
        }
    }

    private void addItem(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        if (position != RecyclerView.NO_POSITION) {
            int color = generateColor();
            mColors.add(position, color);
            notifyItemInserted(position);
        }
    }

    private void changeItem(View view) {
        int position = mRecyclerView.getChildAdapterPosition(view);
        if (position != RecyclerView.NO_POSITION) {
            int color = generateColor();
            mColors.set(position, color);
            notifyItemChanged(position);
        }
    }

    private View.OnClickListener mItemAction = v -> deleteItem(v);

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Activity activity = (Activity) mRecyclerView.getContext();
        View container = activity.getLayoutInflater().inflate(R.layout.rl_slide_item, parent, false);
        container.setOnClickListener(mItemAction);
        return new MyViewHolder(container);
    }

    private int generateColor() {
        int red = ((int) (Math.random() * 200));
        int green = ((int) (Math.random() * 200));
        int blue = ((int) (Math.random() * 200));
        return Color.rgb(red, green, blue);
    }

    private void generateData() {
        for (int i = 0; i < 4; ++i) {
            mColors.add(generateColor());
        }
    }


    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        // 首先保证数据集合数据正确
        Collections.swap(mColors, fromPosition, toPosition);
        // 通知item发生移动
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        mColors.remove(position);
        notifyItemRemoved(position);
    }
}
