package cn.com.erayton.usagreement.utils;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utils {
    private static final String TAG = "Utils" ;


    public static void print(String s){
        System.out.println(s);
    }
    public static void printArray(String[] s){
        System.out.println(s);
    }

    public static byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();

            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }

            for (int i = start; i < end - 1; i++) {
                if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
                    baos.write(0x7d);
                    i++;
                } else if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
                    baos.write(0x7e);
                    i++;
                } else {
                    baos.write(bs[i]);
                }
            }
            for (int i = end - 1; i < bs.length; i++) {
                baos.write(bs[i]);
            }
        } catch (Exception e) {
            Log.e(TAG, "doEscape4Receive: ", e);
            return null;
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        return baos.toByteArray();
    }

    public static byte[] doEscape4Send(byte[] bs, int start, int end) throws IOException {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }
            // TODO: 2017/3/31 <= ?
            for (int i = start; i < end; i++) {
                if (bs[i] == 0x7e) {
                    baos.write(0x7d);
                    baos.write(0x02);
                } else if (bs[i] == 0x7d) {
                    baos.write(0x7d);
                    baos.write(0x01);
                } else {
                    baos.write(bs[i]);
                }
            }
            for (int i = end; i < bs.length; i++) {
                baos.write(bs[i]);
            }

        } catch (Exception e) {
            Log.e(TAG, "doEscape4Send: ", e);
            return null;
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        return baos.toByteArray();
    }

    /**
     * 取小数点后六位
     *
     * */

    public static Double getSixPoint(Double d){
//        Log.d(TAG, "getSixPoint"+Double.parseDouble(String.format("%.6f", d))) ;
        return Double.parseDouble(String.format("%.6f", d)) ;
    }

    /** 状态位定义
     *
     * @param isAcc         acc 是否开
     * @param isLocation    是否有效定位
     * @param isGps         是否开启 gps 定位
     * @param isBeiDou      是否开启北斗定位
     */
    public static String getStateAssembly(boolean isAcc, boolean isLocation, boolean isGps , boolean isBeiDou){
        //        报警状态
        int acc = 1;    //  1
        int loc = 2 ;   //  10
        int nowStatus = 31 ;        //  11111

        if (isGps){
            nowStatus = nowStatus<<1 | acc ;
            if (isBeiDou){
                nowStatus = nowStatus<<1 | acc ;
            }
        }else {
            if (isBeiDou){
                nowStatus = nowStatus<<2 | loc ;
            }
        }
        //  后补 0
        nowStatus <<= 12 ;
        if (isAcc) nowStatus |= acc ;
        if (isLocation){
            //  定位成功
            nowStatus |= loc ;
        }
        //  int 类型转为二进制字符串
        return Integer.toBinaryString(nowStatus) ;
    }



    /** 报警标志位定义
     */
    public static String getWarnAssembly(){
//        //        报警状态
//        int acc = 1;    //  1
//        int loc = 2 ;   //  10
        int nowStatus = 31 ;    //  11111
//
//        if (isGps){
//            nowStatus = nowStatus<<1 | acc ;
//            if (isBeiDou){
//                nowStatus = nowStatus<<1 | acc ;
//            }
//        }else {
//            if (isBeiDou){
//                nowStatus = nowStatus<<2 | loc ;
//            }
//        }
//        //  后补 0
//        nowStatus <<= 12 ;
//        if (isAcc) nowStatus |= acc ;
//        if (isLocation){
//            //  定位成功
//            nowStatus |= loc ;
//        }

        return Integer.toBinaryString(nowStatus) ;
    }
}
