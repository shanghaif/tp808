package cn.com.erayton.usagreement.model.decode;


import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 实时音视频传输状态通知
 * */
public class ServerTransferStatusMsg extends PacketData {
    private int channelNum ;
    //  丢包率    Fix the package rate
    private int packageRate ;


    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getPackageRate() {
        return packageRate;
    }

    public void setPackageRate(int packageRate) {
        this.packageRate = packageRate*100;
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
        setPackageRate(bitOperator.parseIntFromBytes(tmp, 1, 1));
    }



}
