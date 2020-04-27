package cn.com.erayton.usagreement.data.db.table;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import cn.com.erayton.usagreement.data.db.config.MyDatabase;

/**
 * 视频信息录制表
 * Created by Administrator on 2019/1/28.
 */
@Table(database = MyDatabase.class)
public class VideoRecord extends BaseModel {
    @PrimaryKey(autoincrement = true)
    public long id;
    @Column
    public String name;     //  文件名
    @Column
    public long startTime;  //  开始时间
    @Column
    public long endTime;    //  结束时间
    @Column
    public int channel;     //  通道号
    @Column
    public int sourceType;  //  资源类型
    @Column
    public int streamType;  //  码流类型
    @Column
    public int memoryType;  //  存储器类型
    @Column
    public long size;       //  文件大小
    @Column
    public String warning;  //  报警标志

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

    public int getStreamType() {
        return streamType;
    }

    public void setStreamType(int streamType) {
        this.streamType = streamType;
    }

    public int getMemoryType() {
        return memoryType;
    }

    public void setMemoryType(int memoryType) {
        this.memoryType = memoryType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getWarning() {
        return warning;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }
}
