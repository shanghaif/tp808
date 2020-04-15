package cn.com.erayton.usagreement.model.encode;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.model.TerminalAVPropertieInfo;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

public class TerminalAVPropertieMsg extends PacketData {

    private TerminalAVPropertieInfo info ;

    public TerminalAVPropertieInfo getInfo() {
        return info;
    }

    public void setInfo(TerminalAVPropertieInfo info) {
        this.info = info;
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(bitOperator.integerTo1Byte(info.getAudioEncoding()));
            baos.write(bitOperator.integerTo1Byte(info.getAudioChannel()));
            baos.write(bitOperator.integerTo1Byte(info.getAudioRate()));
            baos.write(bitOperator.integerTo1Byte(info.getAudioNum()));
//            baos.write(bitOperator.longToDword(info.getAudioLength()));
            baos.write(bitOperator.longToBytes(info.getAudioLength(), 2));
            baos.write(bitOperator.integerTo1Byte(info.getAudioSupport()));
            baos.write(bitOperator.integerTo1Byte(info.getVideoEncoding()));
            baos.write(bitOperator.integerTo1Byte(info.getMaxAudioChannel()));
            baos.write(bitOperator.integerTo1Byte(info.getMaxVideoChannel()));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return baos.toByteArray();
    }

    @Override
    public int getBodyLength() {
        byte[] bytes = packageDataBody2Byte() ;
        LogUtils.e("getBodyLength: " + bytes.length);
        return bytes.length;
    }
}
