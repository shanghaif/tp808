package net.rayton.usprotocol.model.info;

public class TerminalGeneralInfo {

    //        结果
    private int result ;
    //  应答 ID
    private int respId ;
    //  应答流水号
    private int seNum ;

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public int getRespId() {
        return respId;
    }

    public void setRespId(int respId) {
        this.respId = respId;
    }

    public int getSeNum() {
        return seNum;
    }

    public void setSeNum(int seNum) {
        this.seNum = seNum;
    }

    @Override
    public String toString() {
        return "TerminalGeneralInfo{" +
                "result=" + result +
                ", respId=" + respId +
                ", seNum=" + seNum +
                '}';
    }
}
