package cn.com.erayton.usagreement.socket.client;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.EmptyPacketData;
import cn.com.erayton.usagreement.model.PacketData;
import cn.com.erayton.usagreement.sendModel.TerminalAuthMsg;
import cn.com.erayton.usagreement.sendModel.TerminalGPSMsg;
import cn.com.erayton.usagreement.sendModel.TerminalGeneralMsg;
import cn.com.erayton.usagreement.sendModel.TerminalParametersMsg;
import cn.com.erayton.usagreement.sendModel.TerminalRegisterMsg;
import cn.com.erayton.usagreement.utils.BCD8421Operator;
import cn.com.erayton.usagreement.utils.LogUtils;

public class SocketClientSender {
    private static final String TAG = "SocketClientSender" ;
    private static String phone ;
    private static Object phoneLock = new Object() ;
//    private static Decoder4LoggingOnly decoder4LoggingOnly ;
//    private static MsgTransformer msgTransformer ;
    private static SocketClient socketClient = null ;

//    private static int msid = 0 ;
//    private static Object msidLock = new Object() ;
//
//
//    public static int getMsid() {synchronized (msidLock){
//        return msid;
//    }}
//
//    public static void setMsid(int msid) {synchronized (msidLock){
//        SocketClientSender.msid = msid;
//    }}

    public static void setInstall(SocketClient socketClient) {
        SocketClientSender.socketClient = socketClient;
    }

    public static void setPhone(String phone){synchronized (phoneLock){
        SocketClientSender.phone = phone ;
    }}

    public static String getPhone() {synchronized (phoneLock){
        return phone;
    }}

    private static PacketData.MsgHeader getHeader(){
        PacketData.MsgHeader header = new PacketData.MsgHeader();
        header.setEncryptionType(0);
        header.setHasSubPackage(false);
        header.setReservedBit(0);
        //  设置终端号
        if (phone == null) {
            LogUtils.e("phone is null.") ;
        }
        header.setTerminalPhone("0"+phone);
//        header.setTerminalPhone(String.format("%012s", phone));
        return header ;
    }


    //  通用回复
    public static boolean sendGeneralReponse(TerminalGeneralMsg.TerminalGeneralInfo terminalGeneralInfo, boolean isAsyn, boolean isUdp){
        TerminalGeneralMsg msg = new TerminalGeneralMsg() ;
        msg.setTerminalGeneralInfo(terminalGeneralInfo);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_CONMOM_RSP);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        return send(msg, isAsyn, isUdp) ;
    }


    public static boolean sendRegister(TerminalRegisterMsg.TerminalRegInfo terminalRegInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;
        // 注册
        TerminalRegisterMsg msg = new TerminalRegisterMsg();
        //  body
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
        header.setMsgId(Constants.TERMINAL_REGISTER);
        header.setMsgBodyLength(msg.getBodyLength());

        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean sendHB(boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;
        //  心跳

        EmptyPacketData msg = new EmptyPacketData();    //  空包
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_HEART_BEAT);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        LogUtils.d("header:"+header);
        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean sendAuth(TerminalAuthMsg.TerminalAuthInfo terminalAuthInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;
        //  鉴权
        TerminalAuthMsg msg = new TerminalAuthMsg();
        //  body
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
        header.setMsgId(Constants.TERMINAL_AUTHEN);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean sendGPS(TerminalGPSMsg.TerminalGPSInfo terminalAuthInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;

        TerminalGPSMsg msg = new TerminalGPSMsg();
        terminalAuthInfo.setBCDTime(BCD8421Operator.getInstance().getBCDTime());
        msg.setTerminalGPSInfo(terminalAuthInfo);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_LOCATION_UPLOAD);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean sendParamenter(TerminalParametersMsg.TerminalParametersInfo parametersInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;

        TerminalParametersMsg msg = new TerminalParametersMsg();
        msg.setTerminalParametersInfo(parametersInfo);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.SERVER_PARAMETERS_QUERY_RSP);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean send(byte[] msg, boolean isAsyn, boolean isUdp){
        if (!isUdp){
            return socketClient.sendTcpMsg(msg, isAsyn) ;
        }else return socketClient.sendUdpMsg(msg, isAsyn) ;
    }

    private static boolean send(PacketData msg, boolean isAsyn, boolean isUdp){
        if (!isUdp){
            return socketClient.sendTcpMsg(msg, isAsyn) ;
        }else return socketClient.sendUdpMsg(msg, isAsyn) ;
    }

}
