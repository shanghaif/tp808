package cn.com.erayton.usagreement.model.model;

public class TerminalRiderShipInfo {
    //  起始时间
    private String startTime ;
    //  结束时间
    private String endTime ;
    //  上车人数
    private int getonNum ;
    //  下车人数
    private int getoffNum ;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getGetonNum() {
        return getonNum;
    }

    public void setGetonNum(int getonNum) {
        this.getonNum = getonNum;
    }

    public int getGetoffNum() {
        return getoffNum;
    }

    public void setGetoffNum(int getoffNum) {
        this.getoffNum = getoffNum;
    }

    @Override
    public String toString() {
        return "TerminalRiderShipInfo{" +
                "startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", getonNum=" + getonNum +
                ", getoffNum=" + getoffNum +
                '}';
    }
}
