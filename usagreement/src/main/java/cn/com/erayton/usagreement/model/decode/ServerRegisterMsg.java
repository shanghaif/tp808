package cn.com.erayton.usagreement.model.decode;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
import cn.com.erayton.usagreement.utils.LogUtils;



public class ServerRegisterMsg extends PacketData {
    private int registerResult;
    private String authentication;


    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    public int getRegisterResult() {
        return registerResult;
    }

    public void setRegisterResult(int registerResult) {
        this.registerResult = registerResult;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        LogUtils.e("inflatePackageBody_msgBodyLength: " + msgBodyLength);
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            System.arraycopy(data, Constants.MSGBODY_SUBPACKAGE_DEFAULT_START_INDEX, tmp, 0, tmp.length);
        }else {
            System.arraycopy(data, Constants.MSGBODY_DEFAULT_START_INDEX, tmp, 0, tmp.length);
        }
        LogUtils.e(HexStringUtils.toHexString(tmp));
        setAnswerFlowId(BitOperator.getInstance().parseIntFromBytes(tmp, 0, 2));
        setRegisterResult(BitOperator.getInstance().parseIntFromBytes(tmp, 2, 1));
        setAuthentication(new String(tmp, 3, tmp.length - 3));
    }

    @Override
    public String toString() {
        String str = super.toString();
        String custom = "{" +
                "answerFlowId=" + answerFlowId +
                "registerResult=" + registerResult +
                ", authentication='" + authentication + '\'' +
                '}';
        return str + "\n" + custom;
    }
}
