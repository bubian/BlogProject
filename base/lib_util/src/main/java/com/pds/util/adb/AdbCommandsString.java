package com.pds.util.adb;

/**
 * Created by MrPds
 * Data 2016.7.19
 * func adb调试命令
 */
public class AdbCommandsString {

    /**
     * 查询所有的dumpsys子命令，例如：DUMP OF SERVICE wifi:
     *      这可以执行：adb shell dumpsys wifi查看wifi相关信息。
     */

    public static final String queryAllDumpsysC = "adb shell dumpsys | grep \"DUMP OF SERVICE\"";

    /**
     * 查询所有的service可以执行的命令列表，当然可以用dumpsys来查询这些选项的信息，
     *      比如：adb shell dumpsys meminfo。
     *          adb shell dumpsys查询所有信息
     */

    public static final String queryAllServiceC = "adb shell service list";

    /**
     * 捕获app冲突
     */

    public static final String captureAppError = "adb shell dumpsys | grep myapp | grep Error";

    /**
     * adb命令跟踪ActivityStack信息：adb shell dumpsys activity
     *    或者db shell dumpsys activity activities
     *       或者adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'
     *
     */
    public static final String followActivityStack = "adb shell dumpsys activity activities | sed -En -e '/Running activities/,/Run #0/p'";

    /**
     * adb 命令模拟按键事件
     * 这条命令相当于按了设备的BackKey键,4代表返回键的键值
     */
    public static final String backKeyCode = "adb shell input keyevent 4";

    /**
     * 查看Java层的奔溃
     */

    public static final String javaErr = "adb logcat | grep -i \"system.err\"";

    /**
     * 查看Java层所有日志信息
     */
    public static final String javaInfo = "adb logcat -v process";

    /**
     * 屏幕录制
     */
    public static final String screenecord  = "adb shell screenrecord /sdcard/myscreenrecord.mp4";


    /**
     * 要打印设备上次充电后某个给定应用软件包的电池使用情况统计信息
     */

    public static final String batterystats  = "adb shell dumpsys batterystats --charged <package-name>";

}
