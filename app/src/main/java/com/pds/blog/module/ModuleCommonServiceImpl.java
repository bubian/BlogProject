package com.pds.blog.module;

import com.pds.m2app.ModuleCommonService;

import io.github.prototypez.appjoint.core.ServiceProvider;

@ServiceProvider
public class ModuleCommonServiceImpl implements ModuleCommonService {

    @Override
    public String test(String invoke) {
        return invoke + "————————————>app";
    }
}
