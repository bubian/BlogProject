package blog.pds.com.three.glide.util;/**
 * Created by kuiwen on 2015/10/28.
 */

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * @author <a href="mailto:zhangkuiwen@medlinker.net">Kuiwen.Zhang</a>
 * @version 1.0
 * @description 功能描述
 * @time 2015/10/28 11:47
 **/
class ImageUtil {

    /**
     * url已check过的标识
     */
    private static String ANCHOR_TAG = "#MED_IMG_CHECKED";

    private static String QINIU_SCHEME = "imgs.medlinker.net";//七牛图片
    private static String QINIU_SCHEME_TEST = "glb";//七牛图片
    private static String UPYUN_SCHEME = "s[0-9].medlinker.net";//upyun图片
    private static String MEDLINKER_SCHEME = "temp";//medlinker图片


    /**
     * 基于原图大小，按指定百分比缩放。Scale取值范围1-999。
     *
     * @param url
     * @return
     */
    static String checkUrl(String url, int scale) {
        if (scale < 1 || scale > 999 || TextUtils.isEmpty(url) || hasChecked(url) || !url.startsWith("http") || url.contains(MEDLINKER_SCHEME)) {
            return url;
        }
        if (url.contains(QINIU_SCHEME_TEST) || url.contains(QINIU_SCHEME)) {
            url = url.concat(url.endsWith("?") ? "imageMogr2/thumbnail/" : "?imageMogr2/thumbnail/")
                    .concat("!" + scale + "p").concat("/interlace/1");
        }
        return url.concat(ANCHOR_TAG);
    }

    /**
     * 检查url来源
     *
     * @param url
     * @return
     */
    static String checkUrl(String url, int width, int height) {
        if (TextUtils.isEmpty(url) || hasChecked(url) || !url.startsWith("http") || url.contains(MEDLINKER_SCHEME)) {
            return url;
        }
        if (url.contains(QINIU_SCHEME_TEST) || url.contains(QINIU_SCHEME)) {
            url = handleQiNiu(url, width, height);
        } else if (Pattern.compile(UPYUN_SCHEME).matcher(url).find()) {
            url = handleUpYun(url, width);
        }
        //添加已check过的标识。
        return url.concat(ANCHOR_TAG);
    }

    static boolean hasChecked(String url) {
        return TextUtils.isEmpty(url) || url.endsWith(ANCHOR_TAG);
    }

    /**
     * 七牛缩略图
     *
     * @param url
     * @param width
     * @param height
     * @return
     */
    private static String handleQiNiu(String url, int width, int height) {
        if (width == 0 || height == 0) {
            return url;
        }
        if (url.endsWith("?")) {
            return url.concat("imageMogr2/thumbnail/").concat("!" + width + "x" + height + "r").concat("/interlace/1");
        } else {
            return url.concat("?imageMogr2/thumbnail/").concat("!" + width + "x" + height + "r").concat("/interlace/1");
        }
    }

    /**
     * upanyun缩略图
     *
     * @param url
     * @param width
     * @return
     */
    private static String handleUpYun(String url, int width) {
        if (rangeInDefined(width, 0, 75)) {
            return url + "!60";
        } else if (rangeInDefined(width, 76, 105)) {
            return url + "!90";
        } else if (rangeInDefined(width, 106, 360)) {
            return url + "!128";
        } else if (rangeInDefined(width, 361, 680)) {
            return url + "!680";
        } else {
            return url + "!mx680";
        }
    }

    private static boolean rangeInDefined(int current, int min, int max) {
        return Math.max(min, current) == Math.min(current, max);
    }


}
