package cn.com.erayton.usagreement.model.decode;


import java.util.Arrays;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 查询资源列表
 * */
public class ServerResourceQueryMsg extends PacketData {
    //  逻辑通道号
    private int channelNum ;
    //  开始时间    BCD[6]
    private String startTime ;
    //  结束时间
    private String endTime ;
    //  报警标志
    private byte[] warningMark ;
    //  音视频资源类型
    private int resourceType ;
    //  码流类型
    private int steamType ;
    //  存储器类型
    private int memoryType ;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

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

    public byte[] getWarningMark() {
        return warningMark;
    }

    public void setWarningMark(byte[] warningMark) {
        this.warningMark = warningMark;
    }

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public int getSteamType() {
        return steamType;
    }

    public void setSteamType(int steamType) {
        this.steamType = steamType;
    }

    public int getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(int memoryType) {
        this.memoryType = memoryType;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        LogUtils.d("inflatePackageBody_msgBodyLength: " + msgBodyLength);
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            System.arraycopy(data, Constants.MSGBODY_SUBPACKAGE_START_INDEX, tmp, 0, tmp.length);
        }else {
            System.arraycopy(data, Constants.MSGBODY_START_INDEX, tmp, 0, tmp.length);
        }
        BitOperator bitOperator = BitOperator.getInstance();
        setChannelNum(bitOperator.parseIntFromBytes(tmp, 0, 1));
        setStartTime(bitOperator.parseBcdStringFromBytes(tmp, 1, 6));
        setEndTime(bitOperator.parseBcdStringFromBytes(tmp, 7, 6));
        setWarningMark(bitOperator.subByte(tmp, 13, 8));
        setResourceType(bitOperator.parseIntFromBytes(tmp, 21, 1));
        setSteamType(bitOperator.parseIntFromBytes(tmp, 22, 1));
        setMemoryType(bitOperator.parseIntFromBytes(tmp, 23, 1));
    }

    @Override
    public String toString() {
        return "ServerResourceQueryMsg{" +
                "channelNum=" + channelNum +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", warningMark='" + Arrays.toString(warningMark) + '\'' +
                ", resourceType=" + resourceType +
                ", steamType=" + steamType +
                ", memoryType=" + memoryType +
                '}';
    }
}
