package com.pds.hilt;

import javax.inject.Inject;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/16 2:00 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 */
public class AnalyticsAdapter {

    private final AnalyticsService service;

    @Inject
    AnalyticsAdapter(AnalyticsService service) {
        this.service = service;
    }
}
