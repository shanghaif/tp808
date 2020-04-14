package cn.com.erayton.usagreement.socket.client;

import android.util.Log;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.EmptyPacketData;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.encode.TerminalAuthMsg;
import cn.com.erayton.usagreement.model.encode.TerminalGPSMsg;
import cn.com.erayton.usagreement.model.encode.TerminalGeneralMsg;
import cn.com.erayton.usagreement.model.encode.TerminalParametersMsg;
import cn.com.erayton.usagreement.model.encode.TerminalRegisterMsg;
import cn.com.erayton.usagreement.model.encode.TerminalResourceMsg;
import cn.com.erayton.usagreement.model.model.TerminalAuthInfo;
import cn.com.erayton.usagreement.model.model.TerminalGPSInfo;
import cn.com.erayton.usagreement.model.model.TerminalGeneralInfo;
import cn.com.erayton.usagreement.model.model.TerminalParametersInfo;
import cn.com.erayton.usagreement.model.model.TerminalRegInfo;
import cn.com.erayton.usagreement.utils.BitOperator;
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
    public static boolean sendGeneralReponse(TerminalGeneralInfo terminalGeneralInfo, boolean isAsyn, boolean isUdp){
        TerminalGeneralMsg msg = new TerminalGeneralMsg() ;
        msg.setTerminalGeneralInfo(terminalGeneralInfo);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_CONMOM_RSP);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        return send(msg, isAsyn, isUdp) ;
    }


    public static boolean sendRegister(TerminalRegInfo terminalRegInfo, boolean isAsyn, boolean isUdp){
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

    public static boolean sendAuth(TerminalAuthInfo terminalAuthInfo, boolean isAsyn, boolean isUdp){
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

    public static boolean sendGPS(TerminalGPSInfo terminalAuthInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;

        TerminalGPSMsg msg = new TerminalGPSMsg();
        terminalAuthInfo.setBCDTime(BitOperator.getInstance().getBCDTime());
        msg.setTerminalGPSInfo(terminalAuthInfo);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_LOCATION_UPLOAD);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    /**参数上传
     *
     * @param parametersInfo
     * @param isAsyn
     * @param isUdp
     * @return
     */
    public static boolean sendParamenter(TerminalParametersInfo parametersInfo, boolean isAsyn, boolean isUdp){
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

    /**音视频资源列表上传
     *
     * @param serNum
     * @return
     */
    public static boolean sendAVResourceList(int serNum){
        if (socketClient == null) return false ;
        TerminalResourceMsg msg = new TerminalResourceMsg(serNum) ;
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.SERVER_PARAY_RSP);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, true, false) ;
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

//    /**用户登录
//     *
//     * @param provinceId    省域ID
//     * @param cityId    市县域ID
//     * @param manufacturerId    制造商ID
//     * @param terminalType  终端型号
//     * @param terminalId    终端ID
//     * @param color  * 车牌颜色(BYTE) 车牌颜色，未上牌时，取值为0<br>
//     *          * 0===未上车牌<br>
//     *          * 1===蓝色<br>
//     *          * 2===黄色<br>
//     *          * 3===黑色<br>
//     *          * 4===白色<br>
//     *          * 9===其他
//     * @param licensePlate  车号牌
//     * @return  是否发送成功
//     */
//    public static boolean USLogin(int provinceId, int cityId, String manufacturerId,
//                                  String terminalType, String terminalId, int color, String licensePlate){
//        TerminalRegisterMsg.TerminalRegInfo regInfo = new TerminalRegisterMsg.TerminalRegInfo() ;
////        regInfo.setProvinceId(provinceId==0?0x00:provinceId);
////        regInfo.setCityId(cityId==0?0x00:cityId);
//        regInfo.setProvinceId(provinceId);
//        regInfo.setCityId(cityId);
//        regInfo.setManufacturerId(manufacturerId==null?"12345":manufacturerId);
//        regInfo.setTerminalType(terminalType==null?"12345678901234567890":terminalType);
//        regInfo.setTerminalId(terminalId==null?"ABCD123":terminalId);
////        regInfo.setLicensePlateColor(color==0?0x01:color);
//        regInfo.setLicensePlateColor(color);
//        regInfo.setLicensePlate(licensePlate==null?phone:licensePlate);        // 终端名称 - 别名
//        Log.d(TAG, "SendRegister ---------------------------------"+regInfo) ;
//        return sendRegister(regInfo, false ,false) ;
//    }

//    /** 鉴权
//     */
//    public static boolean USAuth(String authCode){
//        TerminalAuthMsg.TerminalAuthInfo authInfo = new TerminalAuthMsg.TerminalAuthInfo();
//        authInfo.setAuth(authCode);
//        return sendAuth(authInfo, false, false) ;
//    }
//
//    /** 通用回复
//     * @param result     回复流水号
//     * @param respId    回复消息ID
//     * @param seNum  回复码 0 成功， 1 失败，
//     * @return  是否发送成功
//     */
//    public static boolean USGeneral(int result ,int respId, int seNum){
//        TerminalGeneralMsg.TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralMsg.TerminalGeneralInfo() ;
//        terminalGeneralInfo.setResult(result);
//        terminalGeneralInfo.setRespId(respId);
//        terminalGeneralInfo.setSeNum(seNum);
//        return sendGeneralReponse(terminalGeneralInfo, false, false) ;
//    }

}
