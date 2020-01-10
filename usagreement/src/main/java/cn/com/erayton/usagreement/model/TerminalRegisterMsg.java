package cn.com.erayton.usagreement.model;

import android.util.Log;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
import cn.com.erayton.usagreement.utils.LogUtils;


/**
 * 终端注册消息
 *
 * @author hylexus
 */
public class TerminalRegisterMsg extends PacketData {
    private static final String TAG = "TerminalRegisterMsg";

    private int registerResult;
    private String authentication;

    private TerminalRegInfo terminalRegInfo;


    public TerminalRegisterMsg() {

    }

    public void setTerminalRegInfo(TerminalRegInfo msgBody) {
        this.terminalRegInfo = msgBody;
    }

    public TerminalRegInfo getTerminalRegInfo() {
        return terminalRegInfo;
    }

    public int getRegisterResult() {
        return registerResult;
    }

    public void setRegisterResult(int registerResult) {
        this.registerResult = registerResult;
    }

    public String getAuthentication() {
        return authentication;
    }

    public void setAuthentication(String authentication) {
        this.authentication = authentication;
    }

    @Override
    public String toString() {
        return "TerminalRegisterMsg{" +
                "registerResult=" + registerResult +
                ", authentication='" + authentication + '\'' +
                ", terminalRegInfo=" + terminalRegInfo +
                ", msgHeader=" + msgHeader +
                ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) +
                ", checkSum=" + checkSum +
                ", answerFlowId=" + answerFlowId +
                '}';
    }


    //    @Override
//    public String toString() {
//        return "TerminalRegisterMsg [terminalRegInfo=" + terminalRegInfo + ", msgHeader=" + msgHeader
//                + ", msgBodyBytes=" + Arrays.toString(msgBodyBytes) + ", checkSum=" + checkSum + "]";
//    }

    @Override
    public int getBodyLength() {
        byte[] bytes = packageDataBody2Byte();
        Log.e(TAG, "getBodyLength: " + bytes.length);
        return bytes.length;
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        LogUtils.e("inflatePackageBody_msgBodyLength: " + msgBodyLength);
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            System.arraycopy(data, Constants.MSGBODY_SUBPACKAGE_START_INDEX, tmp, 0, tmp.length);
        }else {
            System.arraycopy(data, Constants.MSGBODY_START_INDEX, tmp, 0, tmp.length);
        }
        LogUtils.e(HexStringUtils.toHexString(tmp));
        setAnswerFlowId(BitOperator.getInstance().parseIntFromBytes(tmp, 0, 2));
        setRegisterResult(BitOperator.getInstance().parseIntFromBytes(tmp, 2, 1));
        setAuthentication(new String(tmp, 3, tmp.length - 3));
    }


    public byte[] packageDataBody2Byte() {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            //拼接消息体
            // 1. 省域ID word(16)
            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalRegInfo.getProvinceId()));
            //市县域 ID
            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalRegInfo.getCityId()));
            //制造商 ID
            baos.write(terminalRegInfo.getManufacturerId().getBytes());
            //终端型号
            baos.write(terminalRegInfo.getTerminalType().getBytes());
            //终端 ID
            baos.write(terminalRegInfo.getTerminalId().getBytes());
            //车牌颜色
            baos.write(BitOperator.getInstance().integerTo1Byte(terminalRegInfo.getLicensePlateColor()));
            //车牌标识
            baos.write(terminalRegInfo.getLicensePlate().getBytes());
        } catch (IOException e) {
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

    public static class TerminalRegInfo {
        // 省域ID(WORD),设备安装车辆所在的省域，省域ID采用GB/T2260中规定的行政区划代码6位中前两位
        // 0保留，由平台取默认值
        private int provinceId;
        // 市县域ID(WORD) 设备安装车辆所在的市域或县域,市县域ID采用GB/T2260中规定的行 政区划代码6位中后四位
        // 0保留，由平台取默认值
        private int cityId;
        // 制造商ID(BYTE[5]) 5 个字节，终端制造商编码
        private String manufacturerId;
        // 终端型号(BYTE[8]) 八个字节， 此终端型号 由制造商自行定义 位数不足八位的，补空格。
        private String terminalType;
        // 终端ID(BYTE[7]) 七个字节， 由大写字母 和数字组成， 此终端 ID由制造 商自行定义
        private String terminalId;
        /**
         * 车牌颜色(BYTE) 车牌颜色，按照 JT/T415-2006 的 5.4.12 未上牌时，取值为0<br>
         * 0===未上车牌<br>
         * 1===蓝色<br>
         * 2===黄色<br>
         * 3===黑色<br>
         * 4===白色<br>
         * 9===其他
         */
        private int licensePlateColor;
        // 车牌(STRING) 公安交 通管理部门颁 发的机动车号牌
        private String licensePlate;

        public TerminalRegInfo() {
        }

        public int getProvinceId() {
            return provinceId;
        }

        public void setProvinceId(int provinceId) {
            this.provinceId = provinceId;
        }

        public int getCityId() {
            return cityId;
        }

        public void setCityId(int cityId) {
            this.cityId = cityId;
        }

        public String getManufacturerId() {
            return manufacturerId;
        }

        public void setManufacturerId(String manufacturerId) {
            this.manufacturerId = manufacturerId;
        }

        public String getTerminalType() {
            return terminalType;
        }

        public void setTerminalType(String terminalType) {
            this.terminalType = terminalType;
        }

        public String getTerminalId() {
            return terminalId;
        }

        public void setTerminalId(String terminalId) {
            this.terminalId = terminalId;
        }

        public int getLicensePlateColor() {
            return licensePlateColor;
        }

        public void setLicensePlateColor(int licensePlate) {
            this.licensePlateColor = licensePlate;
        }

        public String getLicensePlate() {
            return licensePlate;
        }

        public void setLicensePlate(String licensePlate) {
            this.licensePlate = licensePlate;
        }

        @Override
        public String toString() {
            return "TerminalRegInfo{" +
                    "provinceId=" + provinceId +
                    ", cityId=" + cityId +
                    ", manufacturerId='" + manufacturerId + '\'' +
                    ", terminalType='" + terminalType + '\'' +
                    ", terminalId='" + terminalId + '\'' +
                    ", licensePlateColor=" + licensePlateColor +
                    ", licensePlate='" + licensePlate + '\'' +
                    '}';
        }
    }
}
