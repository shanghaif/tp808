package cn.com.erayton.usagreement.utils;

import android.util.Log;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BitOperator {
    private static final String TAG = "BitOperator";

    private BitOperator() {
        //no instance
    }

    public static final class HOLDER {
        private static final BitOperator INSTANCE = new BitOperator();
    }

    public static BitOperator getInstance() {
        return HOLDER.INSTANCE;
    }

    /**
     * 把一个整形该为byte
     *
     * @param value
     * @return
     * @throws Exception
     */
    public byte integerTo1Byte(int value) {
        return (byte) (value & 0xFF);
    }

    /**
     * 把一个整形该为1位的byte数组
     *
     * @param value
     * @return
     * @throws Exception
     */
    public byte[] integerTo1Bytes(int value) {
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
    public byte[] integerTo2Bytes(int value) {
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
    public byte[] integerTo3Bytes(int value) {
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
    public byte[] integerTo4Bytes(int value) {
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
    public int byteToInteger(byte[] value) {
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
    public int oneByteToInteger(byte value) {
        return (int) value & 0xFF;
    }

    /**
     * 把一个2位的数组转化位整形
     *
     * @param value
     * @return
     * @throws Exception
     */
    public int twoBytesToInteger(byte[] value) {
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
    public int threeBytesToInteger(byte[] value) {
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
    public int fourBytesToInteger(byte[] value) {
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
    public long fourBytesToLong(byte[] value) throws Exception {
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
    public long bytes2Long(byte[] value) {
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
    public byte[] longToBytes(long value) {
        return longToBytes(value, 8);
    }

    /**
     * 把一个长整形改为byte数组
     *
     * @param value
     * @return
     * @throws Exception
     */
    public byte[] longToBytes(long value, int len) {
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
    public byte[] generateTransactionID() throws Exception {
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
    public int[] getIntIPValue(String ip) throws Exception {
        String[] sip = ip.split("[.]");
        // if (sip.length != 4) {
        // throw new Exception("error IPAddress");
        // }
        int[] intIP = {Integer.parseInt(sip[0]), Integer.parseInt(sip[1]), Integer.parseInt(sip[2]),
                Integer.parseInt(sip[3])};
        return intIP;
    }

    /**
     * 把byte类型IP地址转化位字符串
     *
     * @param address
     * @return
     * @throws Exception
     */
    public String getStringIPValue(byte[] address) throws Exception {
        int first = this.oneByteToInteger(address[0]);
        int second = this.oneByteToInteger(address[1]);
        int third = this.oneByteToInteger(address[2]);
        int fourth = this.oneByteToInteger(address[3]);

        return first + "." + second + "." + third + "." + fourth;
    }

    /**
     * 合并字节数组
     *
     * @param first
     * @param rest
     * @return
     */
    public byte[] concatAll(byte[] first, byte[]... rest) {
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
    public byte[] concatAll(List<byte[]> rest) {
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

    public int parseIntFromBytes(byte[] data, int startIndex, int length) {
        return this.parseIntFromBytes(data, startIndex, length, 0);
    }

    private int parseIntFromBytes(byte[] data, int startIndex, int length, int defaultVal) {
        try {
            // 字节数大于4,从起始索引开始向后处理4个字节,其余超出部分丢弃
            final int len = length > 4 ? 4 : length;
            byte[] tmp = new byte[len];
            System.arraycopy(data, startIndex, tmp, 0, len);
            return byteToInteger(tmp);
        } catch (Exception e) {
            Log.e(TAG, "parseIntFromBytes: ", e);
            e.printStackTrace();
            return defaultVal;
        }
    }

    public String parseBcdStringFromBytes(byte[] data, int startIndex, int length) {
        return this.parseBcdStringFromBytes(data, startIndex, length, null);
    }

    private String parseBcdStringFromBytes(byte[] data, int startIndex, int length, String defaultVal) {
        try {
            byte[] tmp = new byte[length];
            System.arraycopy(data, startIndex, tmp, 0, length);
            return BCD8421Operator.getInstance().bcd2String(tmp);
        } catch (Exception e) {
            Log.e(TAG, "parseBcdStringFromBytes: ", e);
            return defaultVal;
        }
    }

    public float byte2Float(byte[] bs) {
        return Float.intBitsToFloat(
                (((bs[3] & 0xFF) << 24) + ((bs[2] & 0xFF) << 16) + ((bs[1] & 0xFF) << 8) + (bs[0] & 0xFF)));
    }

    public byte[] float2Byte (float value) {
//        return ByteBuffer.allocate(4).putFloat(value).array();
        return ByteBuffer.allocate(4).putFloat(value).array();
    }


    public float byteBE2Float(byte[] bytes) {
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

    public int getCheckSum4JT808(byte[] bs, int start, int end) {
        if (start < 0 || end > bs.length)
            throw new ArrayIndexOutOfBoundsException("getCheckSum4JT808 error : index out of bounds(start=" + start
                    + ",end=" + end + ",bytes length=" + bs.length + ")");
        int cs = 0;
        for (int i = start; i < end; i++) {
            cs ^= bs[i];
        }
        return cs;
    }

    public int getBitRange(int number, int start, int end) {
        if (start < 0)
            throw new IndexOutOfBoundsException("min index is 0,but start = " + start);
        if (end >= Integer.SIZE)
            throw new IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but end = " + end);

        return (number << Integer.SIZE - (end + 1)) >>> Integer.SIZE - (end - start + 1);
    }

    public int getBitAt(int number, int index) {
        if (index < 0)
            throw new IndexOutOfBoundsException("min index is 0,but " + index);
        if (index >= Integer.SIZE)
            throw new IndexOutOfBoundsException("max index is " + (Integer.SIZE - 1) + ",but " + index);

        return ((1 << index) & number) >> index;
    }

    public int getBitAtS(int number, int index) {
        String s = Integer.toBinaryString(number);
        return Integer.parseInt(s.charAt(index) + "");
    }

    @Deprecated
    public int getBitRangeS(int number, int start, int end) {
        String s = Integer.toBinaryString(number);
        StringBuilder sb = new StringBuilder(s);
        while (sb.length() < Integer.SIZE) {
            sb.insert(0, "0");
        }
        String tmp = sb.reverse().substring(start, end + 1);
        sb = new StringBuilder(tmp);
        return Integer.parseInt(sb.reverse().toString(), 2);
    }

    /*
    * Dword
    * */
    public byte[] longToDword(long value) {
        byte[] data = new byte[4];

        for (int i = 0; i < data.length; i++) {
            data[i] = (byte) (value >> (8 * i));
        }
        return data;
    }

    /*
     * Dword
     * */
    public long dwordBytesToLong(byte[] data) {
        return (data[3] << 8 * 3) + (data[2] << 8 * 2) + (data[1] << 8)
                + data[0];
    }



    /**
     * BCD字节数组===>String
     *
     * @param bytes
     * @return 十进制字符串
     */
    public String bcd2String(byte[] bytes) {
        StringBuilder temp = new StringBuilder(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            // 高四位
            temp.append((bytes[i] & 0xf0) >>> 4);
            // 低四位
            temp.append(bytes[i] & 0x0f);
        }
        return temp.toString().substring(0, 1).equalsIgnoreCase("0") ? temp.toString().substring(1) : temp.toString();
    }

//    /// <summary>
//    /// BCD码转为10进制串(阿拉伯数据)
//    /// </summary>
//    /// <param name="bytes">BCD码 </param>
//    /// <returns>10进制串 </returns>
//    public String bcd2Str(byte[] bytes)
//    {
//        StringBuilder temp = new StringBuilder(bytes.length * 2);
//
//        for (int i = 0; i < bytes.length; i++)
//        {
//            temp.append((byte)((bytes[i] & 0xf0) >> 4));
//            temp.append((byte)(bytes[i] & 0x0f));
//        }
//        return temp.toString().substring(0, 1).Equals("0") ? temp.toString().substring(1) : temp.toString();
//    }
//
//    /// <summary>
//    /// 10进制串转为BCD码
//    /// </summary>
//    /// <param name="asc">10进制串 </param>
//    /// <returns>BCD码 </returns>
//    public byte[] str2Bcd(String asc)
//    {
//        int len = asc.length();
//        int mod = len % 2;
//
//        if (mod != 0)
//        {
//            asc = "0" + asc;
//            len = asc.length();
//        }
//
//        byte[] abt = new byte[len];
//        if (len >= 2)
//        {
//            len = len / 2;
//        }
//
//        byte[] bbt = new byte[len];
//        abt = System.Text.Encoding.ASCII.GetBytes(asc);
//        System.
//        int j, k;
//
//        for (int p = 0; p < asc.length() / 2; p++)
//        {
//            if ((abt[2 * p] >= '0') && (abt[2 * p] <= '9'))
//            {
//                j = abt[2 * p] - '0';
//            }
//            else if ((abt[2 * p] >= 'a') && (abt[2 * p] <= 'z'))
//            {
//                j = abt[2 * p] - 'a' + 0x0a;
//            }
//            else
//            {
//                j = abt[2 * p] - 'A' + 0x0a;
//            }
//
//            if ((abt[2 * p + 1] >= '0') && (abt[2 * p + 1] <= '9'))
//            {
//                k = abt[2 * p + 1] - '0';
//            }
//            else if ((abt[2 * p + 1] >= 'a') && (abt[2 * p + 1] <= 'z'))
//            {
//                k = abt[2 * p + 1] - 'a' + 0x0a;
//            }
//            else
//            {
//                k = abt[2 * p + 1] - 'A' + 0x0a;
//            }
//
//            int a = (j << 4) + k;
//            byte b = (byte)a;
//            bbt[p] = b;
//        }
//        return bbt;
//    }


    /**
     * 字符串==>BCD字节数组
     *
     * @param str
     * @return BCD字节数组
     */
    public byte[] string2Bcd(String str) {
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
        Log.i(TAG,"bcdtime -byte - "+ret.length) ;
        return ret;
    }

    private byte ascII2Bcd(byte asc) {
        if ((asc >= '0') && (asc <= '9'))
            return (byte) (asc - '0');
        else if ((asc >= 'A') && (asc <= 'F'))
            return (byte) (asc - 'A' + 10);
        else if ((asc >= 'a') && (asc <= 'f'))
            return (byte) (asc - 'a' + 10);
        else
            return (byte) (asc - 48);
    }

    public byte[] getBCDTime(){
        Date date = new Date() ;
//        SimpleDateFormat f = new SimpleDateFormat("今天是"+"yyyy年MM月dd日 E kk点mm分");
//        SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd-hh-mm-ss");
        SimpleDateFormat f = new SimpleDateFormat("yyMMddHHmmss");
        Log.i(TAG,"getBCDTime- "+f.format(date)) ;
        return string2Bcd(f.format(date));
    }


    //  ===================================================================


    //数字相关===============================================================================================

    //将数字转大端字节数组　例子: (1,2)=[0,1]
    public byte[] toDDbyte(long value, int length) {
        return reverse(toXDbyte(value, length));
    }

    //将数字转大端字节数组　例子: (1,2)=[0,1]
    public byte[] toDDbyte(String value, int length) {
        try {
            return toDDbyte(Long.valueOf(value), length);
        } catch (Exception e) {
            return null;
        }
    }




    private static final long[] fflong = {0xFF,0xFF00,0xFF0000,0xFF000000,0xFF000000};

//    //将小端byte[]转成数字　例子: ([1,0],0,2)=1
//    public static long toXDint(byte[] byteData,int startPos,int length){
//        if(byteData == null){
//            return 0;
//        }
//        long value = 0;
//        int index = 0, endPos = startPos + length;
//        for(int i=startPos; i<endPos; i++,index++){
//            value |= (byteData[i]<<(index*8)) & fflong[index];
//        }
//        return value;
//    }

    //将数字转成指定长度的字符串   例子: (3,2)=03
    public static String numToStr(Object value, int length){
        try {
            String v = String.valueOf(Long.parseLong(value.toString()));
            while (v.length() < length) {
                v = "0" + v;
            }
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    //数组相关===============================================================================================

    //将bytes数组复制，从startPos位置开始，长度为length
    //例子: ([0,1,2,3],1,3)=[1,2,3]
    public static byte[] subByte(byte[] bytes,int startPos,int length){
        byte[] bs = new byte[length];
        System.arraycopy(bytes, startPos, bs, 0, length);
        return bs;
    }

    //将bytes数组复制，从startPos位置开始直到结尾
    //例子: ([0,1,2,3],2)=[2,3]
    public static byte[] subByte(byte[] bytes,int startPos){
        int length = bytes.length - startPos;
        return subByte(bytes, startPos, length);
    }

    //将多个字节数组合并  例子: (1,[2,3],[4,5])=[1,2,3,4,5]
    public static byte[] concast(Object... bs) {
        int length = 0;
        for (Object b : bs) {
            if (b instanceof byte[]) {
                length += ((byte[]) b).length;
            } else {
                length++;
            }
        }
        byte[] bytes = new byte[length];
        int index = 0;
        for (Object b : bs) {
            if (b instanceof byte[]) {
                byte[] array = (byte[]) b;
                System.arraycopy(array, 0, bytes, index, array.length);
                index += array.length;
            } else {
                bytes[index++] = (byte)b;
            }
        }
        return bytes;
    }

    //将多个字节数组合并  例子: ([2,3],[4,5])=[2,3,4,5]
    public static byte[] concast(byte[]...bs) {
        int len = 0, index = 0;
        for(byte[] b : bs) {
            len += b.length;
        }
        byte[] bytes = new byte[len];
        for(byte[] b : bs) {
            System.arraycopy(b, 0, bytes, index, b.length);
            index += b.length;
        }
        return bytes;
    }

    //将小数组复制到大数据里  例子: ([1,2,0,0,0],2,[3,4,5])  [1,2,0,0,0]->[1,2,3,4,5]
    public static void arraycopy(byte[] bigs, int startPos, byte[] smalls) {
        System.arraycopy(smalls, 0, bigs, startPos, smalls.length);
    }

    //反转数组  例子: ([1,2,3]) = [3,2,1]
    private static byte[] reverse(byte[] data) {
//		ArrayUtils.reverse(data);
        data=invertArray(data);
        return data;
    }
    /**
     * 反转数组
     */
    public static <T> T invertArray(T array) {
        int len = Array.getLength(array);
        Class<?> classz = array.getClass().getComponentType();
        Object dest = Array.newInstance(classz, len);
        System.arraycopy(array, 0, dest, 0, len);
        Object temp;

        for (int i = 0; i < (len / 2); i++) {
            temp = Array.get(dest, i);
            Array.set(dest, i, Array.get(dest, len - i - 1));
            Array.set(dest, len - i - 1, temp);
        }

        return (T)dest;
    }


    public static int indexOf(byte[] bigs, byte[] smalls) {
        int smallLength = smalls.length, length = bigs.length - smallLength;
        for (int i = 0; i <= length; i++) {
            boolean flag = true;
            for (int j = 0, len = smallLength; j < len; j++) {
                if (bigs[i + j] != smalls[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                return i;
            }
        }
        return -1;
    }

    public static int indexOf(byte[] bigs, byte value) {
        int length = bigs.length;
        for (int i = 0; i <= length; i++) {
            if (bigs[i] == value) {
                return i;
            }
        }
        return -1;
    }

    public static int lastIndexOf(byte[] bigs, byte[] smalls) {
        int smallLength = smalls.length, length = bigs.length - smallLength;
        for (int i = length; i >= 0; i--) {
            boolean flag = true;
            for (int j = 0, len = smallLength; j < len; j++) {
                if (bigs[i + j] != smalls[j]) {
                    flag = false;
                    break;
                }
            }
            if (flag == true) {
                return i;
            }
        }
        return -1;
    }

    //字符串相关===============================================================================================

    //字符串转成指定长度的字节数组
    public static byte[] strToBytes(String str, int length) {
        try {
            byte[] bytes = str.getBytes(), b = new byte[length];
            System.arraycopy(bytes, 0, b, length-bytes.length, bytes.length);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    //字符串转成指定长度的字节数组
    public static byte[] strToBytes(String str) {
        try {
            return str.getBytes();
        } catch (Exception e) {
            return null;
        }
    }

    //字节数组转成字符串
    public String bytesToStr(byte[] bytes, int startPos, int length) {
        try {
            return new String(subByte(bytes,startPos,length)).trim();
        } catch (Exception e) {
            return null;
        }
    }

    //字符串转成指定长度的字节数组
    public static byte[] strToBytes_UTF8(String str, int length) {
        try {
            byte[] bytes = str.getBytes("UTF-8"), b = new byte[length];
            System.arraycopy(bytes, 0, b, length-bytes.length, bytes.length);
            return b;
        } catch (Exception e) {
            return null;
        }
    }

    //字符串转成指定长度的字节数组
    public static byte[] strToBytes_UTF8(String str) {
        try {
            return str.getBytes("UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    //字节数组转成字符串
    public String bytesToStr_UTF8(byte[] bytes, int startPos, int length) {
        try {
            return new String(subByte(bytes,startPos,length),"UTF-8").trim();
        } catch (Exception e) {
            return null;
        }
    }

    //字节数组转成字符串
    public static String bytesToStr_UTF8(byte[] bytes) {
        try {
            return new String(bytes,"UTF-8").trim();
        } catch (Exception e) {
            return null;
        }
    }


    //************************************************
//    /**
//     * 把一个整形改为2位的byte数组
//     *
//     * @param value
//     * @return
//     * @throws Exception
//     */
//    public static byte[] integerTo2Bytes(int value) {
//        byte[] result = new byte[2];
//        result[0] = (byte) ((value >>> 8) & 0xFF);
//        result[1] = (byte) (value & 0xFF);
//        return result;
//    }

//    /**
//     * 字符串==>BCD字节数组
//     *
//     * @param str
//     * @return BCD字节数组
//     */
//    public byte[] string2Bcd(String str) {
//        // 奇数,前补零
//        if ((str.length() & 0x1) == 1) {
//            str = "0" + str;
//        }
//
//        byte ret[] = new byte[str.length() / 2];
//        byte bs[] = str.getBytes();
//        for (int i = 0; i < ret.length; i++) {
//
//            byte high = ascII2Bcd(bs[2 * i]);
//            byte low = ascII2Bcd(bs[2 * i + 1]);
//
//            ret[i] = (byte) ((high << 4) | low);
//        }
//        return ret;
//    }

    // 字符串前补0
    public static String addZero(String str, int len) {
        if (str.length() < len) {
            int l = str.length();
            for (int i = 0; i < len - l; i++) {
                str = "0" + str;
            }
        }
        return str;
    }

//    /**
//     * 读取buffer里面固定长度的字节,但不清理释放引用
//     * @param buffer
//     * @return
//     */
//    public static byte[] readBufLen(ByteBuf buffer,int len){
//        byte[] data = new byte[len];
//        buffer.readBytes(data);
//        return data;
//    }
//
//    /**
//     * 读取buffer里面的所有字节,并释放引用
//     * @param buffer
//     * @return
//     */
//    public static byte[] readBuf(ByteBuf buffer){
//        byte[] data = new byte[buffer.readableBytes()];
//        buffer.readBytes(data);
//        buffer.release();
//        return data;
//    }

    public static String getHexStr(byte temp){
        return Integer.toHexString((temp & 0xFF)+0x100).substring(1).toUpperCase();
    }


    public static String getHexStr(byte[] temp){
        StringBuilder sb = new StringBuilder();
        for (int index = 0; index < temp.length; index++) {
            sb.append(getHexStr(temp[index]));
        }
        return sb.toString();
    }

//  大小端
//将大端byte[]转成数字　例子: ([0,1],0,2)=1
public long toDDint(byte[] byteData,int startPos,int length){
    byteData = reverse(subByte(byteData, startPos, length));
    return toXDint(byteData,0,length);
}


    //转数字转小端byte[]　例子: (1,2)=[1,0]
    public static byte[] toXDbyte(long value, int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = (byte) ((value >> i * 8) & 0xFF);
        }
        return result;
    }

    //转数字转小端byte[]　例子: (1,2)=[1,0]
    public static byte[] toXDbyte(String value, int length) {
        try {
            return toXDbyte(Long.valueOf(value), length);
        } catch (Exception e) {
            return null;
        }
    }

    //将小端byte[]转成数字　例子: ([1,0],0,2)=1
    public static long toXDint(byte[] byteData,int startPos,int length){
        if(byteData == null){
            return 0;
        }
        long value = 0;
        int index = 0, endPos = startPos + length;
        for(int i=startPos; i<endPos; i++,index++){
            value |= (byteData[i]<<(index*8)) & fflong[index];
        }
        return value;
    }
}
