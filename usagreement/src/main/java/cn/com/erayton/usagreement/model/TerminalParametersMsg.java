package cn.com.erayton.usagreement.model;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;

/**
 * 平台组装上传设置参数
 * */
public class TerminalParametersMsg extends PacketData {
    private String TAG = TerminalParametersMsg.class.getName() ;
    private TerminalParametersInfo terminalInfo ;

    public TerminalParametersInfo getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalParametersInfo(TerminalParametersInfo terminalInfo) {
        this.terminalInfo = terminalInfo;
    }



    @Override
    public int getBodyLength() {
        byte[] bytes = packageDataBody2Byte() ;

        return bytes.length;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getSerialNumber()));
            baos.write(bitOperator.integerTo1Bytes(terminalInfo.getInstructCount()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0022));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getLoggedOnInterval()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0027));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getGpsSleepInterval()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0029));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getGpsDefInterval()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0028));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getAlarmInterval()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0020));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getStrategy()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0021));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getPlan()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0030));
            //  baos.write(bitOperator.integerTo3Bytes(040000));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getAngelInflection()));

            baos.write(bitOperator.integerTo4Bytes(Constants.TERMINAL_PARAMETERS_SETTING_0X0031));
            //  baos.write(bitOperator.integerTo1Bytes(02));
            baos.write(bitOperator.integerTo2Bytes(terminalInfo.getThreshold()));


        } catch (IOException e) {
            e.printStackTrace();
        }catch (Exception e) {
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
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        Log.e(TAG, "inflatePackageBody_msgBodyLength: " + msgBodyLength);
        int msgBodyByteStartIndex = 12;
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            msgBodyByteStartIndex = 16;
        }
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        System.arraycopy(data, msgBodyByteStartIndex, tmp, 0, tmp.length);
        BitOperator bitOperator = BitOperator.getInstance();
        System.out.println(Arrays.toString(tmp));
//        Log.d(TAG, "inflatePackageBody --------------- Arrays.toString(tmp) -------------------- tmp.length ---"+ Arrays.toString(tmp)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- tmp.length ---"+ tmp.length) ;
        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0 1 ---"+ bitOperator.parseIntFromBytes(tmp,0,1)) ;
        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0 2 ---"+ bitOperator.parseIntFromBytes(tmp,0,2)) ;
        terminalInfo.setSerialNumber(bitOperator.parseIntFromBytes(tmp,0,1));
        terminalInfo.setInstructCount(bitOperator.parseIntFromBytes(tmp,1,1));
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 1 1---"+ bitOperator.parseIntFromBytes(tmp,1,1)) ;
        byte[] parameterList = new byte[tmp.length-2];
        System.arraycopy(tmp, 2, parameterList, 0, parameterList.length);
        System.out.println(Arrays.toString(tmp));
        for (int i=0; i< parameterList.length; i = i+4){
            Log.d(TAG, "inflatePackageBody ----------- "+getParameterSettingsString(bitOperator.parseIntFromBytes(parameterList, i, 4))+" ------------------------ "+i+" 4---"+ bitOperator.parseIntFromBytes(parameterList,i,4)) ;
            if (bitOperator.parseIntFromBytes(parameterList,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0029){
                terminalInfo.setGpsDefInterval(bitOperator.parseIntFromBytes(parameterList,i+5,4));
            }else if (bitOperator.parseIntFromBytes(parameterList,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0027){
                terminalInfo.setGpsSleepInterval(bitOperator.parseIntFromBytes(parameterList,i+5,4));
            }
            i = i +5 ;
            Log.d(TAG, "inflatePackageBody ----------- value ------------------------ "+i+" 4---"+ bitOperator.parseIntFromBytes(parameterList,i,4)) ;
        }

    }


    private String getParameterSettingsString(int i){
        switch (i){
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0020:      //  0x20
                return "位置汇报策略";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0021:
                return "位置汇报方案";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0022:
                return "驾驶员未登录汇报时间间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0027:
                return "休眠时汇报时间间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0028:
                return "紧急报警时汇报时间间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0029:      //  0x29
                return "缺省时间汇报间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0030:
                return "拐点补传角度";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0031:
                return "电子围栏半径（非法位移阈值）";
            default:
                return "其它"+i;
        }
    }


    public static class TerminalParametersInfo{

        //    应答流水号     WORD
        private int serialNumber ;
        //  指令数
        private int instructCount ;
        //  参数项
        private String itemParamenter ;
        //  GPS 休眠上传间隔      DWORD 休眠时汇报时间间隔，单位为秒（s），>0
        private int GpsSleepInterval ;
        //  GPS 缺省上传间隔      DWORD 缺省时间汇报间隔，单位为秒（s），>0
        private int GpsDefInterval ;

        //  汇报策略        DWORD 位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
        private int strategy ;
        //  汇报方案        DWORD 位置汇报方案，0：根据 ACC 状态；
        //                          1：根据登录状态和 ACC 状态， 先判断登录状态，若登录再根据 ACC 状态
        private int plan ;
        //  驾驶员未登录时间间隔      DWORD 驾驶员未登录汇报时间间隔，单位为秒（s），>0
        private int loggedOnInterval ;
        //  紧急报警时间间隔    DWORD 紧急报警时汇报时间间隔，单位为秒（s），>0
        private int alarmInterval ;
        //  拐点补偿角度          DWORD 拐点补传角度，< 180
        private int angelInflection ;
        //  非法位移阀值          WORD 电子围栏半径（非法位移阈值），单位为米
        private int threshold ;

        public int getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(int serialNumber) {
            this.serialNumber = serialNumber;
        }

        public int getInstructCount() {
            return instructCount;
        }

        public void setInstructCount(int instructCount) {
            this.instructCount = instructCount;
        }

        public String getItemParamenter() {
            return itemParamenter;
        }

        public void setItemParamenter(String itemParamenter) {
            this.itemParamenter = itemParamenter;
        }

        public int getGpsSleepInterval() {
            return GpsSleepInterval;
        }

        public void setGpsSleepInterval(int gpsSleepInterval) {
            GpsSleepInterval = gpsSleepInterval;
        }

        public int getGpsDefInterval() {
            return GpsDefInterval;
        }

        public void setGpsDefInterval(int gpsDefInterval) {
            GpsDefInterval = gpsDefInterval;
        }

        public int getStrategy() {
            return strategy;
        }

        public void setStrategy(int strategy) {
            this.strategy = strategy;
        }

        public int getPlan() {
            return plan;
        }

        public void setPlan(int plan) {
            this.plan = plan;
        }

        public int getLoggedOnInterval() {
            return loggedOnInterval;
        }

        public void setLoggedOnInterval(int loggedOnInterval) {
            this.loggedOnInterval = loggedOnInterval;
        }

        public int getAlarmInterval() {
            return alarmInterval;
        }

        public void setAlarmInterval(int alarmInterval) {
            this.alarmInterval = alarmInterval;
        }

        public int getAngelInflection() {
            return angelInflection;
        }

        public void setAngelInflection(int angelInflection) {
            this.angelInflection = angelInflection;
        }

        public int getThreshold() {
            return threshold;
        }

        public void setThreshold(int threshold) {
            this.threshold = threshold;
        }
    }
}
