package cn.erayton.easytoolsandroid;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static long timeNow(){
        return System.currentTimeMillis() ;
    }

    /**    time now format
     *
     * @param format    格式
     * @return  格式化后的时间
     */
    public static String timeNowFormat(String format){
        return timeFormat(timeNow(), format) ;
    }

    /**    time format
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @return  格式化后的时间
     */
    public static String timeFormat(long timeStamp, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format, Locale.CHINA) ;
        return formatter.format(new Date(timeStamp));
    }

    /**    time place format
     *
     * @param timeStamp 时间戳
     * @param format    格式
     * @param locale    地区
     * @return  格式化后的时间
     */
    public static String timeFormat(long timeStamp, String format, Locale locale){
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale) ;
        return formatter.format(new Date(timeStamp));
    }
}
