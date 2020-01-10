package cn.com.erayton.usagreement.utils;

public class TimeUtils {

    public static int getTime(int i) {
        return (int) (System.currentTimeMillis() % (long) 1000000000);
    }

    public static long getTime() {
        return System.currentTimeMillis();
    }
}
