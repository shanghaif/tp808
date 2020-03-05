package cn.com.erayton.usagreement.model.encode;


import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.model.TerminalGPSInfo;
import cn.com.erayton.usagreement.utils.BitOperator;

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
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //32 位二进制 从高到低位
            baos.write(BitOperator.getInstance().integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getWarningMark()), 2)));
//            String radix2State = "00000000000000000000000000000010";
            baos.write(BitOperator.getInstance().integerTo4Bytes(Integer.parseInt(terminalGPSInfo.getState(), 2)));
//            baos.write(BitOperator.getInstance().integerTo4Bytes(Integer.parseInt(String.valueOf(terminalGPSInfo.getState()), 2)));
//            baos.write(BitOperator.getInstance().integerTo4Bytes(Integer.parseInt(radix2State, 2)));
            baos.write(BitOperator.getInstance().longToBytes(terminalGPSInfo.getLatitude(), 4));
            baos.write(BitOperator.getInstance().longToBytes(terminalGPSInfo.getLongitude(), 4));
            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalGPSInfo.getAltitude()));
            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalGPSInfo.getSpeed()));
            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalGPSInfo.getDirection()));
//            baos.write(BCD8421Operator.getInstance().getBCDTime());
            baos.write(BitOperator.getInstance().getBCDTime());
            baos.write(BitOperator.getInstance().integerTo1Bytes(terminalGPSInfo.getAdditionalInformationId()));
            baos.write(BitOperator.getInstance().integerTo1Bytes(terminalGPSInfo.getAdditionalInformationLength()));
            baos.write(BitOperator.getInstance().integerTo4Bytes(terminalGPSInfo.getMileage()));
            //  位置信息附加项

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


//    public static class TerminalGPSInfo{            //  位置基本信息
//        //        报警标志[4]   报警标志位定义         DWORD
//        private int warningMark;
//        //        状态[4]     状态位定义               DWORD
//        private String state;
//        //        private int state;
////        纬度[4]     以度为单位的纬度值乘以 10 的 6 次方，精确到百万分之一度     DWORD
//        private long latitude;
//        //        经度[4]     以度为单位的经度值乘以 10 的 6 次方，精确到百万分之一度     DWORD
//        private long longitude;
//        //        高程[2]     海拔高度，单位为米（m）
//        private int altitude;
//        //        速度[2]     1/10km/h
//        private int speed ;
//        //        方向[1]     0-359，正北为 0，顺时针
//        private int direction;
//        //        时间[6]
//        private byte[] BCDTime ;
//        //        位置附加信息
////        附加信息 ID
//        private int additionalInformationId ;
//        //        private byte[] fid= new byte[6] ;
////        附加信息长度
//        private int additionalInformationLength ;
//        // 里程
//        private int mileage ;
//
//
//        public int getWarningMark() {
//            return warningMark;
//        }
//
//        public void setWarningMark(int warningMark) {
//            this.warningMark = warningMark;
//        }
//
//        public String getState() {
//            return state;
//        }
//
//        public void setState(String state) {
//            this.state = state;
//        }
//
////        public int getState() {
////            return state;
////        }
////
////        public void setState(int state) {
////            this.state = state;
////        }
//
//        public long getLatitude() {
//            return latitude;
//        }
//
//        public void setLatitude(double latitude) {
//            this.latitude = (long) (Utils.getSixPoint(latitude)* Math.pow(10,6));
//        }
//
//        public long getLongitude() {
//            return longitude;
//        }
//
//        public void setLongitude(double longitude) {
//            this.longitude = (long) (Utils.getSixPoint(longitude)* Math.pow(10,6));
//        }
//
//        public int getAltitude() {
//            return altitude;
//        }
//
//        public void setAltitude(int altitude) {
//            this.altitude = altitude;
//        }
//
//        public int getSpeed() {
//            return speed;
//        }
//
//        public void setSpeed(int speed) {
//            this.speed = speed*10;
//        }
//
//        public int getDirection() {
//            return direction;
//        }
//
//        public void setDirection(int direction) {
//            this.direction = direction;
//        }
//
//        public byte[] getBCDTime() {
//            return BCDTime;
//        }
//
//        public void setBCDTime(byte[] BCDTime) {
//            this.BCDTime = BCDTime;
//        }
//
//        public int getAdditionalInformationId() {
//            return additionalInformationId;
//        }
//
//        public void setAdditionalInformationId(int additionalInformationId) {
//            this.additionalInformationId = additionalInformationId;
//        }
//
//        public int getAdditionalInformationLength() {
//            return additionalInformationLength;
//        }
//
//        public void setAdditionalInformationLength(int additionalInformationLength) {
//            this.additionalInformationLength = additionalInformationLength;
//        }
//
//        public int getMileage() {
//            return mileage;
//        }
//
//        public void setMileage(int mileage) {
//            this.mileage = mileage;
//        }
//
//        @Override
//        public String toString() {
//            return "TerminalGPSInfo{" +
//                    "warningMark=" + warningMark +
//                    ", state=" + state +
//                    ", latitude=" + latitude +
//                    ", longitude=" + longitude +
//                    ", altitude=" + altitude +
//                    ", speed=" + speed +
//                    ", direction=" + direction +
//                    ", BCDTime=" + Arrays.toString(BCDTime) +
//                    ", additionalInformationId=" + additionalInformationId +
//                    ", additionalInformationLength=" + additionalInformationLength +
//                    ", mileage=" + mileage +
//                    '}';
//        }
//    }
}
