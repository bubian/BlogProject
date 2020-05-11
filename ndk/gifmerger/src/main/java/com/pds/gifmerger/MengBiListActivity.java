package com.pds.gifmerger;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import md.edu.pds.kt.gifmerge.R;

public class MengBiListActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meng_bi_list);
        mRecyclerView = findViewById(R.id.recycler_view);
        GridLayoutManager manager = new GridLayoutManager(MengBiListActivity.this, 2);

        MengbiAdapter mengbiAdapter = new MengbiAdapter(MengBiListActivity.this);
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(20));
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mengbiAdapter);
    }

    private static class MengbiAdapter extends RecyclerView.Adapter<MengbiViewHolder> {

        private final Context context;

        private TypedArray mDrawableList;

        public MengbiAdapter(Context context) {
            this.context = context;
            mDrawableList = context.getResources().obtainTypedArray(R.array.source);
        }

        @Override
        public MengbiViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new MengbiViewHolder(LayoutInflater.from(context).inflate(R.layout.item_mengbi, null));
        }

        @Override
        public void onBindViewHolder(MengbiViewHolder holder, int position) {
            holder.bind(position, mDrawableList.getResourceId(position, -1));
        }

        @Override
        public int getItemCount() {
            return mDrawableList.length();
        }
    }

    static class MengbiViewHolder extends RecyclerView.ViewHolder {

        ImageView mImageView;
        TextView mTextView;

        public MengbiViewHolder(View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.image);
            mTextView = itemView.findViewById(R.id.position);

        }

        public void bind(int position, int resId) {
            mImageView.setImageResource(resId);
            mTextView.setText("" + (position + 1) + "");
        }
    }

    private static class SpaceItemDecoration extends RecyclerView.ItemDecoration {

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if (parent.getChildLayoutPosition(view) < 2) {
                outRect.top = space;
            }
            if (parent.getChildLayoutPosition(view) % 2 == 0) {
                outRect.left = space;
                outRect.bottom = space;
            } else {
                outRect.left = space;
                outRect.bottom = space;
                outRect.right = space;
            }
        }
    }
}


