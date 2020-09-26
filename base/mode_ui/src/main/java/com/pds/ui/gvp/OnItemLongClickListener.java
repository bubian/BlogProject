package com.pds.ui.gvp;

import android.view.View;

public interface OnItemLongClickListener<T> {

    boolean onItemLongClick(View view, int position, T data);
}