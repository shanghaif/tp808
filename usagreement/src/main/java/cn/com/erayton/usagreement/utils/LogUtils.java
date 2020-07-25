package cn.com.erayton.usagreement.utils;

import android.util.Log;

import cn.com.erayton.usagreement.BuildConfig;

/**
 *
 * 日志封装
 */

public class LogUtils {
    //  0. logFormat, 1. className, 2. lineNumber, 3. methodName, 4. log
    private static String logFormat = "%2$s %3$d ====== %1$s:%4$s" ;
//    private static String log Format = "%s%d================%s:%s" ;
    private static String className;    //  类名
    private static String methodName;   //  方法名
    private static int lineNumber;      //  行数

    /**
     * 判断是否可以调试
     * @return
     */
    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

//    public static boolean isDebuggable() {
//        return false;
//    }


    private static String createLog(String log) {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("================");
//        buffer.append(methodName);
//        buffer.append("(").append(className).append(":").append(lineNumber).append(")================:");
//        buffer.append(log);
//        return buffer.toString();
//        return String.format(logFormat, className, lineNumber, methodName, log) ;
        return String.format(logFormat, methodName, className, lineNumber, log) ;
    }

    /**
     * 获取文件名、方法名、所在行数
     * @param sElements
     */
    private static void getMethodNames(StackTraceElement[] sElements){
        className = sElements[1].getFileName();
        methodName = sElements[1].getMethodName();
        lineNumber = sElements[1].getLineNumber();
    }

    public static void e(String message){
        getMethodNames(new Throwable().getStackTrace());
        e(className, message);
    }

    public static void e(String tag, String message){
        if (!isDebuggable())
            return;
        Log.e(tag, createLog(message));
    }

//    public static void e(String tag, String message){
//        Log.e(tag, createLog(message));
//    }
//    public static void e(String message){
//        if (!isDebuggable())
//            return;
//        getMethodNames(new Throwable().getStackTrace());
//        Log.e(className, createLog(message));
//    }

    public static void i(String message){
        getMethodNames(new Throwable().getStackTrace());
        i(className, message);
    }

    public static void i(String tag, String message){
        if (!isDebuggable())
            return;
//        getMethodNames(new Throwable().getStackTrace());
        Log.i(tag, createLog(message));
    }

//    public static void i(String tag, String message){
//        Log.i(tag, createLog(message));
//    }
//    public static void i(String message){
//        if (!isDebuggable())
//            return;
//        getMethodNames(new Throwable().getStackTrace());
//        Log.i(className, createLog(message));
//    }


    public static void d(String message){
        getMethodNames(new Throwable().getStackTrace());
        d(className, message);
    }

    public static void d(String tag, String message){
        if (!isDebuggable())
            return;
//        getMethodNames(new Throwable().getStackTrace());
        Log.d(tag, createLog(message));
    }

//    public static void d(String tag, String message){
//        Log.d(tag, createLog(message));
//    }


    public static void v(String message){
        getMethodNames(new Throwable().getStackTrace());
        v(className, message);
    }

    public static void v(String tag, String message){
        if (!isDebuggable())
            return;
//        getMethodNames(new Throwable().getStackTrace());
        Log.v(tag, createLog(message)) ;
    }


    public static void w(String message){
        getMethodNames(new Throwable().getStackTrace());
        w(className, message);
    }

    public static void w(String tag, String message){
        if (!isDebuggable())
            return;
        Log.w(tag, createLog(message));
    }




}
