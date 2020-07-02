package cn.com.erayton.usagreement.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class TimeUtils {

    public static int getTime(int i) {
        return (int) (System.currentTimeMillis() % (long) 1000000000);
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }



    /**
     * 时间戳转换成日期格式字符串
     * @param seconds 精确到秒的字符串
     * @param format  yyMMddHHmmss
     * @return
     */
    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds+"000")));
    }

    /**
     * 日期格式字符串转换成时间戳
     * @param date_str 字符串日期
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String date2TimeStamp(String date_str,String format){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return String.valueOf(sdf.parse(date_str).getTime()/1000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 取得当前时间戳（精确到秒）
     * @return
     */
    public static String timeStamp(){
        long time = System.currentTimeMillis();
//        String t = String.valueOf(time/1000);
//        return t;
        return String.valueOf(time/1000);
    }

    /**
     * 取得当前时间
     * @param format 如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getNowTimeFormat(String format){
        return timeFormat(System.currentTimeMillis(), format);
    }

    /**
     * 获取几天前零点的时间戳
     * */
    public static String dayAgo(int day,String format){
        return dayAgo(0, day, format);
    }

    /**
     * 获取几个月前零点的时间戳
     * */
    public static String dayAgo(int month, int day, String format){
        return dayAgo(0, month, day, format);
    }

    public static String dayAgo(int year, int month, int day,String format){
        Calendar calendar = Calendar.getInstance();
//        SimpleDateFormat sdf2 = new SimpleDateFormat(format);
        calendar.add(Calendar.YEAR, year);
        calendar.add(Calendar.DAY_OF_MONTH, month);
        calendar.add(Calendar.DATE, day);
//        String three_days_after = sdf2.format(calendar2.getTime());
//        System.out.println(calendar2.getTime());
//        return sdf2.format(calendar2.getTime());
        if (format == null) {
            return String.valueOf((calendar.getTimeInMillis()/1000));   //  毫秒转为秒
        }
        return timeFormat(calendar.getTime(), format);
    }

//    private static String timeFormat(Date date, String format){
//        SimpleDateFormat f = new SimpleDateFormat(format, Locale.getDefault());
//        return f.format(date) ;
//    }

    private static String timeFormat(Object date, String format){
        SimpleDateFormat f = new SimpleDateFormat(format, Locale.getDefault());
        return f.format(date) ;
    }
}
