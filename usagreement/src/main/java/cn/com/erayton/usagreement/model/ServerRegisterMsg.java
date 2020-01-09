package cn.com.erayton.usagreement.model;

import android.util.Log;

import cn.com.erayton.usagreement.utils.BitOperator;


/**
 * Created by android on 2017/4/1.
 */

public class ServerRegisterMsg extends PacketData {
    private static final String TAG = "ServerRegisterMsg";
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
        Log.e(TAG, "inflatePackageBody_msgBodyLength: " + msgBodyLength);
        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        BitOperator bitOperator = BitOperator.getInstance();
        setAnswerFlowId(bitOperator.parseIntFromBytes(tmp, 0, 2));
        setRegisterResult(bitOperator.parseIntFromBytes(tmp, 2, 1));
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
