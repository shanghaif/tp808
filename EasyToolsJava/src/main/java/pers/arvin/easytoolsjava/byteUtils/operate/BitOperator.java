package pers.arvin.easytoolsjava.byteUtils.operate;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Arvin
 * @deprecated 位操作
 * */
public class BitOperator {

    private BitOperator() {
    }

    private static final class HOLDER{
        private static final BitOperator INSTANCE = new BitOperator() ;
    }

    public static BitOperator getInstance(){
        return HOLDER.INSTANCE ;
    }

    //region 整形(int)改为byte or byte[]
    /**
     * 把一个整形改为byte
     *
     * @param value
     * @return
     * @throws Exception
     */
    public byte integerTo1Byte(int value) {
        return (byte) (value & 0xFF);
    }

    /**
     * 把一个整形改为1位的byte数组
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
    //endregion


    //region 长整型(long) 改为 byte or byte[]
    /**
     * 把一个长整形改为 byte 数组
     *
     * @param value
     * @return
     * @throws Exception
     */
    public byte[] longToBytes(long value) {
        return longToBytes(value, 8);
    }

    /**
     * 把一个长整形改为 byte 数组
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
    //endregion


    //region byte or byte[] 转化为整形(int)
    /**
     * 把一个byte转化为整形,通常为指令用
     *
     * @param value
     * @return
     * @throws Exception
     */
    public int oneByteToInteger(byte value) {
        return (int) value & 0xFF;
    }

    /**
     * 把一个2位的数组转化为整形
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
     * 把一个3位的数组转化为整形
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
     * 把一个4位的数组转化为整形,通常为指令用
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
    //endregion



    //region byte or byte[] 转化为长整形
    /**
     * 把一个4位的数组转化为长整形
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
     * 把一个byte数组转化长整形
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
    //endregion


    //region 合并字节数组
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
     * 合并字节数组列表
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
    //endregion

}
