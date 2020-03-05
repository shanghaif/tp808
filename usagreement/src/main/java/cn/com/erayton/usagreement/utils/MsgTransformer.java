package cn.com.erayton.usagreement.utils;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.DataHeader;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.decode.ServerRegisterMsg;

/**
 * * Created by Kent_Lee on 2017/3/31.
 */

public class MsgTransformer {
    private static final String TAG = MsgTransformer.class.getSimpleName();
    protected BitOperator bitOperator;
    //protected JT808ProtocolUtils jt808ProtocolUtils;
    protected BCD8421Operator bcd8421Operator;

    public MsgTransformer() {
        bitOperator = BitOperator.getInstance();
        bcd8421Operator = BCD8421Operator.getInstance();
    }


    public byte[] packageDataToByte(PacketData data) {
        PacketData.MsgHeader msgHeader = data.getMsgHeader();
        DataHeader.HeaderMsg headerMsg = new DataHeader.HeaderMsg() ;
        DataHeader dataHeader = new DataHeader() ;
        headerMsg.setPhone(msgHeader.getTerminalPhone());
        headerMsg.setMsgId(msgHeader.getMsgId());
        return dataHeader.generate808(headerMsg, data.packageDataBody2Byte());
    }

//    public byte[] packageDataToByte(PacketData data) {
//        PacketData.MsgHeader msgHeader = data.getMsgHeader();
//        ByteArrayOutputStream baos = null;
//        byte[] result;
//        try {
//            //拼接消息头
//            baos = new ByteArrayOutputStream();
//            // 1. 消息ID word(16)
//            baos.write(bitOperator.integerTo2Bytes(msgHeader.getMsgId()));
//            // 2. 消息体属性 word(16)
//            baos.write(bitOperator.integerTo2Bytes(msgHeader.getMsgBodyPropsField()));
//            // 3. 终端手机号 bcd[6]
//            baos.write(bcd8421Operator.string2Bcd(msgHeader.getTerminalPhone()));
//            // 4. 消息流水号 word(16),按发送顺序从 0 开始循环累加
//            baos.write(bitOperator.integerTo2Bytes(msgHeader.getFlowId()));
//            // 消息包封装项 0 或者 word(16)+word(16)
//            if (msgHeader.isHasSubPackage()) {
//                baos.write(bitOperator.integerTo2Bytes(msgHeader.getPackageInfoField()));
//            }
//            //消息头Bytes
//            byte[] msgHeadBytes = baos.toByteArray();
//            //消息体Bytes
//            byte[] msgBodyBytes = data.packageDataBody2Byte();
//            //(消息头+消息体)Bytes
//            byte[] bs = bitOperator.concatAll(msgHeadBytes, msgBodyBytes);
//            //检验码Bytes
//            byte[] checkCodeBytes = bitOperator.integerTo1Bytes(bitOperator.getCheckSum4JT808(bs, 0, bs.length - 1));
//            //(消息头+消息体+检验码)Bytes
//            byte[] beforeEscape = bitOperator.concatAll(msgHeadBytes, msgBodyBytes, checkCodeBytes);
//            //(消息头+消息体+检验码)Bytes-->转义处理
//            byte[] afterEscape = doEscape4Send(beforeEscape, 0, beforeEscape.length - 1);
//            //标识位
//            byte[] delimiter = bitOperator.integerTo1Bytes(Constants.PKG_DELIMITER);
//            //标识位+消息头+消息体+检验码+标识位
//            result = bitOperator.concatAll(delimiter, afterEscape, delimiter);
////
////
////            //=========================消息头==================================//
////            //[0,1]消息Id
//////        byte[] msgIdb = BitOperator.numToByteArray(msgId, 2);
////            byte[] msgIdb = BitOperator.getInstance().integerTo2Bytes(msgHeader.getMsgId());
////            //[2,3]消息体属性
////            byte[] msgBodyAttributes = msgBodyAttributes(data.packageDataBody2Byte().length, 0);
////            //[4,9]终端手机号 BCD[6](占6位)
////            byte[] terminalPhone = BCD8421Operator.getInstance().string2Bcd(msgHeader.getTerminalPhone());
////            //[10,11]流水号
//////        byte[] flowNum = BitOperator.numToByteArray(0, 2);
////            byte[] flowNum = BitOperator.getInstance().integerTo2Bytes(msgHeader.getFlowId());
////            //[12]消息包封装项 不分包 就没有
//////        byte[] msgHeader = ByteUtil.byteMergerAll(msgIdb, msgBodyAttributes, terminalPhone, flowNum);
////            byte[] msgHeader1 = BitOperator.getInstance().concatAll(msgIdb, msgBodyAttributes, terminalPhone, flowNum);
////            //=========================数据合并（消息头，消息体）=====================//
//////        byte[] bytes = ByteUtil.byteMergerAll(msgHeader, msgBody);
////            byte[] bytes = BitOperator.getInstance().concatAll(msgHeader1, data.packageDataBody2Byte());
////            //=========================计算校验码==================================//
////            String checkCodeHexStr = getBCC(bytes);
//////        byte[] checkCode = HexUtil.hexStringToByte(checkCodeHexStr);
////            byte[] checkCode = HexStringUtils.hexStringToByte(checkCodeHexStr);
////            //=========================合并:消息头 消息体 校验码 得到总数据============//
//////        byte[] AllData = ByteUtil.byteMergerAll(bytes, checkCode);
////            byte[] AllData = BitOperator.getInstance().concatAll(bytes, checkCode);
////
////
////            //  =========================转义 7E和7D================================== //
////            //=========================标识位==================================//
////            byte[] flag = new byte[]{0x7E};
////            String hexStr = HexStringUtils.toHexString(AllData) ;
////            String replaceHexStr = hexStr.replaceAll(FLAG_7D, "0X7D 0X01")
////                    .replaceAll(FLAG_7E, "0X7D 0X02")
////                    // 最后去除空格
////                    .replaceAll(" ", "");
////            //替换好后 转回byte[]
//////        byte[] replaceByte = HexUtil.hexStringToByte(replaceHexStr);
////        Log.i("cjh", "msgHeader.getTerminalPhone()"+msgHeader.getTerminalPhone()+"\t terminalPhone"+terminalPhone+"\n replaceHexStr"+replaceHexStr) ;
////            byte[] replaceByte = HexStringUtils.hexStringToByte(replaceHexStr);
////            //=========================最终传输给服务器的数据==================================//
//////        return ByteUtil.byteMergerAll(flag, replaceByte, flag);
////            return BitOperator.getInstance().concatAll(flag, replaceByte, flag);
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            try {
//                if (baos != null) baos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return result;
//
//    }


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
