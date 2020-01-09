package cn.com.erayton.usagreement.model;

import android.util.Log;

import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 音视频实时传输控制
 * 0x92
 * */
public class ServerAVTranslateControlMsg extends PacketData {
    String TAG = ServerAVTranslateControlMsg.class.getName() ;

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        LogUtils.d("inflatePackageBody_msgBodyLength: " + msgBodyLength);
        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        BitOperator bitOperator = BitOperator.getInstance();
        LogUtils.d("pass:"+bitOperator.parseIntFromBytes(tmp, 1, 1));
        LogUtils.d("control:"+bitOperator.parseIntFromBytes(tmp, 2, 1));
        LogUtils.d("close:"+bitOperator.parseIntFromBytes(tmp, 3, 1));
        LogUtils.d("steam type:"+bitOperator.parseIntFromBytes(tmp, 4, 1));
//        setIpLength(bitOperator.parseIntFromBytes(tmp, 1, 1));
//        setHost(bitOperator.bytesToStr(tmp,2, getIpLength()));
//        setTcpPort((int) bitOperator.toDDint(tmp, getIpLength()+2, 2));
//        setUdpPort((int) bitOperator.toDDint(tmp, getIpLength()+4, 2));
//        setChannelNum(bitOperator.parseIntFromBytes(tmp, getIpLength()+6, 1));
//        setDataType(bitOperator.parseIntFromBytes(tmp, getIpLength()+7, 1));
//        setSteamType(bitOperator.parseIntFromBytes(tmp, getIpLength()+8, 1));     //  少了个码流类型

    }


}
