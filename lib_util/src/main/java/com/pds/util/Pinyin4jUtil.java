package com.pds.util;

//import net.sourceforge.pinyin4j.PinyinHelper;
//import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
//import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
//import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
//import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;
//
//import java.util.ArrayList;
//import java.util.List;

/**
 * @author <a href="mailto:ganyu@medlinker.net">ganyu</a>
 * @version 3.0
 * @description Pinyin4j工具类
 * @time 2015/11/8 13:50
 */
public class Pinyin4jUtil {
//    /**
//     * 返回拼音类型：全拼
//     */
//    public static final String RET_PINYIN_TYPE_FULL = "full";
//    /**
//     * 返回拼音类型：首字母
//     */
//    public static final String RET_PINYIN_TYPE_HEADCHAR = "headChar";
//
//    /**
//     * 将字符串转换为拼音
//     * TODO 需要处理多音字组合问题
//     *
//     * @param str 字符串
//     * @return
//     */
//    public static String getLetterString(String str) {
//        return makeStringByStringSet(str2Pinyin(str));
//    }
//
//    /**
//     * 字符串集合转换字符串(逗号分隔)
//     *
//     * @param stringSet
//     * @return
//     */
//    private static String makeStringByStringSet(List<String> stringSet) {
//        if (stringSet == null) {
//            return "";
//        }
//        StringBuilder str = new StringBuilder();
//        int i = 0;
//        for (String s : stringSet) {
//            if (i == stringSet.size() - 1) {
//                str.append(s);
//            } else {
//                str.append(s + ",");
//            }
//            i++;
//        }
//        return str.toString().toLowerCase();
//    }
//
//    private static List<String> str2Pinyin(String src) {
//        return str2Pinyin(src, null);
//    }
//
//    /**
//     * 字符串转换为拼音
//     *
//     * @param src     需要转换的字符串
//     * @param retType 返回拼音结果类型
//     * @return 如果retType为RET_PINYIN_TYPE_FULL，则返回全拼；如果retType为RET_PINYIN_TYPE_HEADCHAR;如果传入其他值，返回全拼
//     * @throws BadHanyuPinyinOutputFormatCombination
//     */
//    private static List<String> str2Pinyin(String src, String retType) {
//        if (src != null && !src.trim().equalsIgnoreCase("")) {
//            char[] srcChar = src.toCharArray();
//            // 汉语拼音格式输出类
//            HanyuPinyinOutputFormat hanYuPinOutputFormat = new HanyuPinyinOutputFormat();
//            // 输出设置，大小写，音标方式等
//            hanYuPinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
//            hanYuPinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
//            hanYuPinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
//
//            String[][] temp = new String[src.length()][];
//            for (int i = 0; i < srcChar.length; i++) {
//                try {
//                    temp[i] = PinyinHelper.toHanyuPinyinStringArray(srcChar[i], hanYuPinOutputFormat);
//
//                    if (temp[i] == null) {//如果str.charAt(i)非汉字，则保持原样
//                        temp[i] = new String[]{String.valueOf(srcChar[i])};
//                    } else {
//                        //如果retType是RET_PINYIN_TYPE_HEADCHAR，则只取转换后的首字母
//                        if (RET_PINYIN_TYPE_HEADCHAR.equalsIgnoreCase(retType)) {
//                            String[] temptemp = new String[temp[i].length];
//                            for (int j = 0; j < temp[i].length; j++) {
//                                temptemp[j] = String.valueOf(temp[i][j].charAt(0));
//                            }
//                            temp[i] = temptemp;
//                        }
//                    }
//                } catch (BadHanyuPinyinOutputFormatCombination e) {
//                    e.printStackTrace();
//                }
//            }
//            String[] pingyinArray = exchange(temp);
//            List<String> pinyinSet = new ArrayList<>();
//            for (int i = 0; i < pingyinArray.length; i++) {
//                pinyinSet.add(pingyinArray[i]);
//            }
//            return pinyinSet;
//        }
//        return null;
//    }
//
//    /**
//     * 递归
//     *
//     * @param strJaggedArray
//     * @return
//     */
//    private static String[] exchange(String[][] strJaggedArray) {
//        String[][] temp = doExchange(strJaggedArray);
//        return temp[0];
//    }
//
//    /**
//     * 递归
//     *
//     * @param strJaggedArray
//     * @return
//     */
//    private static String[][] doExchange(String[][] strJaggedArray) {
//        int len = strJaggedArray.length;
//        if (len >= 2) {
//            int len1 = strJaggedArray[0].length;
//            int len2 = strJaggedArray[1].length;
//            int newlen = len1 * len2;
//            String[] temp = new String[newlen];
//            int index = 0;
//            for (int i = 0; i < len1; i++) {
//                for (int j = 0; j < len2; j++) {
//                    temp[index] = strJaggedArray[0][i] + strJaggedArray[1][j];
//                    index++;
//                }
//            }
//            String[][] newArray = new String[len - 1][];
//            for (int i = 2; i < len; i++) {
//                newArray[i - 1] = strJaggedArray[i];
//            }
//            newArray[0] = temp;
//            return doExchange(newArray);
//        } else {
//            return strJaggedArray;
//        }
//    }
}
