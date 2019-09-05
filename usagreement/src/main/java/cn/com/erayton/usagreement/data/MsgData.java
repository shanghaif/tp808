package cn.com.erayton.usagreement.data;

import android.util.Log;

import cn.com.erayton.usagreement.model.EmptyPacketData;
import cn.com.erayton.usagreement.model.PacketData;
import cn.com.erayton.usagreement.model.TerminalAuthMsg;
import cn.com.erayton.usagreement.model.TerminalGPSMsg;
import cn.com.erayton.usagreement.model.TerminalRegisterMsg;
import cn.com.erayton.usagreement.utils.MsgTransformer;

public class MsgData {
    private static final String TAG = "MsgData" ;
    private static MsgData instance = null;
    private static String phone = "12345678901" ;

    /**
     * 必须配置 setPhone
     * */
    public static MsgData getInstance(){
        if (instance == null) instance = new MsgData() ;
        return instance ;
    }

    private MsgData() {

    }

    public void setPhone(String phone){
        this.phone = phone ;
    }

    public static String getPhone() {
        return phone;
    }

    private PacketData.MsgHeader getHeader(){
        PacketData.MsgHeader header = new PacketData.MsgHeader();
        header.setEncryptionType(0);
        header.setHasSubPackage(false);
        header.setReservedBit(0);
        //设置终端号
        if (phone == null) {
            Log.i(TAG, "phone is null.") ;
        }
        header.setTerminalPhone("0"+phone);

        return header ;
    }

    public PacketData registerPackageData(int msgid, TerminalRegisterMsg.TerminalRegInfo terminalRegInfo) {
        // 注册
        TerminalRegisterMsg msg = new TerminalRegisterMsg();
        //body
//        TerminalRegisterMsg.TerminalRegInfo terminalRegInfo = new TerminalRegisterMsg.TerminalRegInfo();
//        terminalRegInfo.setProvinceId(0x00);
//        terminalRegInfo.setCityId(0x00);
//        terminalRegInfo.setManufacturerId("12345");
//        terminalRegInfo.setTerminalType("12345678901234567890");
//        terminalRegInfo.setTerminalId("ABCD123");
//        terminalRegInfo.setLicensePlateColor(0x01);
//        terminalRegInfo.setLicensePlate("粤A:66666");
        msg.setTerminalRegInfo(terminalRegInfo);
        //header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(msgid);
        header.setMsgBodyLength(msg.getBodyLength());

        msg.setMsgHeader(header);

        return msg;
    }

    public PacketData authenticationPackageData(int msgid, TerminalAuthMsg.TerminalAuthInfo terminalAuthInfo) {
        //  鉴权

        TerminalAuthMsg msg = new TerminalAuthMsg();
        //body
//        TerminalRegisterMsg.TerminalRegInfo terminalRegInfo = new TerminalRegisterMsg.TerminalRegInfo();
//        terminalRegInfo.setProvinceId(0x00);
//        terminalRegInfo.setCityId(0x00);
//        terminalRegInfo.setManufacturerId("12345");
//        terminalRegInfo.setTerminalType("12345678901234567890");
//        terminalRegInfo.setTerminalId("ABCD123");
//        terminalRegInfo.setLicensePlateColor(0x01);
//        terminalRegInfo.setLicensePlate("粤A:66666");
        msg.setTerminalAuthInfo(terminalAuthInfo);
        //header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(msgid);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return msg;
    }

//    public PacketData heartPackageData(int msgid) {
//        //  心跳
//
//        EmptyPacketData msg = new EmptyPacketData();    //  空包
//        PacketData.MsgHeader header = getHeader();
//        header.setMsgId(msgid);
//        header.setMsgBodyLength(msg.getBodyLength());
//        msg.setMsgHeader(header);
//        Log.d(TAG, "header:"+header) ;
//        return msg;
//    }

    public byte[] heartPackageData(int msgid) {
        //  心跳

        EmptyPacketData msg = new EmptyPacketData();    //  空包
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(msgid);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        Log.d(TAG, "header:"+header) ;
        MsgTransformer msgTransformer = new MsgTransformer() ;
        return msgTransformer.packageDataToByte(msg) ;
//        return msg;
    }

    public PacketData gpsPackageData(int msgid, TerminalGPSMsg.TerminalGPSInfo terminalAuthInfo) {
        //  GPS
        TerminalGPSMsg msg = new TerminalGPSMsg();
        msg.setTerminalGPSInfo(terminalAuthInfo);
        //header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(msgid);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        return msg;
    }




}
