package cn.com.erayton.usagreement.socket.client;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.EmptyPacketData;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.encode.TerminalAVPropertieMsg;
import cn.com.erayton.usagreement.model.encode.TerminalAuthMsg;
import cn.com.erayton.usagreement.model.encode.TerminalGPSMsg;
import cn.com.erayton.usagreement.model.encode.TerminalGeneralMsg;
import cn.com.erayton.usagreement.model.encode.TerminalParametersMsg;
import cn.com.erayton.usagreement.model.encode.TerminalRegisterMsg;
import cn.com.erayton.usagreement.model.encode.TerminalResourceMsg;
import cn.com.erayton.usagreement.model.encode.TerminalResourceStatusMsg;
import cn.com.erayton.usagreement.model.encode.TerminalRiderShipMsg;
import cn.com.erayton.usagreement.model.model.TerminalAVPropertieInfo;
import cn.com.erayton.usagreement.model.model.TerminalAuthInfo;
import cn.com.erayton.usagreement.model.model.TerminalGPSInfo;
import cn.com.erayton.usagreement.model.model.TerminalGeneralInfo;
import cn.com.erayton.usagreement.model.model.TerminalParametersInfo;
import cn.com.erayton.usagreement.model.model.TerminalRegInfo;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.model.model.TerminalRiderShipInfo;
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
        //  ???????????????
        if (phone == null) {
            LogUtils.e("phone is null.") ;
        }
        header.setTerminalPhone("0"+phone);
//        header.setTerminalPhone(String.format("%012s", phone));
        return header ;
    }


    //  ????????????
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
        // ??????
        TerminalRegisterMsg msg = new TerminalRegisterMsg();
        //  body
