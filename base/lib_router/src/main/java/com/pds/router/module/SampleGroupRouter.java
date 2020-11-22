package com.pds.router.module;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/26 1:03 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
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
public class SampleGroupRouter {
    private static final String SAMPLE = "/SampleModule";

    public static final String FILE_LOAD = SAMPLE +"/file/load";
    public static final String FIlE_X5_LOAD = SAMPLE +"/file-x5";
    public static final String PDF_LOAD = SAMPLE +"/pdf-native";
    public static final String X5_QB = SAMPLE +"/x5-qb";
    public static final String PDF_JS = SAMPLE +"/pdsJs";

    // TextView ComPat html
    public static final String SAMPLE_HOME = SAMPLE +"/home";
    public static final String TV_COMPAT_HTML = SAMPLE +"/tvCompatHtml";
    public static final String TV_HTML = SAMPLE +"/tvHtml";
}
