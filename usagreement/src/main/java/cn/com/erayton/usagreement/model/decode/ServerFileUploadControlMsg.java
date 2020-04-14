package cn.com.erayton.usagreement.model.decode;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 文件上传控制
 * */
public class ServerFileUploadControlMsg extends PacketData {

    //  应答流水号
    private int serNum ;
    //  上传控制
    private int uploadControl ;


    public int getSerNum() {
        return serNum;
    }

    public void setSerNum(int serNum) {
        this.serNum = serNum;
    }

    public int getUploadControl() {
        return uploadControl;
    }

    public void setUploadControl(int uploadControl) {
        this.uploadControl = uploadControl;
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
        setSerNum(bitOperator.parseIntFromBytes(tmp, 0, 2));
        setUploadControl(bitOperator.parseIntFromBytes(tmp, 2, 1));

    }

    @Override
    public String toString() {
        return "ServerFileUploadControlMsg{" +
                "serNum=" + serNum +
                ", uploadControl=" + uploadControl +
                '}';
    }
}
