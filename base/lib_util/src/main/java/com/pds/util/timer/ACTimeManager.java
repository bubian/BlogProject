package com.pds.util.timer;


import java.util.HashMap;
import java.util.Map;


/**
 * @author: pengdaosong CreateTime:  2019-08-23 11:29 Email：pengdaosong@medlinker.com Description:
 */
public class ACTimeManager {

    private ACTimeManager mACTimeManager;

    private Map<String,CountTimeManager> mTimeItems;
    private Map<String,CountTimeManager> mCacheTimeItems;

    public ACTimeManager(){
        mTimeItems = new HashMap<>();
        mCacheTimeItems = new HashMap<>();
    }

    public CountTimeManager getCountTimeManagerFromCache(String key){
        if(null == mCacheTimeItems){
            return CountTimeManager.getInstance();
        }
        CountTimeManager manager = mCacheTimeItems.remove(key);
        if (null == manager){
            manager = CountTimeManager.getInstance();
        }
        return manager;
    }


    public void addACTimeItem(String key,CountTimeManager manager){
        if (null == mTimeItems){
            mTimeItems = new HashMap<>();
        }
        mTimeItems.put(key,manager);
    }

    public void addToCache(){
        if (null == mTimeItems){
           return;
        }
        if (null == mCacheTimeItems){
            mCacheTimeItems = new HashMap<>();
        }
        mCacheTimeItems.putAll(mTimeItems);
    }

    public void setTimeValueCallback(String key, CountTimeManager.TimeValue timeValue){
        if (null == mTimeItems){
            return;
        }

        CountTimeManager manager = mTimeItems.get(key);
        if (null != manager){
            manager.setTimeCallback(timeValue);
        }
    }

    public void startTimeCount(String key){
        if (null == mTimeItems){
            return;
        }

        CountTimeManager manager = mTimeItems.get(key);
        if (null != manager){
            manager.start();
        }
    }

    public void stopTimeCount(String key){
        if (null == mTimeItems){
            return;
        }

        CountTimeManager manager = mTimeItems.get(key);
        if (null != manager){
            manager.stop();
            manager.setTimeCallback(null);
        }
    }

    public void resetTimeCount(String key){
        if (null == mTimeItems){
            return;
        }

        CountTimeManager manager = mTimeItems.get(key);
        if (null != manager){
            manager.stop();
            manager.setTimeCallback(null);
            manager.reset();
        }
    }

    public void removeTimeValueCallback(String key){
        if (null == mTimeItems){
            return;
        }
        CountTimeManager manager = mTimeItems.get(key);
        if (null != manager){
            manager.setTimeCallback(null);
        }
    }

    public void removeAllACTimeItem(){
        if (null == mTimeItems){
            return;
        }

        for (CountTimeManager countTimeManager : mTimeItems.values()){
            countTimeManager.reset();
            countTimeManager.stop();
        }

        mTimeItems.clear();
    }


    public void removeAllCacheACTimeItem(){
        if (null == mCacheTimeItems){
            return;
        }
        for (CountTimeManager countTimeManager : mCacheTimeItems.values()){
            countTimeManager.reset();
            countTimeManager.stop();
        }
        mCacheTimeItems.clear();
    }

    public void destory(){
        mTimeItems.clear();
        mCacheTimeItems.clear();
        mACTimeManager = null;
    }

    public static String getKey(long id,int type){
        return "" + id +"_"+ type;
    }
}
