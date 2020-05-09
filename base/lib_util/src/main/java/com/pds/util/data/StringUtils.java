package com.pds.util.data;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pds.util.R;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Author: KindyFung.
 * CreateTime:  2015/11/2 18:16
 * Email：fangjing@medlinker.com.
 * Description:
 */
public class StringUtils {
    private static final String TAG = StringUtils.class.getSimpleName();


    /**
     * 定义script的正则表达式
     */
    private static final String REGEX_SCRIPT = "<script[^>]*?>[\\s\\S]*?<\\/script>";
    /**
     * 定义style的正则表达式
     */
    private static final String REGEX_STYLE = "<style[^>]*?>[\\s\\S]*?<\\/style>";
    /**
     * 定义HTML标签的正则表达式
     */
    private static final String REGEX_HTML = "<[^>]+>";
    /**
     * 定义空格回车换行符
     */
    private static final String REGEX_SPACE = "\\s*|\t|\r|\n";

    public static String num2Percent(float num) {
        NumberFormat format = NumberFormat.getPercentInstance();
        format.setMinimumFractionDigits(1);
        format.setRoundingMode(RoundingMode.DOWN);
        return format.format(num);
    }


    public static String getRandomString(int length) {
        String KeyString = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        StringBuilder sb = new StringBuilder();
        int len = KeyString.length();
        for (int i = 0; i < length; i++) {
            sb.append(KeyString.charAt((int) Math.round(Math.random() * (len - 1))));
        }
        return sb.toString();
    }

