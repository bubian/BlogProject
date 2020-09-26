package com.pds.base.manager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.function.Function;
import java.util.function.Predicate;


public class ActivityStashManager {

    private static final String TAG = "Live_ActivityStash";
    /**
     * 存放所有Activity的Map
     */
    private static final Map<String, LinkedList<ActivityInfo>> COUNTER = new HashMap<>();

    private static String mLastPageId = "";//统计使用的上一个页面

    /**
     * 当前Activity
     */
    private static WeakReference<Activity> sCurrentActivity = null;
    //activity 启动的顺序列表
    private static LinkedList<WeakReference<Activity>> activityList = new LinkedList<>();

    /**
     * activity创建时调用此方法
     *
     * @param activity
     */
    public static void onActivityCreated(Activity activity) {
        if (activity == null) {
            return;
        }
        String activityName = activity.getClass().getName();
        LinkedList<ActivityInfo> acitivies = COUNTER.get(activityName);
        if (acitivies == null) {
            acitivies = new LinkedList<>();
            COUNTER.put(activityName, acitivies);
        }
        acitivies.add(new ActivityInfo(activity, activityList.peekLast() == null ? "null" : activityList.peekLast().get().getClass().getName()));

        activityList.add(new WeakReference<Activity>(activity));
    }

    /**
     * activity销毁时调用此方法
     *
     * @param activity
     */
    public static void onActivityDestroied(Activity activity) {
        if (activity == null) {
            return;
        }
        String activityName = activity.getClass().getName();
        LinkedList<ActivityInfo> acitivies = COUNTER.get(activityName);
        if (acitivies != null) {
            Iterator<ActivityInfo> iterator = acitivies.iterator();
            while (iterator.hasNext()) {
                ActivityInfo item = iterator.next();
                if (item.wrfActivity.get() != null && item.wrfActivity.get() == activity) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (activityList != null) {
            Iterator<WeakReference<Activity>> iterator = activityList.iterator();
            while (iterator.hasNext()) {
                WeakReference<Activity> item = iterator.next();
                if (item.get() != null && item.get() == activity) {
                    iterator.remove();
                    break;
                }
            }
        }
        if (sCurrentActivity != null && sCurrentActivity.get() == activity) {
            setCurrentActivity(null);
        }
    }

    /**
     * 清除所有的activity
     */

    public static void finishAll() {
        // Log.i(TAG, "finishAll");
        for (String key : COUNTER.keySet()) {
            finishActivity(key);
        }
        COUNTER.clear();

        activityList.clear();
    }

    /**
     * 清除指定的activity
     *
     * @param activityName Class.getName
     */
    public static void finishActivity(String activityName) {
        //  Log.i(TAG, "finishActivity, ");
        List<ActivityInfo> activities = COUNTER.get(activityName);
        if (activities != null && !activities.isEmpty()) {
            for (ActivityInfo info : activities) {
                if (info.wrfActivity != null && info.wrfActivity.get() != null) {
                    info.wrfActivity.get().finish();
                }
            }
            activities.clear();
        }
    }


    public static void finishActivity(Activity activity) {
        if (activity == null) {
            return;
        }
        String activityName = activity.getClass().getName();
        List<ActivityInfo> activities = COUNTER.get(activityName);
        if (activities != null && !activities.isEmpty()) {
            for (ActivityInfo act : activities) {
                if (act != null && act.wrfActivity.get() != null) {
                    act.wrfActivity.get().finish();
                }
            }
            activities.clear();
        }
    }

    public static void cleanActivity() {
        COUNTER.clear();
    }

    public static final boolean hasActivityInStack(String activityName) {
        List<ActivityInfo> activities = COUNTER.get(activityName);
        return activities != null && !activities.isEmpty();
    }

    public static Activity getsCurrentActivity() {
        return sCurrentActivity != null ? sCurrentActivity.get() : null;
    }

    public static void setCurrentActivity(Activity currentActivity) {
        ActivityStashManager.sCurrentActivity = new WeakReference<Activity>(currentActivity);
    }

    public static List<Activity> getActivity(String activityName) {
        List<ActivityInfo> activities = COUNTER.get(activityName);
        List<Activity> list = null;
        if (activities != null && !activities.isEmpty()) {
            list = new ArrayList<>();
            for (ActivityInfo act : activities) {
                if (act != null && act.wrfActivity.get() != null) {
                    list.add(act.wrfActivity.get());
                }
            }
            return list;
        }
        return null;
    }

    public static ActivityInfo getLastActivityInfo(String currActitiyName) {
        LinkedList<ActivityInfo> activities = COUNTER.get(currActitiyName);
        if (activities == null || activities.size() == 0) {
            return null;
        }
        final ActivityInfo info = activities.getLast();
        if (info.lastActivityName == null) {
            return null;
        }
        activities = COUNTER.get(info.lastActivityName);
        if (activities == null || activities.size() == 0) {
            return null;
        }
        return activities.getLast();
    }

    public static void setLastPageId(String lastPageId) {
        mLastPageId = lastPageId;
    }

    /**
     * 将栈顶Activity弹出
     *
     * @param num 回退多少
     */
    public static void pop(int num, Class<?> hybridActivityClass) {
        if (num == 0) {
            finishUntilFirstNativePage(hybridActivityClass);
            return;
        }
        if (num == 999) {
            finishUntilFirstNativeOfNextWebPage(hybridActivityClass);
            return;
        }
        if (num > 0 && activityList.size() > num) {
            int size = activityList.size();
            for (int i = size - 1; i >= size - num; i--) {
                Activity currentAct = activityList.get(i).get();
                if (currentAct != null && currentAct.getClass() == hybridActivityClass) {
                    finishLastActivity();
                } else {
                    return;
                }
            }
        }
    }

    /**
     * 将栈顶Activity弹出
     *
     * @param num 回退多少
     */
    public static void popActivity(int num) {
        if (num < 0) {
            return;
        }
        if (num == 0) {
            getsCurrentActivity().finish();
            return;
        }
        if (activityList.size() > num) {
            int size = activityList.size();
            for (int i = size - 1; i >= size - num; i--) {
                Activity currentAct = activityList.get(i).get();
                if (currentAct != null) {
                    currentAct.finish();
                } else {
                    return;
                }
            }
        }
    }


    private static void finishUntilFirstNativePage(Class<?> hybridActivity) {
        if (activityList != null) {
            int size = activityList.size();
            for (int i = size - 1; i >= 0; i--) {
                Activity currentAct = activityList.get(i).get();
                if (currentAct != null && currentAct.getClass() == hybridActivity) {
                    finishLastActivity();
                } else {
                    return;
                }
            }
        }
    }

    private static void finishUntilFirstNativeOfNextWebPage(Class<?> hybridActivity) {
        if (activityList != null) {
            int size = activityList.size();
            for (int i = size - 1; i > 0; i--) {
                Activity currentAct = activityList.get(i).get();
                Activity preAct = activityList.get(i - 1).get();
                if (currentAct != null && preAct != null
                        && (preAct.getClass() == currentAct.getClass())
                        && (currentAct.getClass() == hybridActivity)) {
                    finishLastActivity();
                } else {
                    break;
                }
            }
        }
    }


    /**
     * finish最近启动的activity
     */
    @SuppressLint("CheckResult")
    public static void finishLastActivity() {
        if (activityList == null || activityList.isEmpty()) {
            return;
        }
        WeakReference<Activity> weakReference = activityList.pollLast();
        LinkedList<ActivityInfo> activityInfo = COUNTER.get(weakReference.get().getClass().getName());
        ActivityInfo info = activityInfo.pollLast();
        if (null == info){
            return;
        }
        Activity activity = info.wrfActivity.get();
        if (!"MainActivity".equals(activity.getClass().getSimpleName())){
            return;
        }
        activity.finish();
    }


    public static class ActivityInfo {
        public WeakReference<Activity> wrfActivity;
        public Bundle data;
        public Class<? extends Activity> clazz;
        public String lastActivityName;

        ActivityInfo(Activity activity, String lastActivityName) {
            wrfActivity = new WeakReference<Activity>(activity);
            data = activity.getIntent().getExtras();
            clazz = activity.getClass();
            this.lastActivityName = lastActivityName;
        }

        @Override
        public String toString() {
            return "ActivityInfo{" +
                    "wrfActivity=" + wrfActivity +
                    ", data=" + data +
                    ", clazz=" + clazz +
                    ", lastActivityName='" + lastActivityName + '\'' +
                    '}';
        }
    }
}
