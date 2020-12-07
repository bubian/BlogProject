package com.pds.router.module;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/24 10:09 AM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 * <p>
 * 注意：分组名重要注意事项：根据平时使用过程发现：相同"组"里定义的路由不能使用在不同module中，不然先定义的"路由"跳转失败，最后定义的可以正常跳转。
 * 比如下面定义了分组LIB = "/base"和路由HYBRID = LIB + "/hybrid"，而路由在web module中使用，如果这时候路由XXXX = LIB + "/xxx"，
 * 在除web module的其它module中使用，那么会导致跳转web module路由失败，而跳转定义路由所在的module跳转成功。
 * <p>
 * 建议：所以定义路由时，组名最好以module名开头，这样避免上面的问题，而且分组可以一定程度上优化内存。
 * <p>
 * 猜测：使用ARouter需要在build.gradle文件配置"arg("AROUTER_MODULE_NAME", project.getName())",ARouter在工程编译是会扫描路由注解，
 * ARouter支持单个module独立注解，把module使用到的路由以module为单位进行保存，当使用在不同module且分组名相同时的时候，后扫描的module中路由信息覆盖前面的。
 */
public class ModuleGroupRouter {
    private static final String LIB = "/web";
    public static final String HYBRID = LIB + "/hybrid";

    private static final String REACT = "/RnModule";
    public static final String RN_ACTIVITY = REACT + "/rnActivity";
    public static final String RN_FRAGMENT = REACT + "/rnFragment";

    private static final String FLUTTER = "/FlutterModule";
    public static final String FLUTTER_ACTIVITY = FLUTTER + "/flutterActivity";
    public static final String FLUTTER_FRAGMENT = FLUTTER + "/flutterFragment";

    private static final String AL = "/AddressLookup";
    public static final String ADDRESS_FIND = AL + "/addressFind";

    private static final String LIB_STUDY = "/kotlin_study";
    public static final String UI_STUDY = LIB_STUDY + "/uiStudy";
    public static final String UI_AUDIO_RECORD = LIB_STUDY + "/audioRecord";

    private static final String LIB_S_PLUGIN = "/lib_splugin";
    public static final String PLUGIN_PHONE_PROXY = LIB_S_PLUGIN + "/proxy";
    public static final String PLUGIN_ACTIVITY_REPLACE = LIB_S_PLUGIN + "/activityReplace";

    private static final String LIB_PLUGIN = "/lib_plugin";
    public static final String PLUGIN_COMMERCIAL_PLUGIN = LIB_PLUGIN + "/commercial";

    private static final String LIB_ML = "/MotionLayout";
    public static final String GOOGLE_VIEW_WIDGET = LIB_ML + "/viewWidget";

    private static final String NDK = "/ndk";
    public static final String NDK_CHANGE_SOUND = NDK + "/changeSound";
    // 其它
    public static final String LINK_HYBRID = "/link/hybrid";
}
