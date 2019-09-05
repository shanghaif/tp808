package cn.com.erayton.usagreement.model;


import java.util.Arrays;
import java.util.List;

public class DataHeader {

    private static final String FLAG_7D ="0X7D" ;
    private static final String FLAG_7E ="0X7E" ;


//    /**
//     * 包装808数据
//     *
//     * @param msgId   消息id
//     * @param phone   终端手机号
//     * @param msgBody 消息体
//     * @return
//     */
    /**
     * 包装808数据
     *
     * @param headerMsg 消息头
     * @param msgBody 消息体
     * @return
     */
//    public byte[] generate808(int msgId, String phone, byte[] msgBody) {
    public byte[] generate808(HeaderMsg headerMsg, byte[] msgBody) {
        //=========================标识位==================================//
        byte[] flag = new byte[]{0x7E};
        //=========================消息头==================================//
        //[0,1]消息Id
//        byte[] msgIdb = BitOperator.integerTo2Bytes(msgId);
        byte[] msgIdb = BitOperator.integerTo2Bytes(headerMsg.getMsgId());
        //[2,3]消息体属性
        byte[] msgBodyAttribute = msgBodyAttributes(msgBody.length, 0);
        //[4,9]终端手机号 BCD[6](占6位)
        byte[] terminalPhone = BCD8421Operater.string2Bcd(headerMsg.getPhone());
        //[10,11]流水号
        byte[] flowNum = BitOperator.integerTo2Bytes(0);
        //[12]消息包封装项 不分包 就没有
        byte[] msgHeader = BitOperator.concatAll(msgIdb, msgBodyAttribute, terminalPhone, flowNum);
        //=========================数据合并（消息头，消息体）=====================//
        byte[] bytes = BitOperator.concatAll(msgHeader, msgBody);
        //=========================计算校验码==================================//
        String checkCodeHexStr = getBCC(bytes);
        byte[] checkCode = HexStringUtils.hexStringToByte(checkCodeHexStr);
        //=========================合并:消息头 消息体 校验码 得到总数据============//
        byte[] AllData = BitOperator.concatAll(bytes, checkCode);
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
        return BitOperator.concatAll(flag, replaceByte, flag);
    }

    /**
     * 生成消息体属性
     *
     * @param subpackage [13]是否分包 0:不分包 1:分包
     */
    private byte[] msgBodyAttributes(int msgLength, int subpackage) {
//        byte[] length = BitOperator.numToByteArray(msgLength, 2);
        byte[] length = BitOperator.integerTo2Bytes(msgLength);
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
        return BitOperator.integerTo2Bytes(msgBodyAttr);
    }


    /**
     * BCC 校验算法
     *
     * @param data
     * @return 十六进制
     */
    private String getBCC(byte[] data) {
        String ret = "";
        byte BCC[] = new byte[1];
        for (int i = 0; i < data.length; i++) {
            BCC[0] = (byte) (BCC[0] ^ data[i]);
        }
        String hex = Integer.toHexString(BCC[0] & 0xFF);
        if (hex.length() == 1) {
            hex = '0' + hex;
        }
        ret += hex.toUpperCase();
        return ret;
    }

    public static class HeaderMsg{
        private int msgId ;
        private String phone ;

        public int getMsgId() {
            return msgId;
        }

        public void setMsgId(int msgId) {
            this.msgId = msgId;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }



    // =================== HexStringUtils ===================
static class HexStringUtils{
    private static final char[] DIGITS_HEX = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };


