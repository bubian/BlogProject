package com.pds.entity.base;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class DataListEntity<T extends DataEntity> extends DataEntity {

    @SerializedName("start")
    private String start;
    @SerializedName("more")
    private int more;
    @SerializedName("list")
    private ArrayList<T> list;
    @SerializedName("total")
    private int total;
    @SerializedName("newAddCounts")
    private int newAddCounts;
    @SerializedName("recentlyFeedsComplete")
    private String recentlyFeedsComplete;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public boolean hasMore() {
        return more == 1;
    }

    public void setMore(boolean hasMore) {
        this.more = hasMore ? 1 : 0;
    }

    public ArrayList<T> getList() {
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public int getMore() {
        return more;
    }

    public void setMore(int more) {
        this.more = more;
    }

    public String getRecentlyFeedsComplete() {
        return recentlyFeedsComplete;
    }

    public void setRecentlyFeedsComplete(String recentlyFeedsComplete) {
        this.recentlyFeedsComplete = recentlyFeedsComplete;
    }

    public int getNewAddCounts() {
        return newAddCounts;
    }

    public void setNewAddCounts(int newAddCounts) {
        this.newAddCounts = newAddCounts;
    }

    public void setList(ArrayList<T> list) {
        this.list = list;
    }

    public DataListEntity(ArrayList<T> list) {
        this.list = list;
    }

    public DataListEntity() {
    }

    @Override
    public String toString() {
        return "DataWraperEntity{" +
                ", start='" + start + '\'' +
                ", more=" + more +
                ", list=" + list +
                '}';
    }
}
