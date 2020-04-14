package cn.com.erayton.usagreement.model.decode;


import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 云台调整焦距
 * 平台请求调整镜头焦距
 *  0, 焦距调大   1, 焦距调小
 * */
public class ServerFocalLengthMsg extends PacketData {
    //  逻辑通道号
    private int channelNum ;
    //  焦距调整方向
    private int direction ;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
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
        }else {
            System.arraycopy(data, Constants.MSGBODY_START_INDEX, tmp, 0, tmp.length);
        }
        BitOperator bitOperator = BitOperator.getInstance();
        setChannelNum(bitOperator.parseIntFromBytes(tmp, 0, 1));
        setDirection(bitOperator.parseIntFromBytes(tmp,1, 1));
    }

    @Override
    public String toString() {
        return "ServerRotateMsg{" +
                "channelNum=" + channelNum +
                ", direction=" + direction +
                '}';
    }
}
