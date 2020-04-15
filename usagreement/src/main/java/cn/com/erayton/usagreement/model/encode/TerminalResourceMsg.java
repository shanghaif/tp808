package cn.com.erayton.usagreement.model.encode;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

public class TerminalResourceMsg extends PacketData {

    // 流水号
    private int serNum;
    // 音视频总数
    private int avSize;
    // 音视频资源列表
    private List<TerminalResourceInfo> infoList;
    // private TerminalResourceInfo info;

    // public TerminalResourceMsg(int serNum, TerminalResourceInfo info) {
    //     this.serNum = serNum;
    //     this.info = info;
    //     this.avSize = 1;
    // }


    private int getSerNum() {
        return serNum;
    }

    public void setSerNum(int serNum) {
        this.serNum = serNum;
    }

    private int getAvSize() {
        return avSize;
    }

    private void setAvSize(int avSize) {
        this.avSize = avSize;
    }

    private List<TerminalResourceInfo> getInfoList() {
        return infoList;
    }

    public void setInfoList(List<TerminalResourceInfo> infoList) {
        this.infoList = infoList;
        setAvSize(infoList.size());
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            // 流水号 [2]
            baos.write(bitOperator.integerTo2Bytes(getSerNum()));
            // 音视频资源总数 [4]
            baos.write(bitOperator.integerTo4Bytes(getAvSize()));
            // baos.write(bitOperator.integerTo4Bytes(0));
            // -------------------------------- on info
            if (getAvSize() != 0) {
                for (TerminalResourceInfo info : getInfoList()) {
                    baos.write(bitOperator.integerTo1Byte(info.getChannelNum()));
                    baos.write(bitOperator.string2Bcd(info.getStartTime()));
                    baos.write(bitOperator.string2Bcd(info.getEndTime()));
                    baos.write(info.getWrang());
                    baos.write(bitOperator.integerTo1Byte(info.getResourceType()));
                    baos.write(bitOperator.integerTo1Byte(info.getSteamType()));
                    baos.write(bitOperator.integerTo1Byte(info.getMemoryType()));
                    baos.write(bitOperator.longToBytes(info.getFileSize(), 4));
                }
            }
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