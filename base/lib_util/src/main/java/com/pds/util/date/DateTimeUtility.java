package com.pds.util.date;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Author: KindyFung.
 * CreateTime:  2016/5/26 17:30
 * Email：fangjing@medlinker.com.
 * Description: 创建时间选择器工具类
 */
public class DateTimeUtility {

	public static final long MILLISECONDS_PER_DAY = 86400000L;
	public static final long MILLISECONDS_PER_HOUR = 3600000L;
	public static final long MILLISECONDS_PER_MIN = 60000L;
	public static final long MILLISECONDS_PER_SECOND = 1000L;
	private static int _localTimeZoneOffsetInMilliseconds = TimeZone
			.getDefault().getRawOffset();
	private static final String _standardFormat = "yyyy-MM-dd HH:mm:ss";
	private static final String _standardFormat_ms = "yyyy-MM-dd HH:mm:ss.SSS";

	/**
	 * 转换日期成日历
	 * 
	 * @param paramDate
	 * @return
	 */
	public static Calendar convertDateToCalendar(Date paramDate) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.setTime(paramDate);
		return localCalendar;
	}

	/**
	 * 转换本地到世界时间
	 * 
	 * @param paramDate
	 * @return
	 */
	public static Date convertLocalToUtc(Date paramDate) {
		return new Date(paramDate.getTime()
				- _localTimeZoneOffsetInMilliseconds);
	}

	/**
	 * 转换字符串成国内日期
	 * 
	 * @param paramString1
	 * @param paramString2
	 * @return
	 */
	private static Date convertStringToDateInternal(String paramString1,
			String paramString2) {
		SimpleDateFormat localSimpleDateFormat = new SimpleDateFormat(
				paramString2);
		try {
			Date localDate = localSimpleDateFormat.parse(paramString1);
			return localDate;
		} catch (ParseException localParseException) {
			
		}
		return null;
	}

	/**
	 * 转换国际时间成本地
	 * 
	 * @param paramDate
	 * @return
	 */
	public static Date convertUtcToLocal(Date paramDate) {
		return new Date(paramDate.getTime()
				+ _localTimeZoneOffsetInMilliseconds);
	}

	public static Date covertStringFromUtcStringDataToGmtDate(String paramString) {
		return convertUtcToLocal(covertStringToDate(paramString));
	}

	/**
	 * 转换字符串成日期
	 * 
	 * @param paramString
	 * @return
	 */
	public static Date covertStringToDate(String paramString) {
		if (TextUtils.isEmpty(paramString)) {
			return null;
		}
		return convertStringToDateInternal(paramString, "yyyy-MM-dd HH:mm:ss");
	}

	/**
	 * 转换字符串成日期
	 * 
	 * @param paramString1
	 * @param paramString2
	 * @return
	 */
	public static Date covertStringToDate(String paramString1,
			String paramString2) {
		if (TextUtils.isEmpty(paramString1)) {
			return null;
		}
		return convertStringToDateInternal(paramString1, paramString2);
	}

	public static Date covertStringToDateWithMs(String paramString) {
		int i = paramString.length();
		if ((TextUtils.isEmpty(paramString)) && (i <= 4)) {
			return null;
		}
		if (paramString.charAt(i - 4) == '.') {
			return convertStringToDateInternal(paramString,
					"yyyy-MM-dd HH:mm:ss.SSS");
		}
		return convertStringToDateInternal(paramString, "yyyy-MM-dd HH:mm:ss");
	}

	public static long getDateDiffInDays(Date paramDate1, Date paramDate2) {
		return getDateDiffInMilliSeconds(paramDate1, paramDate2) / 86400000L;
	}

	public static long getDateDiffInHours(Date paramDate1, Date paramDate2) {
		return getDateDiffInMilliSeconds(paramDate1, paramDate2) / 3600000L;
	}

	public static long getDateDiffInMilliSeconds(Date paramDate1,
			Date paramDate2) {
		Calendar localCalendar1 = convertDateToCalendar(paramDate1);
		Calendar localCalendar2 = convertDateToCalendar(paramDate2);
		return localCalendar1.getTimeInMillis()
				- localCalendar2.getTimeInMillis();
	}

	public static long getDateDiffInSeconds(Date paramDate1, Date paramDate2) {
		return getDateDiffInMilliSeconds(paramDate1, paramDate2) / 1000L;
	}

	public static String getDateTimeString(Date paramDate) {
		return getDateTimeString(paramDate, "yyyy-MM-dd HH:mm:ss");
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateTimeString(Date paramDate, String paramString) {
		return new SimpleDateFormat(paramString).format(paramDate);
	}

	public static String getDateTimeStringWithMs(Date paramDate) {
		return getDateTimeString(paramDate, "yyyy-MM-dd HH:mm:ss.SSS");
	}

	public static int getDaysOfCurrentMonth() {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.set(Calendar.DATE, 1);
		localCalendar.roll(Calendar.DATE, -1);
		return localCalendar.get(Calendar.DATE);
	}

	public static int getDaysOfYearMonth(int paramInt1, int paramInt2) {
		Calendar localCalendar = Calendar.getInstance();
		localCalendar.set(Calendar.YEAR, paramInt1);
		localCalendar.set(Calendar.MONTH, paramInt2 - 1);
		localCalendar.set(Calendar.DATE, 1);
		localCalendar.roll(Calendar.DATE, -1);
		return localCalendar.get(Calendar.DATE);
	}

	public static String mills2dateString(long timeMillis,String paramString){
		Date date=new Date(timeMillis);
		SimpleDateFormat formatter=new SimpleDateFormat(paramString, Locale.getDefault());
		return formatter.format(date);
	}

    /**
     * 两个时间戳是否是同一天 时间戳是long型的（11或者13）
     */
    public static boolean isSameData(String currentTime, String lastTime) {
        try {
            Calendar nowCal = Calendar.getInstance();
            Calendar dataCal = Calendar.getInstance();
            SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
            Long nowLong = new Long(currentTime);
            Long dataLong = new Long(lastTime);
            String data1 = df1.format(nowLong);
            String data2 = df2.format(dataLong);
            Date now = df1.parse(data1);
            Date date = df2.parse(data2);
            nowCal.setTime(now);
            dataCal.setTime(date);
            return isSameDay(nowCal, dataCal);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 != null && cal2 != null) {
            return cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA)
                    && cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                    && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
        } else {
            return false;
        }
    }
}
