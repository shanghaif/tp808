package cn.erayton.easytoolsandroid;

import java.util.Calendar;

public class DateTimeUtils {
    private Calendar calendar = Calendar.getInstance() ;


    /**
     * 默认构造方法
     */
    public DateTimeUtils() {

    }

    /**
     * long milliseconds
     * */
    public DateTimeUtils(long milliseconds) {
        calendar.setTimeInMillis(milliseconds);
    }




}