//        TerminalRegisterMsg.TerminalRegInfo terminalRegInfo = new TerminalRegisterMsg.TerminalRegInfo();
//        terminalRegInfo.setProvinceId(0x00);
//        terminalRegInfo.setCityId(0x00);
//        terminalRegInfo.setManufacturerId("12345");
//        terminalRegInfo.setTerminalType("12345678901234567890");
//        terminalRegInfo.setTerminalId("ABCD123");
//        terminalRegInfo.setLicensePlateColor(0x01);
//        terminalRegInfo.setLicensePlate("???A:66666");
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
        //  ??????

        EmptyPacketData msg = new EmptyPacketData();    //  ??????
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_HEART_BEAT);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);
        LogUtils.d("header:"+header);
        return send(msg, isAsyn, isUdp) ;
    }

    public static boolean sendAuth(TerminalAuthInfo terminalAuthInfo, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;
        //  ??????
        TerminalAuthMsg msg = new TerminalAuthMsg();
        //  body
//        TerminalRegisterMsg.TerminalRegInfo terminalRegInfo = new TerminalRegisterMsg.TerminalRegInfo();
//        terminalRegInfo.setProvinceId(0x00);
//        terminalRegInfo.setCityId(0x00);
//        terminalRegInfo.setManufacturerId("12345");
//        terminalRegInfo.setTerminalType("12345678901234567890");
//        terminalRegInfo.setTerminalId("ABCD123");
//        terminalRegInfo.setLicensePlateColor(0x01);
//        terminalRegInfo.setLicensePlate("???A:66666");
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

    /**????????????
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

    /**???????????????????????????
     *
     * */
    public static boolean sendAVPropertie(TerminalAVPropertieInfo info, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;

        TerminalAVPropertieMsg msg = new TerminalAVPropertieMsg();
        msg.setInfo(info);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_AVPROPERTIE_UPLOAD);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    /**????????????????????????
     *
     * */
    public static boolean sendRidership(TerminalRiderShipInfo info, boolean isAsyn, boolean isUdp){
        if (socketClient == null)   return false ;

        TerminalRiderShipMsg msg = new TerminalRiderShipMsg();
        msg.setInfo(info);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_RIDERSHIP_UPLOAD);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp) ;
    }

    /**
     * ???????????????????????????
     * @param serNum
     * @return
     */
    public static boolean sendAVResourceList(int serNum, List<TerminalResourceInfo> infos, boolean isAsyn, boolean isUdp){
        if (socketClient == null)
            return false;
//        List<TerminalResourceInfo> infos = new ArrayList<>() ;
//        TerminalResourceInfo info = new TerminalResourceInfo();
//        info.setChannelNum(1);
//        info.setStartTime("200415000000");
//        info.setEndTime("200415235959");
//        // byte[] a = { 0, 0, 0, 0, 0, 0, 0, 0 };
//        byte[] a = { 1, 1, 1, 1, 1, 1, 1, 1 };
//        info.setWrang(a);
//        info.setResourceType(0);
//        info.setSteamType(1);
//        info.setMemoryType(1);
//
//        info.setFileSize(1024);
//        for (int i = 0; i < 5; i++) {
//            infos.add(info);
//        }
        TerminalResourceMsg msg = new TerminalResourceMsg();
        msg.setSerNum(serNum);
        msg.setInfoList(infos);
        // header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_RESOURCE_LIST_UPLOAD);
        header.setMsgBodyLength(msg.getBodyLength());
        msg.setMsgHeader(header);

        return send(msg, isAsyn, isUdp);
    }

    /**
     * ??????????????????
     * @param seNum ?????????
     * @param code  ??????
     * @return
     */
    public boolean SendAVTranslate(int seNum, int code){
        Log.d("SocketClient", "sendUploadResp ---------------------------------") ;
        TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralInfo() ;
        terminalGeneralInfo.setSeNum(seNum);
        terminalGeneralInfo.setRespId(Constants.SERVER_AVTRANSMISSION_REQUEST);
        terminalGeneralInfo.setResult(code);
        return sendGeneralReponse(terminalGeneralInfo, false, false) ;

    }

    /** ??????????????????(????????????)
     *
     * @param seNum ?????????
     * @param code  ??????
     * @return
     */

    public static boolean sendUploadResp(int seNum, int code){
        Log.d("SocketClient", "sendUploadResp ---------------------------------") ;
        TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralInfo() ;
        terminalGeneralInfo.setSeNum(seNum);
        terminalGeneralInfo.setRespId(Constants.SERVER_FILEUPLOAD_REQUEST);
        terminalGeneralInfo.setResult(code);
        return sendGeneralReponse(terminalGeneralInfo, false, false) ;
    }

    /** ????????????????????????
     *
     * */
    public static boolean sendUploadStatus(int serNum, int result, boolean isAsyn, boolean isUdp){
        if (socketClient == null) return false ;
        TerminalResourceStatusMsg msg = new TerminalResourceStatusMsg() ;
        msg.setSerNum(serNum);
        msg.setResult(result);
        //  header
        PacketData.MsgHeader header = getHeader();
        header.setMsgId(Constants.TERMINAL_RESOURCE_STUTUS_UPLOAD);
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

//    /**????????????
//     *
//     * @param provinceId    ??????ID
//     * @param cityId    ?????????ID
//     * @param manufacturerId    ?????????ID
//     * @param terminalType  ????????????
//     * @param terminalId    ??????ID
//     * @param color  * ????????????(BYTE) ???????????????????????????????????????0<br>
//     *          * 0===????????????<br>
//     *          * 1===??????<br>
//     *          * 2===??????<br>
//     *          * 3===??????<br>
//     *          * 4===??????<br>
//     *          * 9===??????
//     * @param licensePlate  ?????????
//     * @return  ??????????????????
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
//        regInfo.setLicensePlate(licensePlate==null?phone:licensePlate);        // ???????????? - ??????
//        Log.d(TAG, "SendRegister ---------------------------------"+regInfo) ;
//        return sendRegister(regInfo, false ,false) ;
//    }

//    /** ??????
//     */
//    public static boolean USAuth(String authCode){
//        TerminalAuthMsg.TerminalAuthInfo authInfo = new TerminalAuthMsg.TerminalAuthInfo();
//        authInfo.setAuth(authCode);
//        return sendAuth(authInfo, false, false) ;
//    }
//
//    /** ????????????
//     * @param result     ???????????????
//     * @param respId    ????????????ID
//     * @param seNum  ????????? 0 ????????? 1 ?????????
//     * @return  ??????????????????
//     */
//    public static boolean USGeneral(int result ,int respId, int seNum){
//        TerminalGeneralMsg.TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralMsg.TerminalGeneralInfo() ;
//        terminalGeneralInfo.setResult(result);
//        terminalGeneralInfo.setRespId(respId);
//        terminalGeneralInfo.setSeNum(seNum);
//        return sendGeneralReponse(terminalGeneralInfo, false, false) ;
//    }

}
