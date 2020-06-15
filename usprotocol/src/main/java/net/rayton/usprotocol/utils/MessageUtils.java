package net.rayton.usprotocol.utils;

import net.rayton.usprotocol.ObjectHeader;

public class MessageUtils {
    private static final String FLAG_7D ="0X7D" ;
    private static final String FLAG_7E ="0X7E" ;

    /**
     * 包装808数据
     *
     * @param headerMsg 消息头
     * @param msgBody 消息体
     * @return
     */
//    public byte[] generate808(int msgId, String phone, byte[] msgBody) {
    public byte[] generate808(ObjectHeader headerMsg, byte[] msgBody) {
        //=========================标识位==================================//
        byte[] flag = new byte[]{0x7E};
        //=========================消息头==================================//
        //[0,1]消息Id
//        byte[] msgIdb = BitOperator.integerTo2Bytes(msgId);
        byte[] msgIdb = BitOperator.getInstance().integerTo2Bytes(headerMsg.getMsgId());
        //[2,3]消息体属性
        byte[] msgBodyAttribute = msgBodyAttributes(msgBody.length, 0);
        //[4,9]终端手机号 BCD[6](占6位)
        byte[] terminalPhone = BitOperator.getInstance().string2Bcd(headerMsg.getTerminalPhone());
        //[10,11]流水号
        byte[] flowNum = BitOperator.getInstance().integerTo2Bytes(0);
        //[12]消息包封装项 不分包 就没有
        byte[] msgHeader = BitOperator.getInstance().concatAll(msgIdb, msgBodyAttribute, terminalPhone, flowNum);
        //=========================数据合并（消息头，消息体）=====================//
        byte[] bytes = BitOperator.getInstance().concatAll(msgHeader, msgBody);
        //=========================计算校验码==================================//
        String checkCodeHexStr = BitOperator.getInstance().getBCC(bytes);
        byte[] checkCode = HexStringUtils.hexStringToByte(checkCodeHexStr);
        //=========================合并:消息头 消息体 校验码 得到总数据============//
        byte[] AllData = BitOperator.getInstance().concatAll(bytes, checkCode);
        //=========================转义 7E和7D==================================//
        // 转成16进制字符串
        String hexStr = HexStringUtils.toHexString(AllData);
        // 替换 7E和7D
        String replaceHexStr = hexStr.replaceAll(FLAG_7D, "0X7D 0X01")
                .replaceAll(FLAG_7E, "0X7D 0X02")
                // 最后去除空格
                .replaceAll(" ", "");
        //替换好后 转回byte[]
        byte[] replaceByte = HexStringUtils.hexStringToByte(replaceHexStr);
        //=========================最终传输给服务器的数据==================================//
        return BitOperator.getInstance().concatAll(flag, replaceByte, flag);
    }

    /**
     * 生成消息体属性
     *
     * @param subpackage [13]是否分包 0:不分包 1:分包
     */
    private byte[] msgBodyAttributes(int msgLength, int subpackage) {
//        byte[] length = BitOperator.numToByteArray(msgLength, 2);
        byte[] length = BitOperator.getInstance().integerTo2Bytes(msgLength);
        //[0,9]消息体长度
        String msgBodyLength = "" +
                //第一个字节最后2bit
                +(byte) ((length[0] >> 1) & 0x1) + (byte) ((length[0] >> 0) & 0x1)
                //第二个字节8bit
                + (byte) ((length[1] >> 7) & 0x1) + (byte) ((length[1] >> 6) & 0x1)
                + (byte) ((length[1] >> 5) & 0x1) + (byte) ((length[1] >> 4) & 0x1)
                + (byte) ((length[1] >> 3) & 0x1) + (byte) ((length[1] >> 2) & 0x1)
                + (byte) ((length[1] >> 1) & 0x1) + (byte) ((length[1] >> 0) & 0x1);
        //[10,12]数据加密方式 0 表示不加密
        String encryption = "000";
        //[13]分包
        String subpackageB = String.valueOf(subpackage);
        //[14,15]保留位
        String reserve = "00";
        String msgAttributes = reserve + subpackageB + encryption + msgBodyLength;
        // 消息体属性
        int msgBodyAttr = Integer.parseInt(msgAttributes, 2);
//        return BitOperator.numToByteArray(msgBodyAttr, 2);
        return BitOperator.getInstance().integerTo2Bytes(msgBodyAttr);
    }
}
