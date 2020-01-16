package cn.com.erayton.jt_t808.video.eventBus.event;


import cn.com.erayton.jt_t808.video.eventBus.EventBusEvent;

public class BroadCastMainEvent extends EventBusEvent {
    private String host ;
    private int tcpPort ;
    private int udpPort ;

    private int channelNum ;
    private int dataType ;
    private int steamType ;
    private int flowId ;


    public BroadCastMainEvent(int code, Object data) {
        super(code, data);
    }


    /**
     * @param  host 服务器 IP 地址
     * @param tcpPort 服务器 TCP 端口号
     * @param udpPort 服务器 UDP 端口号
     * @param channelNum 逻辑通道号
     * @param dataType 数据类型
     * @param steamType 码流类型
     * @param flowId 流水号
     * */
    public BroadCastMainEvent(int code, String host, int tcpPort, int udpPort, int channelNum, int dataType, int steamType, int flowId) {
        super(code);
        this.host = host;
        this.tcpPort = tcpPort;
        this.udpPort = udpPort;
        this.channelNum = channelNum;
        this.dataType = dataType;
        this.steamType = steamType;
        this.flowId = flowId;
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

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }
}
