package cn.com.erayton.usagreement.model.decode;


import java.util.Arrays;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 文件上传指令
 * 回复通用回复,回复后按照要求上传文件
 * */
public class ServerFileUploadMsg extends PacketData {
    //  当前长度, 仅用于解析
    protected int nowLength ;
    //  服务器地址长度
    private int ipLength ;
    //
    private String host ;
    //  FTP 服务器端口号
    private int port ;
    //  用户名长度
    private  int nameLength ;
    //  用户名
    private String userName ;
    //  密码长度
    private int passLength ;
    //  密码
    private String password ;
    //  文件上传路径长度
    private int pathLength ;
    //  文件上传路径
    private String uploadPath ;
    //  逻辑通道号
    private int channelNum ;
    //  开始时间
    private String startTime ;
    //  结束时间
    private String endTime ;
    //  报警标志
    private byte[] warningMark ;
    //  音视频资源类型
    private int resourceType ;
    //  码流类型
    private int steamType ;
    //  存储位置(存储器类型)
    private int memoryType ;
    //  任务执行条件[1]   用 bit 位表示
    private byte[] taskConditions ;


    private int getNowLength() {
        //          k       +       l       +       m       +       n
        return getIpLength()+getNameLength()+getPassLength()+getPathLength();
    }

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

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getNameLength() {
        return nameLength;
    }

    public void setNameLength(int nameLength) {
        this.nameLength = nameLength;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPassLength() {
        return passLength;
    }

    public void setPassLength(int passLength) {
        this.passLength = passLength;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPathLength() {
        return pathLength;
    }

    public void setPathLength(int pathLength) {
        this.pathLength = pathLength;
    }

    public String getUploadPath() {
        return uploadPath;
    }

    public void setUploadPath(String uploadPath) {
        this.uploadPath = uploadPath;
    }

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

    public byte[] getTaskConditions() {
        return taskConditions;
    }

    public void setTaskConditions(byte[] taskConditions) {
        taskConditions = taskConditions;
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
        //  k
        setIpLength(bitOperator.parseIntFromBytes(tmp, 0, 1));
        //  1+k     -k
        setHost(bitOperator.bytesToStr(tmp, 1, getIpLength()));
        //  1+k
        setPort(bitOperator.parseIntFromBytes(tmp, 1+getNowLength(), 2));

        //  l       3+k
        setNameLength(bitOperator.parseIntFromBytes(tmp, 3+getNowLength(), 1));
        //  4+k+l     -l
        setUserName(bitOperator.bytesToStr(tmp, 4+getNowLength()-getNameLength(), getNameLength()));

        //  m       4+k+l
        setPassLength(bitOperator.parseIntFromBytes(tmp, 4+getNowLength(), 1));
        //  5+k+l+m       -m
        setPassword(bitOperator.bytesToStr(tmp, 5+getNowLength()-getPassLength(), getPassLength()));

        //  n           5+k+l+m
        setPathLength(bitOperator.parseIntFromBytes(tmp, 5+getNowLength(), 1));
        //  6+k+l+m+n     -n
        setUploadPath(bitOperator.bytesToStr(tmp, 6+getNowLength()-getPathLength(), getPathLength()));

        //  6+k+l+m+n
        setChannelNum(bitOperator.parseIntFromBytes(tmp, 6+getNowLength(), 1));
        setStartTime(bitOperator.parseBcdStringFromBytes(tmp, 7+getNowLength(), 6));
        setEndTime(bitOperator.parseBcdStringFromBytes(tmp, 13+getNowLength(), 6));
        setWarningMark(bitOperator.subByte(tmp, 19+getNowLength(), 8));
        setResourceType(bitOperator.parseIntFromBytes(tmp, 27+getNowLength(), 1));
        setSteamType(bitOperator.parseIntFromBytes(tmp, 28+getNowLength(), 1));
        setMemoryType(bitOperator.parseIntFromBytes(tmp, 29+getNowLength(), 1));
        setTaskConditions(bitOperator.subByte(tmp, 30+getNowLength(), 1));
        LogUtils.d("inflatePackageBody_mowLength: " + getNowLength());
    }

    @Override
    public String toString() {
        return "ServerFileUploadMsg{" +
                "nowLength=" + nowLength +
                ", ipLength=" + ipLength +
                ", host='" + host + '\'' +
                ", port=" + port +
                ", nameLength=" + nameLength +
                ", userName='" + userName + '\'' +
                ", passLength=" + passLength +
                ", password='" + password + '\'' +
                ", pathLength=" + pathLength +
                ", uploadPath='" + uploadPath + '\'' +
                ", channelNum=" + channelNum +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", warningMark=" + Arrays.toString(warningMark) +
                ", resourceType=" + resourceType +
                ", steamType=" + steamType +
                ", memoryType=" + memoryType +
                ", TaskConditions=" + Arrays.toString(taskConditions) +
                '}';
    }
}