    /** 把16进制字符串转换成字节数组
     * @param hex
     * @return
     */
    private static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }


    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }


    public static String toHexString(byte[] bs) {
        return new String(encodeHex(bs));
    }

    private static char[] encodeHex(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_HEX[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_HEX[0x0F & data[i]];
        }
        return out;
    }
}

    // =================== BCD8421Operater ===================
    static class BCD8421Operater {

        /**
         * 字符串==>BCD字节数组
         *
         * @param str
         * @return BCD字节数组
         */
        public static byte[] string2Bcd(String str) {
            // 奇数,前补零
            if ((str.length() & 0x1) == 1) {
                str = "0" + str;
            }

            byte ret[] = new byte[str.length() / 2];
            byte bs[] = str.getBytes();
            for (int i = 0; i < ret.length; i++) {

                byte high = ascII2Bcd(bs[2 * i]);
                byte low = ascII2Bcd(bs[2 * i + 1]);

                // TODO 只遮罩BCD低四位?
                ret[i] = (byte) ((high << 4) | low);
            }
            return ret;
        }

        private static byte ascII2Bcd(byte asc) {
            if ((asc >= '0') && (asc <= '9'))
                return (byte) (asc - '0');
            else if ((asc >= 'A') && (asc <= 'F'))
                return (byte) (asc - 'A' + 10);
            else if ((asc >= 'a') && (asc <= 'f'))
                return (byte) (asc - 'a' + 10);
            else
                return (byte) (asc - 48);
        }
    }

    // =================== BitOperator ===================
    static class BitOperator{
        /**
         * 把一个整形该为byte
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte integerTo1Byte(int value) {
            return (byte) (value & 0xFF);
        }

        /**
         * 把一个整形该为1位的byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] integerTo1Bytes(int value) {
            byte[] result = new byte[1];
            result[0] = (byte) (value & 0xFF);
            return result;
        }

        /**
         * 把一个整形改为2位的byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] integerTo2Bytes(int value) {
            byte[] result = new byte[2];
            result[0] = (byte) ((value >>> 8) & 0xFF);
            result[1] = (byte) (value & 0xFF);
            return result;
        }

        /**
         * 把一个整形改为3位的byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] integerTo3Bytes(int value) {
            byte[] result = new byte[3];
            result[0] = (byte) ((value >>> 16) & 0xFF);
            result[1] = (byte) ((value >>> 8) & 0xFF);
            result[2] = (byte) (value & 0xFF);
            return result;
        }

        /**
         * 把一个整形改为4位的byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] integerTo4Bytes(int value){
            byte[] result = new byte[4];
            result[0] = (byte) ((value >>> 24) & 0xFF);
            result[1] = (byte) ((value >>> 16) & 0xFF);
            result[2] = (byte) ((value >>> 8) & 0xFF);
            result[3] = (byte) (value & 0xFF);
            return result;
        }

        /**
         * 把byte[]转化位整形,通常为指令用
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static int byteToInteger(byte[] value) {
            int result;
            if (value.length == 1) {
                result = oneByteToInteger(value[0]);
            } else if (value.length == 2) {
                result = twoBytesToInteger(value);
            } else if (value.length == 3) {
                result = threeBytesToInteger(value);
            } else if (value.length == 4) {
                result = fourBytesToInteger(value);
            } else {
                result = fourBytesToInteger(value);
            }
            return result;
        }

        /**
         * 把一个byte转化位整形,通常为指令用
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static int oneByteToInteger(byte value) {
            return (int) value & 0xFF;
        }

        /**
         * 把一个2位的数组转化位整形
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static int twoBytesToInteger(byte[] value) {
            // if (value.length < 2) {
            // throw new Exception("Byte array too short!");
            // }
            int temp0 = value[0] & 0xFF;
            int temp1 = value[1] & 0xFF;
            return ((temp0 << 8) + temp1);
        }

        /**
         * 把一个3位的数组转化位整形
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static int threeBytesToInteger(byte[] value) {
            int temp0 = value[0] & 0xFF;
            int temp1 = value[1] & 0xFF;
            int temp2 = value[2] & 0xFF;
            return ((temp0 << 16) + (temp1 << 8) + temp2);
        }

        /**
         * 把一个4位的数组转化位整形,通常为指令用
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static int fourBytesToInteger(byte[] value) {
            // if (value.length < 4) {
            // throw new Exception("Byte array too short!");
            // }
            int temp0 = value[0] & 0xFF;
            int temp1 = value[1] & 0xFF;
            int temp2 = value[2] & 0xFF;
            int temp3 = value[3] & 0xFF;
            return ((temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
        }

        /**
         * 把一个4位的数组转化位整形
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static long fourBytesToLong(byte[] value) throws Exception {
            // if (value.length < 4) {
            // throw new Exception("Byte array too short!");
            // }
            int temp0 = value[0] & 0xFF;
            int temp1 = value[1] & 0xFF;
            int temp2 = value[2] & 0xFF;
            int temp3 = value[3] & 0xFF;
            return (((long) temp0 << 24) + (temp1 << 16) + (temp2 << 8) + temp3);
        }

        /**
         * 把一个数组转化长整形
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static long bytes2Long(byte[] value) {
            long result = 0;
            int len = value.length;
            int temp;
            for (int i = 0; i < len; i++) {
                temp = (len - 1 - i) * 8;
                if (temp == 0) {
                    result += (value[i] & 0x0ff);
                } else {
                    result += (value[i] & 0x0ff) << temp;
                }
            }
            return result;
        }

        /**
         * 把一个长整形改为byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] longToBytes(long value){
            return longToBytes(value, 8);
        }

        /**
         * 把一个长整形改为byte数组
         *
         * @param value
         * @return
         * @throws Exception
         */
        public static byte[] longToBytes(long value, int len) {
            byte[] result = new byte[len];
            int temp;
            for (int i = 0; i < len; i++) {
                temp = (len - 1 - i) * 8;
                if (temp == 0) {
                    result[i] += (value & 0x0ff);
                } else {
                    result[i] += (value >>> temp) & 0x0ff;
                }
            }
            return result;
        }

        /**
         * 得到一个消息ID
         *
         * @return
         * @throws Exception
         */
        public static byte[] generateTransactionID() throws Exception {
            byte[] id = new byte[16];
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 0, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 2, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 4, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 6, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 8, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 10, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 12, 2);
            System.arraycopy(integerTo2Bytes((int) (Math.random() * 65536)), 0, id, 14, 2);
            return id;
        }

        /**
         * 把IP拆分位int数组
         *
         * @param ip
         * @return
         * @throws Exception
         */
        public static int[] getIntIPValue(String ip) throws Exception {
            String[] sip = ip.split("[.]");
            // if (sip.length != 4) {
            // throw new Exception("error IPAddress");
            // }
            int[] intIP = { Integer.parseInt(sip[0]), Integer.parseInt(sip[1]), Integer.parseInt(sip[2]),
                    Integer.parseInt(sip[3]) };
            return intIP;
        }

        /**
         * 把byte类型IP地址转化位字符串
         *
         * @param address
         * @return
         * @throws Exception
         */
        public static String getStringIPValue(byte[] address) throws Exception {
            int first = oneByteToInteger(address[0]);
            int second = oneByteToInteger(address[1]);
            int third = oneByteToInteger(address[2]);
            int fourth = oneByteToInteger(address[3]);

            return first + "." + second + "." + third + "." + fourth;
        }

        /**
         * 合并字节数组
         *
         * @param first
         * @param rest
         * @return
         */
        public static byte[] concatAll(byte[] first, byte[]... rest) {
            int totalLength = first.length;
            for (byte[] array : rest) {
                if (array != null) {
                    totalLength += array.length;
                }
            }
            byte[] result = Arrays.copyOf(first, totalLength);
            int offset = first.length;
            for (byte[] array : rest) {
                if (array != null) {
                    System.arraycopy(array, 0, result, offset, array.length);
                    offset += array.length;
                }
            }
            return result;
        }

        /**
         * 合并字节数组
         *
         * @param rest
         * @return
         */
        public static byte[] concatAll(List<byte[]> rest) {
            int totalLength = 0;
            for (byte[] array : rest) {
                if (array != null) {
                    totalLength += array.length;
                }
            }
            byte[] result = new byte[totalLength];
            int offset = 0;
            for (byte[] array : rest) {
                if (array != null) {
                    System.arraycopy(array, 0, result, offset, array.length);
                    offset += array.length;
                }
            }
            return result;
        }

        public static float byte2Float(byte[] bs) {
            return Float.intBitsToFloat(
                    (((bs[3] & 0xFF) << 24) + ((bs[2] & 0xFF) << 16) + ((bs[1] & 0xFF) << 8) + (bs[0] & 0xFF)));
        }

        public static float byteBE2Float(byte[] bytes) {
            int l;
            l = bytes[0];
            l &= 0xff;
            l |= ((long) bytes[1] << 8);
            l &= 0xffff;
            l |= ((long) bytes[2] << 16);
            l &= 0xffffff;
            l |= ((long) bytes[3] << 24);
            return Float.intBitsToFloat(l);
        }

        public static int getCheckSum4JT808(byte[] bs, int start, int end) {
            if (start < 0 || end > bs.length)
                throw new ArrayIndexOutOfBoundsException("getCheckSum4JT808 error : index out of bounds(start=" + start
                        + ",end=" + end + ",bytes length=" + bs.length + ")");
            int cs = 0;
            for (int i = start; i < end; i++) {
                cs ^= bs[i];
            }
            return cs;
        }

        public static int getBitRange(int number, int start, int end) {
            if (start < 0)
                throw new IndexOutOfBoundsException("min index is 0,but start = " + start);
            if (end >= Integer.SIZE)
                throw new IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but end = " + end);

            return (number << Integer.SIZE - (end + 1)) >>> Integer.SIZE - (end - start + 1);
        }

        public static int getBitAt(int number, int index) {
            if (index < 0)
                throw new IndexOutOfBoundsException("min index is 0,but " + index);
            if (index >= Integer.SIZE)
                throw new IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but " + index);

            return ((1 << index) & number) >> index;
        }

        public static int getBitAtS(int number, int index) {
            String s = Integer.toBinaryString(number);
            return Integer.parseInt(s.charAt(index) + "");
        }

        @Deprecated
        public static int getBitRangeS(int number, int start, int end) {
            String s = Integer.toBinaryString(number);
            StringBuilder sb = new StringBuilder(s);
            while (sb.length() < Integer.SIZE) {
                sb.insert(0, "0");
            }
            String tmp = sb.reverse().substring(start, end + 1);
            sb = new StringBuilder(tmp);
            return Integer.parseInt(sb.reverse().toString(), 2);
        }
    }

}
