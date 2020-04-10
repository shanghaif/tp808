package cn.com.erayton.usagreement.model.encode;

public class HeaderMsg {

    private int msgId ;
    private String phone ;

    public int getMsgId() {
        return msgId;
    }

    public void setMsgId(int msgId) {
        this.msgId = msgId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public String toString() {
        return "HeaderMsg{" +
                "msgId=" + msgId +
                ", phone='" + phone + '\'' +
                '}';
    }
}
