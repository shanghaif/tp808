package cn.com.erayton.usagreement.model.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.model.model.TerminalRiderShipInfo;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

public class TerminalRiderShipMsg extends PacketData {
    private TerminalRiderShipInfo info ;


    public TerminalRiderShipInfo getInfo() {
        return info;
    }

    public void setInfo(TerminalRiderShipInfo info) {
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
           baos.write(bitOperator.string2Bcd(info.getStartTime()));
           baos.write(bitOperator.string2Bcd(info.getEndTime()));
           baos.write(bitOperator.integerTo2Bytes(info.getGetonNum()));
           baos.write(bitOperator.integerTo2Bytes(info.getGetoffNum()));
        } catch (IOException e) {
            e.printStackTrace();
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
        byte[] bytes = packageDataBody2Byte();
        LogUtils.e("getBodyLength: " + bytes.length);
        return bytes.length;
    }
}
