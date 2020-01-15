package cn.com.erayton.jt_t808.video.eventBus.eventBean;

/**
 * Created by Administrator on 2018/5/8.
 */

public class NoticeEventBean {
    private byte type;
    private String name;
    private String msId;
    public NoticeEventBean(){
    }
    public NoticeEventBean(byte type,String name){
        this.type=type;
        this.name=name;
    }
    public NoticeEventBean(byte type,String name,String msId){
        this.type=type;
        this.name=name;
        this.msId=msId;
    }
    public String getMsId() {
        return msId;
    }

    public void setMsId(String msId) {
        this.msId = msId;
    }

    public byte getType() {
        return type;
    }

    public void setType(byte type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
