package com.pds.router.module;

/**
 * @author: pengdaosong
 * @CreateTime: 2020/10/30 3:23 PM
 * @Email: pengdaosong@medlinker.com
 * @Description:
 *
 *  * 分组名重要注意事项：根据平时使用过程发现：相同"组"里定义的路由不能使用在不同module中，不然先定义的"路由"跳转失败，最后定义的可以正常跳转。
 *  * 比如下面定义了分组LIB = "/base"和路由HYBRID = LIB + "/hybrid"，而路由在web module中使用，如果这时候路由XXXX = LIB + "/xxx"，
 *  * 在除web module的其它module中使用，那么会导致跳转web module路由失败，而跳转定义路由所在的module跳转成功。
 */
public class MainGroupRouter {
    private static final String MAIN = "/main";
    public static final String HOME = MAIN + "/home";
}
