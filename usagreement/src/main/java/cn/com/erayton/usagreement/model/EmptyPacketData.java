package cn.com.erayton.usagreement.model;

/**
 * Created by Kent_Lee on 2017/4/1.
 */

public class EmptyPacketData extends PacketData {
    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }
}
