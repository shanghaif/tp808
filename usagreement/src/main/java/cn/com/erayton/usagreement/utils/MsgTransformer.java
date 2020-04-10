package cn.com.erayton.usagreement.utils;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.DataHeader;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.decode.ServerRegisterMsg;
import cn.com.erayton.usagreement.model.encode.HeaderMsg;

/**
 * * Created by Kent_Lee on 2017/3/31.
 */

public class MsgTransformer {
    private static final String TAG = MsgTransformer.class.getSimpleName();
    protected BitOperator bitOperator;

    public MsgTransformer() {
        bitOperator = BitOperator.getInstance();
    }


    public byte[] packageDataToByte(PacketData data) {
        PacketData.MsgHeader msgHeader = data.getMsgHeader();
        HeaderMsg headerMsg = new HeaderMsg() ;
        DataHeader dataHeader = new DataHeader() ;
        headerMsg.setPhone(msgHeader.getTerminalPhone());
        headerMsg.setMsgId(msgHeader.getMsgId());
        return dataHeader.generate808(headerMsg, data.packageDataBody2Byte());
    }

    public PacketData packageByte2Data(byte[] data) {
        try {
            data = doEscape4Receive(data, 0, data.length - 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        PacketData packetData = null;
        PacketData.MsgHeader msgHeader = new PacketData.MsgHeader();
        msgHeader.setMsgId(bitOperator.parseIntFromBytes(data, 0, 2));
        int msgBodyProps = bitOperator.parseIntFromBytes(data, 2, 2);
        msgHeader.setMsgBodyPropsField(msgBodyProps);
        msgHeader.setMsgBodyLength(msgBodyProps & 0x3ff);
        msgHeader.setEncryptionType((msgBodyProps & 0x1c00) >> 10);
        msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
        msgHeader.setReservedBit(((msgBodyProps & 0xC000) >> 14));
        msgHeader.setTerminalPhone(bitOperator.parseBcdStringFromBytes(data, 4, 6));
        msgHeader.setFlowId(bitOperator.parseIntFromBytes(data, 10, 2));
        if (msgHeader.isHasSubPackage()) {
            msgHeader.setPackageInfoField(bitOperator.parseIntFromBytes(data, 12, 4));
            msgHeader.setTotalSubPackage(bitOperator.parseIntFromBytes(data, 12, 2));
            msgHeader.setSubPackageSeq(bitOperator.parseIntFromBytes(data, 12, 2));
        }
        if (msgHeader.getMsgId() == Constants.SERVER_REGISTER_RSP) {        //  注册应答
            packetData = new ServerRegisterMsg();
            packetData.setMsgHeader(msgHeader);
            packetData.inflatePackageBody(data);
            Log.e(TAG, "packageByte2Data_SERVER_REGISTER_RSP: "+ Integer.toHexString(msgHeader.getMsgId()) +"SERVER_REGISTER_RSP.getAuthentication:"+((ServerRegisterMsg) packetData).getAuthentication());
        }else if (msgHeader.getMsgId() == Constants.SERVER_COMMOM_RSP) {    //  平台通用应答
            packetData = new ServerRegisterMsg();
            packetData.setMsgHeader(msgHeader);
            packetData.inflatePackageBody(data);
            Log.e(TAG, "packageByte2 平台通用应答 SERVER_COMMOM_RSP: "+ Integer.toHexString(msgHeader.getMsgId())+"\n packetData"+packetData );
            return null ;
        }else {
            Log.e(TAG, "packageByte2Data: "+ Integer.toHexString(msgHeader.getMsgId()) );
        }
        return packetData;
    }

    public byte[] doEscape4Receive(byte[] bs, int start, int end) throws Exception {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Receive error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }
            for (int i = start; i < end - 1; i++) {
                if (bs[i] == 0x7d && bs[i + 1] == 0x01) {
                    baos.write(0x7d);
                    i++;
                } else if (bs[i] == 0x7d && bs[i + 1] == 0x02) {
                    baos.write(0x7e);
                    i++;
                } else {
                    baos.write(bs[i]);
                }
            }
            for (int i = end - 1; i < bs.length; i++) {
                baos.write(bs[i]);
            }
        } catch (Exception e) {
            Log.e(TAG, "doEscape4Receive: ", e);
            return null;
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        return baos.toByteArray();
    }

    public byte[] doEscape4Send(byte[] bs, int start, int end) throws IOException {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("doEscape4Send error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            for (int i = 0; i < start; i++) {
                baos.write(bs[i]);
            }
            // TODO: 2017/3/31 <= ?
            for (int i = start; i < end; i++) {
                if (bs[i] == 0x7e) {
                    baos.write(0x7d);
                    baos.write(0x02);
                } else if (bs[i] == 0x7d) {
                    baos.write(0x7d);
                    baos.write(0x01);
                } else {
                    baos.write(bs[i]);
                }
            }
            for (int i = end; i < bs.length; i++) {
                baos.write(bs[i]);
            }

        } catch (Exception e) {
            Log.e(TAG, "doEscape4Send: ", e);
            return null;
        } finally {
            if (baos != null) {
                baos.close();
            }
        }
        return baos.toByteArray();
    }

}
