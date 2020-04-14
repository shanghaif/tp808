package cn.com.erayton.usagreement.model.decode;


import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 平台下发远程录像回放请求
 * 回放方式为1 和2 时 快进或快退倍数字段内容有效,否则置为0
 * jt/JTT1078-2016.pdf   P15
 * */
public class ServerVideoReplayMsg extends ServerResourceQueryMsg {

    private int ipLength ;

    private String host ;

    private int tcpPort ;

    private int udpPort ;
    //  回放方式
    private int playbackMode ;
    //  快进或快退倍数
    private int multiple ;

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

    public int getPlaybackMode() {
        return playbackMode;
    }

    public void setPlaybackMode(int playbackMode) {
        this.playbackMode = playbackMode;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
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
        setIpLength(bitOperator.parseIntFromBytes(tmp, 0, 1));
        setHost(bitOperator.bytesToStr(tmp, 1, getIpLength()));
        setTcpPort(bitOperator.parseIntFromBytes(tmp, 1+getIpLength(), 2));
        setUdpPort(bitOperator.parseIntFromBytes(tmp, 3+getIpLength(), 2));
        setChannelNum(bitOperator.parseIntFromBytes(tmp, 5+getIpLength(), 1));
        setResourceType(bitOperator.parseIntFromBytes(tmp, 6+getIpLength(), 1));
        setSteamType(bitOperator.parseIntFromBytes(tmp, 7+getIpLength(), 1));
        setMemoryType(bitOperator.parseIntFromBytes(tmp, 8+getIpLength(), 1));
        setPlaybackMode(bitOperator.parseIntFromBytes(tmp, 9+getIpLength(), 1));
        setMultiple(bitOperator.parseIntFromBytes(tmp, 10+getIpLength(), 1));
        setStartTime(bitOperator.parseBcdStringFromBytes(tmp, 11+getIpLength(), 6));
        setEndTime(bitOperator.parseBcdStringFromBytes(tmp, 17+getIpLength(), 6));
    }

    @Override
    public String toString() {
        return "ServerVideoReplayMsg{" +
                "ipLength=" + ipLength +
                ", host='" + host + '\'' +
                ", tcpPort=" + tcpPort +
                ", udpPort=" + udpPort +
                ", playbackMode=" + playbackMode +
                "channelNum=" + getChannelNum() +
                ", startTime='" + getStartTime() + '\'' +
                ", endTime='" + getEndTime() + '\'' +
                ", resourceType=" + getResourceType() +
                ", steamType=" + getSteamType() +
                ", memoryType=" + getMemoryType() +
                ", multiple=" + multiple +
                '}';
    }
}
