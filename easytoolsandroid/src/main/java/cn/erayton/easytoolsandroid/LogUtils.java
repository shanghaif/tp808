package cn.erayton.easytoolsandroid;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

/**
 *
 * 日志封装
 */

public class LogUtils {
    //  0. logFormat, 1. className, 2. lineNumber, 3. methodName, 4. log
    private static String logFormat = "%2$s %3$d ================ %1$s:%4$s" ;
//    private static String logFormat = "%s%d================%s:%s" ;
    private static String className;//类名
    private static String methodName;//方法名
    private static int lineNumber;//行数

    /**
     * 判断是否可以调试
     * @return
     */
    public static boolean isDebuggable() {
        return BuildConfig.DEBUG;
    }

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


    private static String createLog(String log, int i) {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("================");
//        buffer.append(methodName);
//        buffer.append("(").append(className).append(":").append(lineNumber).append(")================:");
//        buffer.append(log);
//        return buffer.toString();
//        return String.format(logFormat, className, lineNumber, methodName, log) ;
        return String.format(logFormat, methodName, null, lineNumber, log) ;
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
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.e(className, createLog(message));
    }

    public static void e(Throwable message){
        try {
            if (!isDebuggable())
                return;
            getMethodNames(new Throwable().getStackTrace());
            Log.e(className, createLog(exception(message)));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void i(String message){
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.i(className, createLog(message));
    }

    public static void d(String message){
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.d(className, createLog(message, 0));
    }

    public static void v(String message){
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.v(className, createLog(message));
    }

    public static void w(String message){
        if (!isDebuggable())
            return;
        getMethodNames(new Throwable().getStackTrace());
        Log.w(className, createLog(message));
    }

    /**
     * 将异常信息转化成字符串
     *
     * @param t
     * @return
     * @throws IOException
     */
    public static String exception(Throwable t) throws IOException {
        if (t == null)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            t.printStackTrace(new PrintStream(baos));
        } finally {
            baos.close();
        }
        return baos.toString();
    }
}
