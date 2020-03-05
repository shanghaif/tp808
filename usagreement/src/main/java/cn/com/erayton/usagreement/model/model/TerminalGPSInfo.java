package cn.com.erayton.usagreement.model.model;

import java.util.Arrays;

import cn.com.erayton.usagreement.utils.Utils;

public class TerminalGPSInfo {
    //  位置基本信息
//        报警标志[4]   报警标志位定义         DWORD
    private int warningMark;
    //        状态[4]     状态位定义               DWORD
    private String state;
    //        private int state;
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
    private int additionalInformationId ;
    //        private byte[] fid= new byte[6] ;
//        附加信息长度
    private int additionalInformationLength ;
    // 里程
    private int mileage ;


    public int getWarningMark() {
        return warningMark;
    }

    public void setWarningMark(int warningMark) {
        this.warningMark = warningMark;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

//        public int getState() {
//            return state;
//        }
//
//        public void setState(int state) {
//            this.state = state;
//        }

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
        this.speed = speed*10;
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

    public int getAdditionalInformationId() {
        return additionalInformationId;
    }

    public void setAdditionalInformationId(int additionalInformationId) {
        this.additionalInformationId = additionalInformationId;
    }

    public int getAdditionalInformationLength() {
        return additionalInformationLength;
    }

    public void setAdditionalInformationLength(int additionalInformationLength) {
        this.additionalInformationLength = additionalInformationLength;
    }

    public int getMileage() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        return "TerminalGPSInfo{" +
                "warningMark=" + warningMark +
                ", state=" + state +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", direction=" + direction +
                ", BCDTime=" + Arrays.toString(BCDTime) +
                ", additionalInformationId=" + additionalInformationId +
                ", additionalInformationLength=" + additionalInformationLength +
                ", mileage=" + mileage +
                '}';
    }
}
