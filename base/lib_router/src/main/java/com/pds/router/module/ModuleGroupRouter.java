package com.pds.router.module;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/24 10:09 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 分组名称必须和module所在的文件夹名称相同，比如business目录下的module使用的路由，路由组名要用business，不然跳转失败，找不到对应的组。
 * 如果在相同的目录下，可以不用。
 */
public class ModuleGroupRouter {
    private static final String LIB = "/base";
    public static final String HYBRID = LIB + "/hybrid";

    private static final String REACT = "/react";
    public static final String RN_ACTIVITY = REACT + "/rnActivity";
    public static final String RN_FRAGMENT = REACT + "/rnFragment";

    private static final String FLUTTER = "/flutter";
    public static final String FLUTTER_ACTIVITY = FLUTTER + "/flutterActivity";
    public static final String FLUTTER_FRAGMENT = FLUTTER + "/flutterFragment";

    private static final String BUSINESS = "/business";
    public static final String ADDRESS_FIND = BUSINESS + "/addressFind";

    private static final String LIB_STUDY = "/study";
    public static final String UI_STUDY = LIB_STUDY + "/uiStudy";
    public static final String UI_AUDIO_RECORD = LIB_STUDY + "/audioRecord";

    private static final String LIB_PLUGIN = "/plugin";
    public static final String PLUGIN_PHONE_PROXY = LIB_PLUGIN + "/proxy";
    public static final String PLUGIN_ACTIVITY_REPLACE = LIB_PLUGIN + "/activityReplace";

    // 其它
    public static final String LINK_HYBRID = "/link/hybrid";
}
