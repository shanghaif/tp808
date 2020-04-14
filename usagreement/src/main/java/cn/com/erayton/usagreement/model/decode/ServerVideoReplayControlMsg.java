package cn.com.erayton.usagreement.model.decode;


import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 平台下发远程录像回放控制
 * 回放控制为3 和4 时 快进或快退倍数字段内容有效,否则置为0
 * 回放控制为5 时 拖动回放位置字段内容有效,否则置为0
 * jt/JTT1078-2016.pdf   P16
 * */
public class ServerVideoReplayControlMsg extends PacketData {

    private int channelNum ;

    //  回放控制
    private int playbackControl ;

    //  快进或快退倍数
    private int multiple ;

    //  拖动回放位置
    private String dragTo ;

    public int getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(int channelNum) {
        this.channelNum = channelNum;
    }

    public int getPlaybackControl() {
        return playbackControl;
    }

    public void setPlaybackControl(int playbackControl) {
        this.playbackControl = playbackControl;
    }

    public String getDragTo() {
        return dragTo;
    }

    public void setDragTo(String dragTo) {
        this.dragTo = dragTo;
    }

    public int getMultiple() {
        return multiple;
    }

    public void setMultiple(int multiple) {
        this.multiple = multiple;
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
        setPlaybackControl(bitOperator.parseIntFromBytes(tmp, 1, 1));
        setMultiple(bitOperator.parseIntFromBytes(tmp, 2, 1));
        setDragTo(bitOperator.parseBcdStringFromBytes(tmp, 3, 6));
    }

    @Override
    public String toString() {
        return "ServerVideoReplayControlMsg{" +
                "channelNum=" + channelNum +
                ", playbackControl=" + playbackControl +
                ", multiple=" + multiple +
                ", dragTo='" + dragTo + '\'' +
                '}';
    }
}
