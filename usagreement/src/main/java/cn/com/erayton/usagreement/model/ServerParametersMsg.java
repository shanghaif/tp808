package cn.com.erayton.usagreement.model;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * 平台下发的设置参数
 * */
public class ServerParametersMsg extends PacketData {
    String TAG = ServerParametersMsg.class.getName() ;

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

    @Override
    public byte[] packageDataBody2Byte() {
        return new byte[0];
    }

    @Override
    public void inflatePackageBody(byte[] data) {
        int msgBodyLength = getMsgHeader().getMsgBodyLength();
        byte[] tmp = new byte[msgHeader.getMsgBodyLength()];
        // 2. 消息体
        // 有子包信息,消息体起始字节后移四个字节:消息包总数(word(16))+包序号(word(16))
        if (msgHeader.isHasSubPackage()) {
            System.arraycopy(data, Constants.MSGBODY_SUBPACKAGE_START_INDEX, tmp, 0, tmp.length);
        }else {
            System.arraycopy(data, Constants.MSGBODY_START_INDEX, tmp, 0, tmp.length);
        }
    //  看位数截取
        BitOperator bitOperator =BitOperator.getInstance() ;
//        第一位是指令总数
        setInstructCount( BitOperator.getInstance().parseIntFromBytes(tmp,0,1));
        int loc = 1 ;
        LogUtils.d("inflatePackageBody_msgBodyLength: " + msgBodyLength+",getInstructCount():"+getInstructCount()+"\n"+ HexStringUtils.toHexString(tmp));

        while (loc < tmp.length){
            int valueId = bitOperator.parseIntFromBytes(tmp, loc, 4) ;
            int len = bitOperator.parseIntFromBytes(tmp, loc+4, 1) ;
            int value = bitOperator.parseIntFromBytes(tmp, loc+5, len) ;
            LogUtils.d(getParameterSettingsString(valueId)+":"+valueId+"\n "+len+","+value) ;
            switch (valueId){
                case Constants.TERMINAL_PARAMETERS_SETTING_0X0029:
                    setGpsDefInterval(value) ;
                    break;
                case Constants.TERMINAL_PARAMETERS_SETTING_0X0027:
                    setGpsSleepInterval(value) ;
                    break;
                default:
                    break;

            }
            loc += 5 +len ;

        }

//        for (int i=1; i< tmp.length; i = i+9){
//            Log.d(TAG, "inflatePackageBody ----------- "+getParameterSettingsString(bitOperator.parseIntFromBytes(tmp, i, 4))+" ------------------------ "+i+" 4---"+ bitOperator.parseIntFromBytes(tmp,i,4)) ;
////            if (bitOperator.parseIntFromBytes(tmp,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0029){
////                setGpsDefInterval(bitOperator.parseIntFromBytes(tmp,i+5,4));
////            }else if (bitOperator.parseIntFromBytes(tmp,i,4) == Constants.TERMINAL_PARAMETERS_SETTING_0X0027){
////                setGpsSleepInterval(bitOperator.parseIntFromBytes(tmp,i+5,4));
////            }
//            int len = bitOperator.parseIntFromBytes(tmp, i+4, 1) ;
//            LogUtils.d("\n :"+bitOperator.parseIntFromBytes(tmp, i, 4)+","+bitOperator.parseIntFromBytes(tmp, i+4, 1)+","+bitOperator.parseIntFromBytes(tmp, i+5, len)) ;
//
//            Log.d(TAG, "inflatePackageBody ----------- value ------------------------ "+i+" 4---"+ bitOperator.parseIntFromBytes(tmp,i,4)) ;
//        }
//        LogUtils.d("\n"+1+":"+bitOperator.parseIntFromBytes(tmp, 1, 4)+","+bitOperator.parseIntFromBytes(tmp, 5, 1)+","+bitOperator.parseIntFromBytes(tmp, 6, 4)+
//              "\n"+2+":"+bitOperator.parseIntFromBytes(tmp, 10, 4)+","+bitOperator.parseIntFromBytes(tmp, 14, 1)+","+bitOperator.parseIntFromBytes(tmp, 15, 4)+
//              "\n"+3+":"+bitOperator.parseIntFromBytes(tmp, 19, 4)+","+bitOperator.parseIntFromBytes(tmp, 23, 1)+","+bitOperator.parseIntFromBytes(tmp, 24, 4)+
//              "\n"+4+":"+bitOperator.parseIntFromBytes(tmp, 28, 4)+","+bitOperator.parseIntFromBytes(tmp, 32, 1)+","+bitOperator.parseIntFromBytes(tmp, 33, 4)+
//              "\n"+5+":"+bitOperator.parseIntFromBytes(tmp, 37, 4)+","+bitOperator.parseIntFromBytes(tmp, 41, 1)+","+bitOperator.parseIntFromBytes(tmp, 42, 4)+
//              "\n"+6+":"+bitOperator.parseIntFromBytes(tmp, 46, 4)+","+bitOperator.parseIntFromBytes(tmp, 50, 1)+","+bitOperator.parseIntFromBytes(tmp, 51, 4)+
//              "\n"+7+":"+bitOperator.parseIntFromBytes(tmp, 55, 4)+","+bitOperator.parseIntFromBytes(tmp, 59, 1)+","+bitOperator.parseIntFromBytes(tmp, 60, 4)+
//              "\n"+8+":"+bitOperator.parseIntFromBytes(tmp, 64, 4)+","+bitOperator.parseIntFromBytes(tmp, 68, 1)+","+bitOperator.parseIntFromBytes(tmp, 69, 2)
//                );

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
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0029:
                return "缺省时间汇报间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X002C:
                return "缺省距离汇报间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X002D:
                return "驾驶员未登录汇报距离间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X002E:
                return "休眠时汇报距离间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X002F:
                return "紧急报警时汇报距离间隔";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0030:
                return "拐点补传角度";
            case Constants.TERMINAL_PARAMETERS_SETTING_0X0031:
                return "电子围栏半径（非法位移阈值）";
            default:
                return "其它"+i;
        }
    }
}
