package com.library.bean;

public class FileMsg {

    private String fileName ;
    private String startTime ;
    private String endTime ;
    //  以 1024 为1 M
    private long fileSize ;
    private String displayName ;
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
