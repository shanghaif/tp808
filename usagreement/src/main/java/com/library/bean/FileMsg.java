package com.library.bean;

public class FileMsg {

    //  文件名, 有可能不带文件格式
    private String fileName ;
    //  文件开始时间
    private String startTime ;
    //  文件结束时间
    private String endTime ;
    //  以 1024 为1 M
    private long fileSize ;
    //  文件全名(带.后面的格式)
    private String displayName ;
    //  文件路径
    private String filePath ;
    private int fileType ;
    //  时间长度    得到的值除以100
    private int duration ;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getFileType() {
        return fileType;
    }

    public void setFileType(int fileType) {
        this.fileType = fileType;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
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

    @Override
    public String toString() {
        return "FileMsg{" +
                "fileName='" + fileName + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", fileSize=" + fileSize +
                ", displayName='" + displayName + '\'' +
                ", filePath='" + filePath + '\'' +
                ", fileType=" + fileType +
                ", duration=" + duration +
                '}';
    }
}
