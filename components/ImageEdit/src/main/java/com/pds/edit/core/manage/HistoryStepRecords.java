package com.pds.edit.core.manage;

import android.util.Log;

import java.util.Stack;

public class HistoryStepRecords<T> {

    private static final String TAG = "HistoryStepRecords";

    private static final int maxStackSize = 8;

    private Stack<T> forwardStack = new Stack<>();
    private Stack<T> backStack = new Stack<>();
    private HistoryStepRecordsChange historyStepRecordsChange;

    public void addRecodeToForWordStack(T editorsInfo){
        if (null == editorsInfo){
            Log.e(TAG,"add failed for editorsInfo is null");
            return;
        }
        int currentStackSize = forwardStack.size();
        if (currentStackSize >= maxStackSize){
            forwardStack.remove(currentStackSize - 1);
        }
        forwardStack.push(editorsInfo);
        invoke();
    }

    public void addRecodeToBackStack(T editorsInfo){
        if (null == editorsInfo){
            Log.e(TAG,"add failed for editorsInfo is null");
            return;
        }
        int currentStackSize = backStack.size();
        if (currentStackSize >= maxStackSize){
            backStack.remove(currentStackSize - 1);
        }
        backStack.push(editorsInfo);
        invoke();
    }


    public T popForwardStack(){
        if (forwardStack.isEmpty()){
            invoke();
            return null;
        }
        T editorsInfo = forwardStack.pop();
        addRecodeToBackStack(editorsInfo);
        invoke();
        return editorsInfo;

    }

    public T popBackStack(){
        if (backStack.isEmpty()){
            invoke();
            return null;
        }
        T editorsInfo = backStack.pop();
        addRecodeToForWordStack(editorsInfo);
        invoke();
        return editorsInfo;
    }

    public void clearForwardStack(){
        if (null != forwardStack && !forwardStack.isEmpty())
        forwardStack.clear();
        invoke();
    }

    public void clearBackStack(){
        if (null != backStack && !backStack.isEmpty())
        backStack.clear();
    }

    public void clear(){
        clearBackStack();
        clearForwardStack();
        invoke();
    }

    public T getForwardStackElement(){
        if (null ==forwardStack || forwardStack.isEmpty())return null;
        return forwardStack.pop();
    }

    public interface HistoryStepRecordsChange{
        void call(boolean isBack, boolean isForward);
    }

    private void invoke(){
        if (null != historyStepRecordsChange){
            historyStepRecordsChange.call(!backStack.isEmpty(),!forwardStack.isEmpty());
        }
    }

    public void register(HistoryStepRecordsChange historyStepRecordsChange){
        this.historyStepRecordsChange = historyStepRecordsChange;

    }
}
