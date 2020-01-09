package cn.com.erayton.usagreement.model;

import android.util.Log;

import cn.com.erayton.usagreement.utils.BitOperator;

/**
 * 平台下发的设置参数
 * 比协议文档位数多 1 位的原因是流水号在这里解析
 * */
public class ServerAVTranslateMsg extends PacketData {
    String TAG = ServerAVTranslateMsg.class.getName() ;
    //  服务器 IP 地址长度
    private int ipLength ;
    //  服务器 IP 地址
    private String host ;
    //  服务器 TCP 端口号
    private int tcpPort ;
    //  服务器 UDP 端口号
    private int udpPort ;
    //  逻辑通道号
    private int channelNum ;
    //  数据类型
    private int dataType ;
    //  码流类型
    private int steamType ;

    public int getIpLength() {
        return ipLength;
    }

    public void setIpLength(int ipLength) {
        this.ipLength = ipLength;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(int tcpPort) {
        this.tcpPort = tcpPort;
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int udpPort) {
        this.udpPort = udpPort;
    }

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getDataType() {
        return dataType;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public int getSteamType() {
        return steamType;
    }

    public void setSteamType(int steamType) {
        this.steamType = steamType;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        Log.e(TAG, "inflatePackageBody_msgBodyLength: " + msgBodyLength);
        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        BitOperator bitOperator = BitOperator.getInstance();
        setIpLength(bitOperator.parseIntFromBytes(tmp, 1, 1));
        setHost(bitOperator.bytesToStr(tmp,2, getIpLength()));
        setTcpPort((int) bitOperator.toDDint(tmp, getIpLength()+2, 2));
        setUdpPort((int) bitOperator.toDDint(tmp, getIpLength()+4, 2));
        setChannelNum(bitOperator.parseIntFromBytes(tmp, getIpLength()+6, 1));
        setDataType(bitOperator.parseIntFromBytes(tmp, getIpLength()+7, 1));
//        setSteamType(bitOperator.parseIntFromBytes(tmp, getIpLength()+8, 1));     //  少了个码流类型

    }


    @Override
    public String toString() {
        return "ServerAVTranslateMsg{" +
                "TAG='" + TAG + '\'' +
                ", ipLength=" + ipLength +
                ", host='" + host + '\'' +
                ", tcpPort=" + tcpPort +
                ", udpPort=" + udpPort +
                ", channelNum=" + channelNum +
                ", dataType=" + dataType +
                ", steamType=" + steamType +
                '}';
    }
}
