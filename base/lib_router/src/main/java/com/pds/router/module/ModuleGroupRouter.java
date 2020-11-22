package com.pds.router.module;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/24 10:09 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 * 分组名重要注意事项：根据平时使用过程发现：相同"组"里定义的路由不能使用在不同module中，不然先定义的"路由"跳转失败，最后定义的可以正常跳转。
 * 比如下面定义了分组LIB = "/base"和路由HYBRID = LIB + "/hybrid"，而路由在web module中使用，如果这时候路由XXXX = LIB + "/xxx"，
 * 在除web module的其它module中使用，那么会导致跳转web module路由失败，而跳转定义路由所在的module跳转成功。
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

    private static final String LIB_S_PLUGIN = "/splugin";
    public static final String PLUGIN_PHONE_PROXY = LIB_S_PLUGIN + "/proxy";
    public static final String PLUGIN_ACTIVITY_REPLACE = LIB_S_PLUGIN + "/activityReplace";

    private static final String LIB_PLUGIN = "/plugin";
    public static final String PLUGIN_COMMERCIAL_PLUGIN = LIB_PLUGIN + "/commercial";

    private static final String LIB_GOOGLE = "/google";
    public static final String GOOGLE_VIEW_WIDGET = LIB_GOOGLE + "/viewWidget";

    // 其它
    public static final String LINK_HYBRID = "/link/hybrid";
}
