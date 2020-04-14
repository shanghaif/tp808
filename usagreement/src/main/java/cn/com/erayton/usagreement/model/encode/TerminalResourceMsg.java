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

    //  流水号
    private int serNum ;
    //  音视频总数
    private int avSize ;
    //  音视频资源列表
    private List<TerminalResourceInfo> info ;

    public TerminalResourceMsg(int serNum, List<TerminalResourceInfo> info) {
        this.serNum = serNum;
        this.info = info;
        this.avSize = info.size() ;
    }

    public TerminalResourceMsg(int serNum) {
        this.serNum = serNum;
        this.info = new ArrayList<>();
        this.avSize = 0 ;
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance() ;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //  流水号 [2]
            baos.write(bitOperator.integerTo1Bytes(serNum));
            //  音视频资源总数 [4]
            baos.write(bitOperator.integerTo4Bytes(info.size()));
//            baos.write(bitOperator.integerTo4Bytes(0));


        }catch (IOException e){
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
