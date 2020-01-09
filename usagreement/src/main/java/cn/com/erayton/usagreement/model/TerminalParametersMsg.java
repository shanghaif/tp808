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

//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x20 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0020);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x21 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0021);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x22 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0022);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x27 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0027);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x28 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0028);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x29 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0029);
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 0x31 ---"+ Constants.TERMINAL_PARAMETERS_SETTING_0X0031);
//
//        Log.d(TAG, "inflatePackageBody ---------------  --------------------------------------------------------------------------------------------") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 1 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 2 4 --- "+ bitOperator.parseIntFromBytes(tmp,2,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 5 1 --- "+ bitOperator.parseIntFromBytes(tmp,5,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 7 4 --- "+ bitOperator.parseIntFromBytes(tmp,7,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 10 1 --- "+ bitOperator.parseIntFromBytes(tmp,10,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 2 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 11 4 --- "+ bitOperator.parseIntFromBytes(tmp,11,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 14 1 --- "+ bitOperator.parseIntFromBytes(tmp,14,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 16 4 --- "+ bitOperator.parseIntFromBytes(tmp,16,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 19 1 --- "+ bitOperator.parseIntFromBytes(tmp,19,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 3 ------------------------------------------------------------------------") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 20 4 --- "+ bitOperator.parseIntFromBytes(tmp,20,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 23 1 --- "+ bitOperator.parseIntFromBytes(tmp,23,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 25 4 --- "+ bitOperator.parseIntFromBytes(tmp,25,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 28 1 --- "+ bitOperator.parseIntFromBytes(tmp,28,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 4 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 29 4 --- "+ bitOperator.parseIntFromBytes(tmp,29,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 32 1 --- "+ bitOperator.parseIntFromBytes(tmp,32,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 34 4 --- "+ bitOperator.parseIntFromBytes(tmp,34,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 37 1 --- "+ bitOperator.parseIntFromBytes(tmp,37,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 5 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 38 4 --- "+ bitOperator.parseIntFromBytes(tmp,38,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 41 1 --- "+ bitOperator.parseIntFromBytes(tmp,41,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 43 4 --- "+ bitOperator.parseIntFromBytes(tmp,43,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 46 1 --- "+ bitOperator.parseIntFromBytes(tmp,46,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 6 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 47 4 --- "+ bitOperator.parseIntFromBytes(tmp,47,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 50 1 --- "+ bitOperator.parseIntFromBytes(tmp,50,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 52 4 --- "+ bitOperator.parseIntFromBytes(tmp,52,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 55 1 --- "+ bitOperator.parseIntFromBytes(tmp,55,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 7 ------------------------------------------------------------------------") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 56 4 --- "+ bitOperator.parseIntFromBytes(tmp,56,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 59 1 --- "+ bitOperator.parseIntFromBytes(tmp,59,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 61 4 --- "+ bitOperator.parseIntFromBytes(tmp,61,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 64 1 --- "+ bitOperator.parseIntFromBytes(tmp,64,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 8 ---") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 4 --- "+ bitOperator.parseIntFromBytes(tmp,65,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 68 1 --- "+ bitOperator.parseIntFromBytes(tmp,68,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  --------------------") ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 1 --- "+ bitOperator.parseIntFromBytes(tmp,65,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 2 --- "+ bitOperator.parseIntFromBytes(tmp,65,2)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 3 --- "+ bitOperator.parseIntFromBytes(tmp,65,3)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 6 --- "+ bitOperator.parseIntFromBytes(tmp,65,6)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 65 5 --- "+ bitOperator.parseIntFromBytes(tmp,65,5)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 66 1 --- "+ bitOperator.parseIntFromBytes(tmp,66,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 66 2 --- "+ bitOperator.parseIntFromBytes(tmp,66,2)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 66 3 --- "+ bitOperator.parseIntFromBytes(tmp,66,3)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 66 4 --- "+ bitOperator.parseIntFromBytes(tmp,66,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 66 5 --- "+ bitOperator.parseIntFromBytes(tmp,66,5)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 67 1 --- "+ bitOperator.parseIntFromBytes(tmp,67,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 67 2 --- "+ bitOperator.parseIntFromBytes(tmp,67,2)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 67 3 --- "+ bitOperator.parseIntFromBytes(tmp,67,3)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 67 4 --- "+ bitOperator.parseIntFromBytes(tmp,67,4)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 68 2 --- "+ bitOperator.parseIntFromBytes(tmp,68,2)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 68 3 --- "+ bitOperator.parseIntFromBytes(tmp,68,3)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 69 1 --- "+ bitOperator.parseIntFromBytes(tmp,69,1)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 69 2 --- "+ bitOperator.parseIntFromBytes(tmp,69,2)) ;
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------------------------------------------------") ;

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
