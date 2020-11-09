package com.pds.util.timer;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * @author: pengdaosong CreateTime:  2019-08-23 10:37 Email：pengdaosong@medlinker.com Description:
 */
public class CountTimeManager {

    private static final String TAG = "CountTimeManager";

    private static final int DEFAULT_CAP = 1000;

    private String id;


    /**
     * 计时handler
     */
    private Handler mHandler = new Handler(Looper.getMainLooper());

    /**
     * 反转间隔，单位：ms，默认1s
     */
    private int mCap = DEFAULT_CAP;

    /**
     *  最长计时时长
     */
    private long mLimitTime;

    /**
     * 总计时时长
     * @return
     */

    private long mTotalTime;
    /**
     * 是否时间已经取消或者暂停
     */
    private boolean mIsAbortTask;
    private boolean mIsCompleteTask;

    private TimeValue mTimeValue;

    private CountTimeManager(){}

    public static CountTimeManager getInstance(){
        return new CountTimeManager();
    }

    private boolean isComplete(){
        return 0 != mLimitTime && mLimitTime == mTotalTime;
    }

    /**
     * 开始计时
     */
    public void start(){
        if (null == mHandler){
            return;
        }
        mIsAbortTask = false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.postDelayed(mCountRunnable,mCap);
    }

    private Runnable mCountRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAbortTask){
                if (null != mTimeValue){
                    mHandler.removeCallbacksAndMessages(null);
                    mTimeValue.cancel(id,mTotalTime,mLimitTime);
                }
                return;
            }
            if (mIsCompleteTask){
                mTimeValue.complete(id,mLimitTime);
                mHandler.removeCallbacksAndMessages(null);
                return;
            }
            mTotalTime = mTotalTime + mCap;
            doCallback();
        }
    };

    public void reset(){
        mIsAbortTask = false;
        mIsCompleteTask= false;
        mLimitTime = 0;
        mTotalTime = 0;
    }

    private void doCallback(){
        if (null == mTimeValue){
            mHandler.postDelayed(mCountRunnable,mCap);
            return;
        }
        if (isComplete()){
            mTimeValue.complete(id,mLimitTime);
        }else {
            mHandler.postDelayed(mCountRunnable,mCap);
            mTimeValue.interval(id,mTotalTime);
        }
    }

    public CountTimeManager setTimeCallback(TimeValue timeValue){
        mTimeValue = timeValue;
        return this;
    }

    public void setCap(int cap) {
        mCap = cap;
    }

    public void setLimitTime(long limitTime) {
        mLimitTime = limitTime;
    }

    public void setTotalTime(long totalTime) {
        mTotalTime = totalTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * 停止计时
     */

    public void stop(){
        mIsAbortTask = true;
        if (null != mHandler){
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    /**
     * 内存清理
     */

    public void destroy(){
        doCallback();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mTimeValue = null;
        mLimitTime = 0;
        mTotalTime = 0;
    }

    interface TimeValue{
        void interval(String id,long totalTime);
        void complete(String id,long limitTime);
        void cancel(String id,long totalTime,long limitTime);
    }

    public static class SimpleTimeValue implements TimeValue{

        @Override
        public void interval(String id,long totalTime) {
        }

        @Override
        public void complete(String id,long limitTime) {
        }

        @Override
        public void cancel(String id,long totalTime, long limitTime) {
        }
    }
}
