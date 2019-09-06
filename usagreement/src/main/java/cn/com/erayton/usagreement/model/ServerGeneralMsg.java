package cn.com.erayton.usagreement.model;

import android.util.Log;

import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.Decoder4LoggingOnly;

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
        Decoder4LoggingOnly decoder4LoggingOnly = new Decoder4LoggingOnly() ;

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
//        Log.d(TAG, "应答流水 --bitOperator.parseIntFromBytes(tmp, 2, 2)"+bitOperator.parseIntFromBytes(tmp, 0, 2)) ;
        setSerialNumber(bitOperator.parseIntFromBytes(tmp, 0, 2));
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 0, 1)"+bitOperator.parseIntFromBytes(tmp, 0, 1)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 0, 2)"+bitOperator.parseIntFromBytes(tmp, 0, 2)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 0, 3)"+bitOperator.parseIntFromBytes(tmp, 0, 3)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 0, 4)"+bitOperator.parseIntFromBytes(tmp, 0, 4)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 0, 5)"+bitOperator.parseIntFromBytes(tmp, 0, 5)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 1, 1)"+bitOperator.parseIntFromBytes(tmp, 1, 1)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 1, 2)"+bitOperator.parseIntFromBytes(tmp, 1, 2)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 1, 3)"+bitOperator.parseIntFromBytes(tmp, 1, 3)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 1, 4)"+bitOperator.parseIntFromBytes(tmp, 1, 4)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 2, 1)"+bitOperator.parseIntFromBytes(tmp, 2, 1)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 2, 2)"+bitOperator.parseIntFromBytes(tmp, 2, 2)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 2, 3)"+bitOperator.parseIntFromBytes(tmp, 2, 3)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 3, 1)"+bitOperator.parseIntFromBytes(tmp, 3, 1)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 3, 2)"+bitOperator.parseIntFromBytes(tmp, 3, 2)) ;
//        Log.d(TAG, "应答ID --bitOperator.parseIntFromBytes(tmp, 4, 1)"+bitOperator.parseIntFromBytes(tmp, 4, 1)) ;
        setAnswerId(bitOperator.parseIntFromBytes(tmp, 2, 2));
        setResult(bitOperator.parseIntFromBytes(tmp, 4, 1));
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
