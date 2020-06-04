package com.pds.kotlin.study.recorder;

public interface DatabaseChangedListener {
    void onNewDatabaseEntryAdded();
    void onDatabaseEntryRenamed();
}