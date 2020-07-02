package cn.com.erayton.usagreement.model.encode;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.LogUtils;

public class TerminalResourceStatusMsg extends PacketData {

    //  流水号
    private int serNum ;
    //  结果
    private int result ;
//    private TerminalResourceInfo info ;

    public int getSerNum() {
        return serNum;
    }

    public void setSerNum(int serNum) {
        this.serNum = serNum;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
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
            baos.write(bitOperator.integerTo2Bytes(getSerNum()));
            //  音视频资源总数 [4]
            baos.write(bitOperator.integerTo1Bytes(getResult()));
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