    /**
     * 封装请求参数
     *
     * @param params 请求参数
     * @param encode 编码格式，例如'UTF-8'
     * @return
     */
    public static String encodeParams(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer();
        // 判断参数是否为空
        if (params != null && !params.isEmpty()) {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                try {
                    if (TextUtils.isEmpty(entry.getValue())) {
                        stringBuffer.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                    } else {
                        stringBuffer.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), encode)).append("&");
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            if (stringBuffer != null && stringBuffer.length() > 0) {
                stringBuffer.deleteCharAt(stringBuffer.length() - 1);
            }
        }
        return stringBuffer.toString();
    }

    public static String appendParams(String host, Map<String, String> params) {
        if (TextUtils.isEmpty(host) || params == null || params.isEmpty()) {
            return host;
        }
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(host);
        if (host.contains("?")) {
            sBuilder.append("&");
        } else {
            sBuilder.append("?");
        }
        for (Map.Entry<String, String> entry : params.entrySet()) {
            try {
                if (TextUtils.isEmpty(entry.getValue())) {
                    sBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
                } else {
                    sBuilder.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sBuilder.length() > 0) {
            sBuilder.deleteCharAt(sBuilder.length() - 1);
        }
        return sBuilder.toString();
    }

    /**
     * 判断是否为正确的手机号
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNo(String phone) {
        return !TextUtils.isEmpty(phone) && phone.startsWith("1") && phone.length() == 11;
    }

    public static boolean isMobileNO(String mobiles) {
        if (TextUtils.isEmpty(mobiles)) {
            return false;
        }
        Pattern p = Pattern.compile("^((1[0-9][0-9])\\d{8}$)");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 判断是否包含手机号码
     *
     * @param content
     * @return
     */
    public static boolean isContainMobileNo(String content) {
        if (TextUtils.isEmpty(content)) {
            return false;
        }
        Pattern p = Pattern.compile("((1[3-8][0-9])\\d{8})");
        Matcher m = p.matcher(content);
        return m.find();
    }


    /**
     * 隐藏手机号中间四位
     *
     * @param number 手机号码
     * @return
     */
    public static String buildHiddenMobile(String number) {
        if (null == number || number.length() < 11) {
            return number;
        }
        return number.substring(0, 3) + "****" + number.substring(7, number.length());
    }

    /**
     * 获取字符串长度
     *
     * @param value
     * @return
     */
    public static int getCharLength(String value) {
        if (TextUtils.isEmpty(value)) {
            return 0;
        }
        int valueLength = 0;
        String chineseChar = "[\u0391-\uFFE5]";
        // 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1
        for (int i = 0; i < value.length(); i++) {
            // 获取一个字符
            String temp = value.substring(i, i + 1);
            // 判断是否为中文字符
            if (temp.matches(chineseChar)) {
                // 中文字符长度为2
                valueLength += 2;
            } else {
                // 非中文字符长度为1
                valueLength += 1;
            }
        }
        return valueLength;
    }

    /**
     * 判断是否包含中文
     *
     * @param value
     * @return
     */
    public static boolean hasChineseChar(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        String chineseChar = "[\u0391-\uFFE5]";
        for (int i = 0; i < value.length(); i++) {
            String temp = value.substring(i, i + 1);
            if (temp.matches(chineseChar)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 对EditText字数的限制
     *
     * @param context Context
     * @param num     输入限定字数
     * @return InputFilter[]
     */
    public static InputFilter[] lengthFilter(Context context, final int num) {
        final Context ctx = context.getApplicationContext();
        return new InputFilter[]{new InputFilter.LengthFilter(50) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String text = dest.toString() + source.toString();
                try {
                    if (text.getBytes("GBK").length > num) {
                        int destBytesLength = dest.toString().getBytes("GBK").length;
                        // 截取source
                        return source.toString().substring(0, (num - destBytesLength + 1) / 2);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return source;
            }
        }
        };
    }

    /**
     * 判断字符串是否为手机号
     *
     * @param str 字符串
     * @return 是否为手机号
     */
    public static boolean isMobile(String str) {
        return str.matches("^((1[358][0-9])|(14[57])|(17[0678]))\\d{8}$");
    }


    /**
     * 判断是否为密码，不能包含特殊字符，空格、制表符、中文
     *
     * @param value 字符串，不能包含特殊字符，空格、制表符、中文
     * @return 是否为密码
     */
    public static boolean isPassword(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }
        return !((value.contains(" ") || value.contains("\t") || value.contains("\n") || value.contains("\r")) || hasChineseChar(value));
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    /**
     * 判断字符串是否为字母
     */
    public static boolean isLetter(String str) {
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(str);

        return m.matches();
    }

    public static String[] getSearchKeyWords(String key) {
        String[] keys = new String[]{key};
        if (key.contains(",")) {
            keys = key.split(",");
        } else if (key.contains(" ")) {
            keys = key.split(" ");
        } else if (key.contains("、")) {
            keys = key.split("、");
        }
        return keys;
    }

    public static void copyToClipboard(Context context, CharSequence str) {
        ClipboardManager clipboard = (ClipboardManager)
                context.getSystemService(Context.CLIPBOARD_SERVICE);
        // Creates a new text clip to put on the clipboard
        ClipData clip = ClipData.newPlainText("simple text", str);
        // Set the clipboard's primary clip.
        clipboard.setPrimaryClip(clip);
    }

    private static class NoLineClickSpan extends android.text.style.ClickableSpan {
        int color;

        public NoLineClickSpan(int color) {
            super();
            this.color = color;
        }


        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(color);
            ds.setUnderlineText(false); //去掉下划线
        }

        @Override
        public void onClick(View widget) {
        }
    }

    public static void setSpan(final TextView textView, String text) {

        Spannable span = (Spannable) Html.fromHtml(text);
        span.setSpan(new NoLineClickSpan(textView.getCurrentTextColor()), 0, span.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        textView.setText(span);
        textView.setMovementMethod(android.text.method.LinkMovementMethod.getInstance());
    }

    /**
     * 获取人民币价格的字符串
     *
     * @param context Context
     * @param price   价格
     * @return 转换后的价格，格式：￥15.88
     */
    public static String getPrice(Context context, int price) {
        return context.getString(R.string.price_rmb_format, convertPrice(context, price));
    }

    /**
     * 转换价格
     *
     * @param context Context
     * @param price   价格
     * @return 转换后的价格，格式：15.88
     */
    public static String convertPrice(Context context, int price) {
        checkContext(context);
        return context.getString(R.string.price_inner_convert, (price * 0.01d));
    }

    private static void checkContext(Context context) {
        if (null == context) {
            throw new NullPointerException("context can not be null.");
        }
    }


    /**
     * 对EditText字数的限制
     *
     * @param context Context
     * @param num     输入限定字数
     * @return InputFilter[]
     */
    public static InputFilter[] lengthFilter(final Context context, final int num, final String tips) {
        return new InputFilter[]{new InputFilter.LengthFilter(50) {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                String text = dest.toString() + source.toString();
                try {
                    if (text.getBytes("GBK").length > num) {
                        if (!TextUtils.isEmpty(tips)) {
                            Toast.makeText(context.getApplicationContext(), tips, Toast.LENGTH_LONG).show();
                        }
                        int destBytesLength = dest.toString().getBytes("GBK").length;
                        // 截取source
                        return source.toString().substring(0, (num - destBytesLength + 1) / 2);
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return source;
            }
        }
        };
    }

    /**
     * 去读raw 文件
     *
     * @param resId
     * @return
     */
    public static String readFromRaw(Context context, int resId) {
        String result = "";
        try {
            InputStream input = context.getResources().openRawResource(resId);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = input.read(buffer)) != -1) {
                output.write(buffer, 0, length);
            }
            output.close();
            input.close();
            return output.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 判断一个string是否只包含空格
     */
    public static boolean isAllBlankSpace(String str) {
        for (int i = 0; i < str.length(); i++) {
            char value = str.charAt(i);
            if (!(" ".equals(str.substring(i, i + 1)) || value == '\n')) {
                return false;
            }
        }

        return true;
    }

    public static String unicodeToString(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }

        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            ch = (char) Integer.parseInt(matcher.group(2), 16);
            str = str.replace(matcher.group(1), ch + "");
        }
        return str;
    }


    /**
     * 拼接统计事件
     *
     * @param event
     * @return
     */
    public static String buildEvent(String event) {
        return buildEvent(event, 0);
    }

    public static String buildEvent(String event, long id) {
        return buildEvent(event, id, null);
    }

    public static String buildEvent(String event1, long id, String event2) {
        StringBuilder stringBuilder = new StringBuilder("btn:");
        if (!TextUtils.isEmpty(event1))
            stringBuilder.append("/").append(event1);
        if (id > 0) stringBuilder.append("/").append(id);
        if (!TextUtils.isEmpty(event2))
            stringBuilder.append("/").append(event2);
        return stringBuilder.toString();
    }

    public static String buildPage(String page) {
        return new StringBuilder("page:/").append(page).toString();
    }

    public static String buildPage(String page, long id) {
        return new StringBuilder("page:/")
                .append(page)
                .append("/")
                .append(id)
                .toString();
    }

    public static String delHTMLTag(String htmlStr) {
        if (htmlStr == null || htmlStr.length() == 0) {
            return htmlStr;
        }
        // 过滤script标签
        Pattern p_script = Pattern.compile(REGEX_SCRIPT, Pattern.CASE_INSENSITIVE);
        Matcher m_script = p_script.matcher(htmlStr);
        htmlStr = m_script.replaceAll("");
        // 过滤style标签
        Pattern p_style = Pattern.compile(REGEX_STYLE, Pattern.CASE_INSENSITIVE);
        Matcher m_style = p_style.matcher(htmlStr);
        htmlStr = m_style.replaceAll("");
        // 过滤html标签
        Pattern p_html = Pattern.compile(REGEX_HTML, Pattern.CASE_INSENSITIVE);
        Matcher m_html = p_html.matcher(htmlStr);
        htmlStr = m_html.replaceAll("");
        // 过滤空格回车标签
        Pattern p_space = Pattern.compile(REGEX_SPACE, Pattern.CASE_INSENSITIVE);
        Matcher m_space = p_space.matcher(htmlStr);
        htmlStr = m_space.replaceAll("");
        return htmlStr.trim();
    }

    public static boolean isEmptyOrNull(String str) {
        return null == str || str.equals("");

    }

    public static boolean isEmpty(String str) {
        return isEmptyOrNull(str) || str.trim().equals("") || "null".equalsIgnoreCase(str.trim());
    }

    /**
     *
     */
    public static String subZeroAndDot(float f) {
        return subZeroAndDot(String.valueOf(f));
    }

    /**
     * 使用java正则表达式去掉多余的.与0
     */
    public static String subZeroAndDot(String s) {
        if (s.indexOf(".") > 0) {
            s = s.replaceAll("0+?$", ""); //去掉多余的0
            s = s.replaceAll("[.]$", ""); //如最后一位是.则去掉
        }
        return s;
    }

    public static String addExtra(String url, String str) {
        if (url.endsWith("?")) {
            return url.concat("extra=").concat(str);
        } else if (url.contains("?")) {
            return url.concat("&extra=").concat(str);
        } else {
            return url.concat("?extra=").concat(str);
        }
    }

    public static boolean isStrictEmpty(String s) {
        return s == null || "".equals(s) || "null".equalsIgnoreCase(s);
    }

    /**
     * 判断字符串是否为null或全为空格
     *
     * @param string 待校验字符串
     * @return {@code true}: null或全空格<br> {@code false}: 不为null且不全空格
     */
    public static boolean isSpace(String string) {
        return (string == null || string.trim().length() == 0);
    }

    /**
     * 判断字符串是否为null或长度为0
     *
     * @param string 待校验字符串
     * @return {@code true}: 空<br> {@code false}: 不为空
     */
    public static boolean isEmpty(CharSequence string) {
        return string == null || string.length() == 0;
    }

    /**
     * null转为长度为0的字符串
     *
     * @param string 待转字符串
     * @return string为null转为长度为0字符串，否则不改变
     */
    public static String null2Length0(String string) {
        return string == null ? "" : string;
    }

    /**
     * 返回字符串长度
     *
     * @param string 字符串
     * @return null返回0，其他返回自身长度
     */
    public static int length(CharSequence string) {
        return string == null ? 0 : string.length();
    }

    /**
     * 首字母大写
     *
     * @param string 待转字符串
     * @return 首字母大写字符串
     */
    public static String upperFirstLetter(String string) {
        if (isEmpty(string) || !Character.isLowerCase(string.charAt(0))) {
            return string;
        }
        return String.valueOf((char) (string.charAt(0) - 32)) + string.substring(1);
    }

    /**
     * 首字母小写
     *
     * @param string 待转字符串
     * @return 首字母小写字符串
     */
    public static String lowerFirstLetter(String string) {
        if (isEmpty(string) || !Character.isUpperCase(string.charAt(0))) {
            return string;
        }
        return String.valueOf((char) (string.charAt(0) + 32)) + string.substring(1);
    }

    /**
     * 转化为半角字符
     *
     * @param string 待转字符串
     * @return 半角字符串
     */
    public static String toDBC(String string) {
        if (isEmpty(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == 12288) {
                chars[i] = ' ';
            } else if (65281 <= chars[i] && chars[i] <= 65374) {
                chars[i] = (char) (chars[i] - 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * 转化为全角字符
     *
     * @param string 待转字符串
     * @return 全角字符串
     */
    public static String toSBC(String string) {
        if (isEmpty(string)) {
            return string;
        }
        char[] chars = string.toCharArray();
        for (int i = 0, len = chars.length; i < len; i++) {
            if (chars[i] == ' ') {
                chars[i] = (char) 12288;
            } else if (33 <= chars[i] && chars[i] <= 126) {
                chars[i] = (char) (chars[i] + 65248);
            } else {
                chars[i] = chars[i];
            }
        }
        return new String(chars);
    }

    /**
     * is null or its length is 0 or it is made by space
     * <p>
     * <pre>
     * isBlank(null) = true;
     * isBlank(&quot;&quot;) = true;
     * isBlank(&quot;  &quot;) = true;
     * isBlank(&quot;a&quot;) = false;
     * isBlank(&quot;a &quot;) = false;
     * isBlank(&quot; a&quot;) = false;
     * isBlank(&quot;a b&quot;) = false;
     * </pre>
     *
     * @param str str
     * @return if string is null or its size is 0 or it is made by space, return
     * true, else return false.
     */
    public static boolean isBlank(String str) {

        return (str == null || str.trim().length() == 0);
    }


    /**
     * null Object to empty string
     * <p>
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     *
     * @param str str
     * @return String
     */
    public static String nullStrToEmpty(Object str) {

        return (str == null
                ? ""
                : (str instanceof String ? (String) str : str.toString()));
    }


    /**
     * @param str str
     * @return String
     */
    public static String capitalizeFirstLetter(String str) {

        if (isEmpty(str)) {
            return str;
        }

        char c = str.charAt(0);
        return (!Character.isLetter(c) || Character.isUpperCase(c))
                ? str
                : new StringBuilder(str.length()).append(
                Character.toUpperCase(c))
                .append(str.substring(1))
                .toString();
    }


    /**
     * encoded in utf-8
     *
     * @param str 字符串
     * @return 返回一个utf8的字符串
     */
    public static String utf8Encode(String str) {

        if (!isEmpty(str) && str.getBytes().length != str.length()) {
            try {
                return URLEncoder.encode(str, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(
                        "UnsupportedEncodingException occurred. ", e);
            }
        }
        return str;
    }


    /**
     * @param href 字符串
     * @return 返回一个html
     */
    public static String getHrefInnerHtml(String href) {

        if (isEmpty(href)) {
            return "";
        }

        String hrefReg = ".*<[\\s]*a[\\s]*.*>(.+?)<[\\s]*/a[\\s]*>.*";
        Pattern hrefPattern = Pattern.compile(hrefReg,
                Pattern.CASE_INSENSITIVE);
        Matcher hrefMatcher = hrefPattern.matcher(href);
        if (hrefMatcher.matches()) {
            return hrefMatcher.group(1);
        }
        return href;
    }


    /**
     * @param source 字符串
     * @return 返回htmL到字符串
     */
    public static String htmlEscapeCharsToString(String source) {

        return StringUtils.isEmpty(source)
                ? source
                : source.replaceAll("&lt;", "<")
                .replaceAll("&gt;", ">")
                .replaceAll("&amp;", "&")
                .replaceAll("&quot;", "\"");
    }


    /**
     * @param s str
     * @return String
     */
    public static String fullWidthToHalfWidth(String s) {

        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == 12288) {
                source[i] = ' ';
                // } else if (source[i] == 12290) {
                // source[i] = '.';
            } else if (source[i] >= 65281 && source[i] <= 65374) {
                source[i] = (char) (source[i] - 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }


    /**
     * @param s 字符串
     * @return 返回的数值
     */
    public static String halfWidthToFullWidth(String s) {

        if (isEmpty(s)) {
            return s;
        }

        char[] source = s.toCharArray();
        for (int i = 0; i < source.length; i++) {
            if (source[i] == ' ') {
                source[i] = (char) 12288;
                // } else if (source[i] == '.') {
                // source[i] = (char)12290;
            } else if (source[i] >= 33 && source[i] <= 126) {
                source[i] = (char) (source[i] + 65248);
            } else {
                source[i] = source[i];
            }
        }
        return new String(source);
    }


    /**
     * @param str 资源
     * @return 特殊字符串切换
     */

    public static String replaceBlanktihuan(String str) {

        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 判断给定的字符串是否不为null且不为空
     *
     * @param string 给定的字符串
     */
    public static boolean isNotEmpty(String string) {
        return !isEmpty(string);
    }


    /**
     * 判断给定的字符串数组中的所有字符串是否都为null或者是空的
     *
     * @param strings 给定的字符串
     */
    public static boolean isEmpty(String... strings) {
        boolean result = true;
        for (String string : strings) {
            if (isNotEmpty(string)) {
                result = false;
                break;
            }
        }
        return result;
    }


    /**
     * 判断给定的字符串数组中是否全部都不为null且不为空
     *
     * @param strings 给定的字符串数组
     * @return 是否全部都不为null且不为空
     */
    public static boolean isNotEmpty(String... strings) {
        boolean result = true;
        for (String string : strings) {
            if (isEmpty(string)) {
                result = false;
                break;
            }
        }
        return result;
    }


    /**
     * 如果字符串是null或者空就返回""
     */
    public static String filterEmpty(String string) {
        return StringUtils.isNotEmpty(string) ? string : "";
    }


    /**
     * 在给定的字符串中，用新的字符替换所有旧的字符
     *
     * @param string  给定的字符串
     * @param oldchar 旧的字符
     * @param newchar 新的字符
     * @return 替换后的字符串
     */
    public static String replace(String string, char oldchar, char newchar) {
        char chars[] = string.toCharArray();
        for (int w = 0; w < chars.length; w++) {
            if (chars[w] == oldchar) {
                chars[w] = newchar;
                break;
            }
        }
        return new String(chars);
    }


    /**
     * 把给定的字符串用给定的字符分割
     *
     * @param string 给定的字符串
     * @param ch     给定的字符
     * @return 分割后的字符串数组
     */
    public static String[] split(String string, char ch) {
        ArrayList<String> stringList = new ArrayList<String>();
        char chars[] = string.toCharArray();
        int nextStart = 0;
        for (int w = 0; w < chars.length; w++) {
            if (ch == chars[w]) {
                stringList.add(new String(chars, nextStart, w - nextStart));
                nextStart = w + 1;
                if (nextStart ==
                        chars.length) {    //当最后一位是分割符的话，就再添加一个空的字符串到分割数组中去
                    stringList.add("");
                }
            }
        }
        if (nextStart <
                chars.length) {    //如果最后一位不是分隔符的话，就将最后一个分割符到最后一个字符中间的左右字符串作为一个字符串添加到分割数组中去
            stringList.add(new String(chars, nextStart,
                    chars.length - 1 - nextStart + 1));
        }
        return stringList.toArray(new String[stringList.size()]);
    }


    /**
     * 计算给定的字符串的长度，计算规则是：一个汉字的长度为2，一个字符的长度为1
     *
     * @param string 给定的字符串
     * @return 长度
     */
    public static int countLength(String string) {
        int length = 0;
        char[] chars = string.toCharArray();
        for (int w = 0; w < string.length(); w++) {
            char ch = chars[w];
            if (ch >= '\u0391' && ch <= '\uFFE5') {
                length++;
                length++;
            } else {
                length++;
            }
        }
        return length;
    }


    private static char[] getChars(char[] chars, int startIndex) {
        int endIndex = startIndex + 1;
        //如果第一个是数字
        if (Character.isDigit(chars[startIndex])) {
            //如果下一个是数字
            while (endIndex < chars.length &&
                    Character.isDigit(chars[endIndex])) {
                endIndex++;
            }
        }
        char[] resultChars = new char[endIndex - startIndex];
        System.arraycopy(chars, startIndex, resultChars, 0, resultChars.length);
        return resultChars;
    }


    /**
     * 是否全是数字
     */
    public static boolean isAllDigital(char[] chars) {
        boolean result = true;
        for (int w = 0; w < chars.length; w++) {
            if (!Character.isDigit(chars[w])) {
                result = false;
                break;
            }
        }
        return result;
    }


    /**
     * 删除给定字符串中所有的旧的字符
     *
     * @param string 源字符串
     * @param ch     要删除的字符
     * @return 删除后的字符串
     */
    public static String removeChar(String string, char ch) {
        StringBuffer sb = new StringBuffer();
        for (char cha : string.toCharArray()) {
            if (cha != '-') {
                sb.append(cha);
            }
        }
        return sb.toString();
    }


    /**
     * 删除给定字符串中给定位置处的字符
     *
     * @param string 给定字符串
     * @param index  给定位置
     */
    public static String removeChar(String string, int index) {
        String result = null;
        char[] chars = string.toCharArray();
        if (index == 0) {
            result = new String(chars, 1, chars.length - 1);
        } else if (index == chars.length - 1) {
            result = new String(chars, 0, chars.length - 1);
        } else {
            result = new String(chars, 0, index) +
                    new String(chars, index + 1, chars.length - index);
            ;
        }
        return result;
    }


    /**
     * 删除给定字符串中给定位置处的字符
     *
     * @param string 给定字符串
     * @param index  给定位置
     * @param ch     如果同给定位置处的字符相同，则将给定位置处的字符删除
     */
    public static String removeChar(String string, int index, char ch) {
        String result = null;
        char[] chars = string.toCharArray();
        if (chars.length > 0 && chars[index] == ch) {
            if (index == 0) {
                result = new String(chars, 1, chars.length - 1);
            } else if (index == chars.length - 1) {
                result = new String(chars, 0, chars.length - 1);
            } else {
                result = new String(chars, 0, index) +
                        new String(chars, index + 1, chars.length - index);
                ;
            }
        } else {
            result = string;
        }
        return result;
    }


    /**
     * 对给定的字符串进行空白过滤
     *
     * @param string 给定的字符串
     * @return 如果给定的字符串是一个空白字符串，那么返回null；否则返回本身。
     */
    public static String filterBlank(String string) {
        if ("".equals(string)) {
            return null;
        } else {
            return string;
        }
    }


    /**
     * 将给定字符串中给定的区域的字符转换成小写
     *
     * @param str        给定字符串中
     * @param beginIndex 开始索引（包括）
     * @param endIndex   结束索引（不包括）
     * @return 新的字符串
     */
    public static String toLowerCase(String str, int beginIndex, int endIndex) {
        return str.replaceFirst(str.substring(beginIndex, endIndex),
                str.substring(beginIndex, endIndex)
                        .toLowerCase(Locale.getDefault()));
    }


    /**
     * 将给定字符串中给定的区域的字符转换成大写
     *
     * @param str        给定字符串中
     * @param beginIndex 开始索引（包括）
     * @param endIndex   结束索引（不包括）
     * @return 新的字符串
     */
    public static String toUpperCase(String str, int beginIndex, int endIndex) {
        return str.replaceFirst(str.substring(beginIndex, endIndex),
                str.substring(beginIndex, endIndex)
                        .toUpperCase(Locale.getDefault()));
    }


    /**
     * 将给定字符串的首字母转为小写
     *
     * @param str 给定字符串
     * @return 新的字符串
     */
    public static String firstLetterToLowerCase(String str) {
        return toLowerCase(str, 0, 1);
    }


    /**
     * 将给定字符串的首字母转为大写
     *
     * @param str 给定字符串
     * @return 新的字符串
     */
    public static String firstLetterToUpperCase(String str) {
        return toUpperCase(str, 0, 1);
    }

    /**
     * 判断给定的字符串是否以一个特定的字符串开头，忽略大小写
     *
     * @param sourceString 给定的字符串
     * @param newString    一个特定的字符串
     */
    public static boolean startsWithIgnoreCase(String sourceString, String newString) {
        int newLength = newString.length();
        int sourceLength = sourceString.length();
        if (newLength == sourceLength) {
            return newString.equalsIgnoreCase(sourceString);
        } else if (newLength < sourceLength) {
            char[] newChars = new char[newLength];
            sourceString.getChars(0, newLength, newChars, 0);
            return newString.equalsIgnoreCase(String.valueOf(newChars));
        } else {
            return false;
        }
    }


    /**
     * 判断给定的字符串是否以一个特定的字符串结尾，忽略大小写
     *
     * @param sourceString 给定的字符串
     * @param newString    一个特定的字符串
     */
    public static boolean endsWithIgnoreCase(String sourceString, String newString) {
        int newLength = newString.length();
        int sourceLength = sourceString.length();
        if (newLength == sourceLength) {
            return newString.equalsIgnoreCase(sourceString);
        } else if (newLength < sourceLength) {
            char[] newChars = new char[newLength];
            sourceString.getChars(sourceLength - newLength, sourceLength,
                    newChars, 0);
            return newString.equalsIgnoreCase(String.valueOf(newChars));
        } else {
            return false;
        }
    }


    /**
     * 检查字符串长度，如果字符串的长度超过maxLength，就截取前maxLength个字符串并在末尾拼上appendString
     */
    public static String checkLength(String string, int maxLength, String appendString) {
        if (string.length() > maxLength) {
            string = string.substring(0, maxLength);
            if (appendString != null) {
                string += appendString;
            }
        }
        return string;
    }


    /**
     * 检查字符串长度，如果字符串的长度超过maxLength，就截取前maxLength个字符串并在末尾拼上…
     */
    public static String checkLength(String string, int maxLength) {
        return checkLength(string, maxLength, "…");
    }
}
