package cn.com.erayton.usagreement.model;

import android.util.Log;

import java.util.Arrays;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;

/**
 * 平台下发的设置参数
 * */
public class ServerParametersMsg extends PacketData {
    String TAG = ServerParametersMsg.class.getName() ;

    //    应答流水号     WORD
    private int serialNumber ;
    //  指令数
    private int instructCount ;
    //  GPS 休眠上传间隔
    private int GpsSleepInterval ;
    //  GPS 缺省上传间隔
    private int GpsDefInterval ;


    public int getInstructCount() {
        return instructCount;
    }

    public void setInstructCount(int instructCount) {
        this.instructCount = instructCount;
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

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
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
        setSerialNumber(bitOperator.parseIntFromBytes(tmp,0,1));
        setInstructCount(bitOperator.parseIntFromBytes(tmp,1,1));
//        Log.d(TAG, "inflatePackageBody ---------------  -------------------- 1 1---"+ bitOperator.parseIntFromBytes(tmp,1,1)) ;
        byte[] parameterList = new byte[tmp.length-2];
        System.arraycopy(tmp, 2, parameterList, 0, parameterList.length);
        System.out.println(Arrays.toString(tmp));
        for (int i=0; i< parameterList.length; i = i+4){
            Log.d(TAG, "inflatePackageBody ----------- "+getParameterSettingsString(bitOperator.parseIntFromBytes(parameterList, i, 4))+" ------------------------ "+i+" 4---"+ bitOperator.parseIntFromBytes(parameterList,i,4)) ;
            if (bitOperator.parseIntFromBytes(parameterList,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0029){
                setGpsDefInterval(bitOperator.parseIntFromBytes(parameterList,i+5,4));
            }else if (bitOperator.parseIntFromBytes(parameterList,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0027){
                setGpsSleepInterval(bitOperator.parseIntFromBytes(parameterList,i+5,4));
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
}
