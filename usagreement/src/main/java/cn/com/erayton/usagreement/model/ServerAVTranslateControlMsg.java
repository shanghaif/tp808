package cn.com.erayton.usagreement.model;

import android.util.Log;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 音视频实时传输控制
 * 0x9102
 * */
public class ServerAVTranslateControlMsg extends PacketData {
    String TAG = ServerAVTranslateControlMsg.class.getName() ;

    private int channelNum ;

    private int controlCode ;

    private int closeType ;

    private int steamType ;


    public int getChannelNum() {
        return channelNum;
    }

    private void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getControlCode() {
        return controlCode;
    }

    private void setControlCode(int controlCode) {
        this.controlCode = controlCode;
    }

    public int getCloseType() {
        return closeType;
    }

    private void setCloseType(int closeType) {
        this.closeType = closeType;
    }

    public int getSteamType() {
        return steamType;
    }

    private void setSteamType(int steamType) {
        this.steamType = steamType;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        LogUtils.d("inflatePackageBody_msgBodyLength: " + msgBodyLength);
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            System.arraycopy(data, Constants.MSGBODY_SUBPACKAGE_START_INDEX, tmp, 0, tmp.length);
        }else
        System.arraycopy(data, Constants.MSGBODY_START_INDEX, tmp, 0, tmp.length);

        LogUtils.d("length:"+tmp.length+"\n "+ HexStringUtils.toHexString(tmp));
//        逻辑通道号
        setChannelNum(BitOperator.getInstance().parseIntFromBytes(tmp, 0, 1));
        LogUtils.d("pass:"+getChannelNum());
//        控制指令
        setControlCode(BitOperator.getInstance().parseIntFromBytes(tmp, 1, 1));
        LogUtils.d("control:"+getControlCode());
//        关闭音频类型
        setCloseType(BitOperator.getInstance().parseIntFromBytes(tmp, 2, 1));
        LogUtils.d("close:"+getCloseType());
//        切换码流类型
        setSteamType(BitOperator.getInstance().parseIntFromBytes(tmp, 3, 1));
        LogUtils.d("steam type:"+getSteamType());

    }


}
