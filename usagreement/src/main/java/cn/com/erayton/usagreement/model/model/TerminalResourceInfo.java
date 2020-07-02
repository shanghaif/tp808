package cn.com.erayton.usagreement.model.model;

public class TerminalResourceInfo {
    //  通道号 [1]
    private int channelNum ;
    //  开始时间    [6]
    private String startTime ;
    //  结束时间    [6]
    private String endTime ;
    //  报警标志    [0, 0, 0, 0, 0, 0, 0, 0]    [8]
    private long wrang ;
    //  音视频资源类型 0, 音视频  1, 音频   2, 视频   [1]
    private int resourceType ;
    //  1, 主码流  2, 子码流  [1]
    private int steamType ;
    //  存储类型    1, 主存储器 2, 灾备存储器    [1]
    private int memoryType ;
    //  文件大小, 单位字节(BYTE)  // 1024 -> 1K
    private long fileSize ;

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

    public long getWrang() {
        return wrang;
    }

    public void setWrang(long wrang) {
        this.wrang = wrang;
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    @Override
    public String toString() {
        return "TerminalResourceInfo{" +
                "channelNum=" + channelNum +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", wrang=" + wrang +
                ", resourceType=" + resourceType +
                ", steamType=" + steamType +
                ", memoryType=" + memoryType +
                ", fileSize=" + fileSize +
                '}';
    }
}
