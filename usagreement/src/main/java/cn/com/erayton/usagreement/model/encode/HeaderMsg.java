package cn.com.erayton.usagreement.model.encode;

public class HeaderMsg {

    private int msgId ;
    private String phone ;
    //  流水号
    private int flowId ;
    //  是否分包
    private boolean isSub ;

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

    public int getFlowId() {
        return flowId;
    }

    public void setFlowId(int flowId) {
        this.flowId = flowId;
    }

    public boolean getIsSub() {
        return isSub;
    }

    public void setIsSub(boolean isSub) {
        this.isSub = isSub;
    }

    @Override
    public String toString() {
        return "HeaderMsg{" +
                "msgId=" + msgId +
                ", phone='" + phone + '\'' +
                '}';
    }
}
