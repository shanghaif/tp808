package cn.com.erayton.usagreement.model;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cn.com.erayton.usagreement.utils.BCD8421Operator;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.Decoder4LoggingOnly;
import cn.com.erayton.usagreement.utils.Utils;

/**
 * 终端 GPS 信息
 * */
public class TerminalGPSMsg extends PacketData {
    private String TAG = "TerminalGPSMsg" ;
    private TerminalGPSInfo terminalGPSInfo ;

    public TerminalGPSMsg() {
    }

    public TerminalGPSInfo getTerminalGPSInfo() {
        return terminalGPSInfo;
    }

    public void setTerminalGPSInfo(TerminalGPSInfo terminalGPSInfo) {
        this.terminalGPSInfo = terminalGPSInfo;
    }

    @Override
    public int getBodyLength() {
        byte[] bytes = packageDataBody2Byte() ;
        Log.e(TAG, "getBodyLength: " + bytes.length);
        return bytes.length;
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //32 位二进制 从高到低位
            Decoder4LoggingOnly decoder4LoggingOnly = new Decoder4LoggingOnly() ;
            byte[] warningMark =bitOperator.integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getWarningMark()), 2)) ;
//            decoder4LoggingOnly.decodeHex(warningMark);
            baos.write(warningMark);
            byte[] state = bitOperator.integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getState()), 2));
//            decoder4LoggingOnly.decodeHex(state);
            baos.write(state);
//            byte[] latitue = bitOperator.longToDword(terminalGPSInfo.getLatitude());
            byte[] latitue = bitOperator.longToBytes(terminalGPSInfo.getLatitude(), 4);
//            baos.write(bitOperator.integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getWarningMark()), 2)));
//            baos.write(bitOperator.integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getState()), 2)));
            Log.d(TAG, "terminalGPSInfo.getLatitude()"+terminalGPSInfo.getLatitude()) ;
            decoder4LoggingOnly.decodeHex(latitue);
            baos.write(latitue);
//            byte[] longitude = bitOperator.longToDword(terminalGPSInfo.getLongitude());
            byte[] longitude = bitOperator.longToBytes(terminalGPSInfo.getLongitude(), 4);
            Log.d(TAG, "terminalGPSInfo.getLongitude()"+terminalGPSInfo.getLongitude()) ;
             decoder4LoggingOnly.decodeHex(longitude);
            baos.write(longitude);
            byte[] altitude = bitOperator.integerTo2Bytes(terminalGPSInfo.getAltitude());
//             decoder4LoggingOnly.decodeHex(altitude);
            baos.write(altitude);
//            byte[] speed =  bitOperator.byte2Float(terminalGPSInfo.getSpeed());
            byte[] speed =  bitOperator.integerTo2Bytes(terminalGPSInfo.getSpeed());
            // decoder4LoggingOnly.decodeHex(speed);
            baos.write(speed);
            byte[] girection =  bitOperator.integerTo2Bytes(terminalGPSInfo.getDirection());
            // decoder4LoggingOnly.decodeHex(girection);
            baos.write(girection);
            byte[] bcdtime =  BCD8421Operator.getInstance().getBCDTime();
            // decoder4LoggingOnly.decodeHex(bcdtime);
            baos.write(bcdtime);


//            long lat = 22581626 ;
//            long lng = 113918790 ;
//            byte[] alarm = {0, 0, 0, 0};
//            //32 位二进制 从高到低位
//            String radix2State = "00000000000000000000000000000010";
//            //2进制转int 在装4个字节的byte
////        byte[] state = ByteUtil.int2Bytes(Integer.parseInt(radix2State, 2));
//            byte[] state = BitOperator.getInstance().integerTo4Bytes(Integer.parseInt(radix2State, 2));
//            // DWORD 经纬度
////        byte[] latb = ByteUtil.longToDword(lat);
//            byte[] latb = BitOperator.getInstance().longToDword(lat);
////        byte[] lngb = ByteUtil.longToDword(lng);
//            byte[] lngb = BitOperator.getInstance().longToDword(lng);
//            byte[] gaoChen = {0, 0};
//            byte[] speedb = {0, 0};
//            byte[] orientation = {0, 0};
//            //bcd时间
////        byte[] bcdTime = TimeUtils.getBcdTime();
//            byte[] bcdTime = BCD8421Operator.getInstance().getBCDTime();
//            //位置信息附加项
////        return ByteUtil.byteMergerAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime);
//            return BitOperator.getInstance().concatAll(alarm, state, latb, lngb, gaoChen, speedb, orientation, bcdTime);
        }catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }


    public static class TerminalGPSInfo{            //  位置基本信息
//        报警标志[4]   报警标志位定义         DWORD
        private int warningMark;
//        状态[4]     状态位定义               DWORD
        private int state;
//        纬度[4]     以度为单位的纬度值乘以 10 的 6 次方，精确到百万分之一度     DWORD
        private long latitude;
//        经度[4]     以度为单位的经度值乘以 10 的 6 次方，精确到百万分之一度     DWORD
        private long longitude;
//        高程[2]     海拔高度，单位为米（m）
        private int altitude;
//        速度[2]     1/10km/h
        private int speed ;
//        方向[1]     0-359，正北为 0，顺时针
        private int direction;
//        时间[6]
        private byte[] BCDTime ;
//        位置附加信息
//        附加信息 ID
//        private byte[] fid= new byte[6] ;
//        附加信息长度


        public int getWarningMark() {
            return warningMark;
        }

        public void setWarningMark(int warningMark) {
            this.warningMark = warningMark;
        }

        public int getState() {
            return state;
        }

        public void setState(int state) {
            this.state = state;
        }

        public long getLatitude() {
            return latitude;
        }

        public void setLatitude(double latitude) {
            this.latitude = (long) (Utils.getSixPoint(latitude)* Math.pow(10,6));
        }

        public long getLongitude() {
            return longitude;
        }

        public void setLongitude(double longitude) {
            this.longitude = (long) (Utils.getSixPoint(longitude)* Math.pow(10,6));
        }

        public int getAltitude() {
            return altitude;
        }

        public void setAltitude(int altitude) {
            this.altitude = altitude;
        }

        public int getSpeed() {
            return speed;
        }

        public void setSpeed(int speed) {
            this.speed = speed;
        }

        public int getDirection() {
            return direction;
        }

        public void setDirection(int direction) {
            this.direction = direction;
        }

        public byte[] getBCDTime() {
            return BCDTime;
        }

        public void setBCDTime(byte[] BCDTime) {
            this.BCDTime = BCDTime;
        }

        @Override
        public String toString() {
            return "TerminalGPSInfo{" +
                    "warningMark='" + warningMark + '\'' +
                    ", state='" + state + '\'' +
                    ", latitude=" + latitude +
                    ", longitude=" + longitude +
                    ", altitude=" + altitude +
                    ", speed='" + speed + '\'' +
                    ", direction=" + direction +
                    ", BCDTime=" + Arrays.toString(BCDTime) +
                    '}';
        }
    }
}
