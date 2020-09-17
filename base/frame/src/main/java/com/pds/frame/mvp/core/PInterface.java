package com.pds.frame.mvp.core;

public interface PInterface<V extends BaseV> {
    void attachV(V loginView);
    void detachV();
}
