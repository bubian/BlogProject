package com.pds.base.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.android.vlayout.DelegateAdapter.Adapter;
import com.alibaba.android.vlayout.LayoutHelper;
import com.alibaba.android.vlayout.layout.SingleLayoutHelper;
import com.pds.base.holder.BaseViewHolder;

public class VLayoutSingleAdapter<T> extends Adapter {

  private int mLayoutId;
  private T mData;

  public VLayoutSingleAdapter(){
  }

  public VLayoutSingleAdapter(@LayoutRes int id){
    this.mLayoutId = id;
  }

  public VLayoutSingleAdapter(@LayoutRes int id,T data){
    this.mLayoutId = id;
    mData = data;
  }

  public void setData(T data) {
    mData = data;
    notifyDataSetChanged();
  }

  public T getData() {
    return mData ;
  }

  @Override
  public LayoutHelper onCreateLayoutHelper() {
    return new SingleLayoutHelper();
  }

  @Override
  public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View itemView;
    if (mLayoutId <= 0){
      itemView = getItemView(parent,viewType);
    }else {
      itemView = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
    }
    return new BaseViewHolder(itemView);
  }

  @Override
  public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
    onBindView((BaseViewHolder)holder,position, mData);
  }

  public  void onBindView(BaseViewHolder holder, int position,T data){

  }

  public View getItemView(ViewGroup parent, int viewType){
    return null;
  }

  @Override
  public int getItemCount() {
    return 1;
  }
}
