package cn.erayton.cameratest.event.entity;

/**
 * 日志事件
 */
public class RecordEvent {

    private int status;

    public RecordEvent(int status) {
        super();
        this.status = status ;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
