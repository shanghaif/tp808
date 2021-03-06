package cn.com.erayton.usagreement.model.decode;

import android.util.Log;

import cn.com.erayton.usagreement.utils.BitOperator;

public class ServerGeneralMsg extends PacketData{
    private static final String TAG = "ServerGeneralMsg";
    /**
     * 平台通用应答解析
     * */
//    应答流水号     WORD
    private int serialNumber ;
//    应答 ID        WORD
    private int answerId ;
//    结果           BYTE
    private int result ;

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }


    @Override
    public void inflatePackageBody(byte[] data) {

        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        Log.e(TAG, "inflatePackageBody_msgBodyLength: " + msgBodyLength);

        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        setAnswerFlowId(BitOperator.getInstance().parseIntFromBytes(tmp, 0, 2));
//        Log.d(TAG, "应答流水 --bitOperator.parseIntFromBytes(tmp, 2, 2)"+bitOperator.parseIntFromBytes(tmp, 0, 2)) ;
        setSerialNumber(BitOperator.getInstance().parseIntFromBytes(tmp, 0, 2));
        setAnswerId(BitOperator.getInstance().parseIntFromBytes(tmp, 2, 2));
        setResult(BitOperator.getInstance().parseIntFromBytes(tmp, 4, 1));
//        setAuthentication(new String(tmp, 3, tmp.length - 3));
//        Log.d(TAG, "bitOperator.parseIntFromBytes(tmp, 4, 1)"+bitOperator.parseIntFromBytes(tmp, 4, 1)) ;
//        Log.d(TAG, "bitOperator.parseIntFromBytes(tmp, tmp.length-1, 1)"+bitOperator.parseIntFromBytes(tmp, tmp.length-1, 1)) ;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public String toString() {
        String str = super.toString();
        String custom = "{" +
                "serialNumber='" + serialNumber + '\'' +
                ", answerId='" + answerId + '\'' +
                ", result=" + result +
                '}';
        return str + "\n" + custom;
    }
}
