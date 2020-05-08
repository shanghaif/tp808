package cn.com.erayton.jt_t808.video.manager;


import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.speedtalk.protocol.tscobjs.paramobjs.IP;

import java.util.List;

import cn.com.erayton.jt_t808.constants.PublicConstants;
import cn.com.erayton.jt_t808.video.eventBus.EventBusUtils;
import cn.com.erayton.jt_t808.video.eventBus.event.BroadCastMainEvent;
import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.model.decode.ServerFileUploadMsg;
import cn.com.erayton.usagreement.model.decode.ServerVideoReplayMsg;
import cn.com.erayton.usagreement.model.model.TerminalAuthInfo;
import cn.com.erayton.usagreement.model.model.TerminalGeneralInfo;
import cn.com.erayton.usagreement.model.model.TerminalParametersInfo;
import cn.com.erayton.usagreement.model.model.TerminalRegInfo;
import cn.com.erayton.usagreement.model.model.TerminalResourceInfo;
import cn.com.erayton.usagreement.socket.client.SocketClient;
import cn.com.erayton.usagreement.socket.client.SocketClientSender;


public class USManager {

    private final String TAG = "USManager" ;
    private static USManager singleton = null;
    private SocketClient socketClient ;
    private static String authCode ;
    private static String phone ;
    private static String tHost ;
    private static int tPort ;
    private static int uPort ;
    private boolean isLoginSucc = false ;
    private boolean isReconnect = false ;

    private boolean isLocation = true;//标志是否有效定位

    public static USManager getSingleton() {
        Log.d("USManager", "public static USManager getSingleton() {\n");
        if(singleton == null){
            singleton = new USManager();
        }
        return singleton;
    }


    public void setIsLocation(boolean islocation) {
        this.isLocation = islocation;
    }

    public void setPhone(String phone) {
        this.phone = phone;
        SocketClientSender.setPhone(phone);
    }

    public boolean isLogin(){
        return socketClient.getIsLogin() && socketClient.isConnected();
    }

    public void connect(boolean isOpen){
        socketClient.openSocket(tHost, tPort, isOpen, uPort);
        Log.d(TAG, "IP:"+tHost+",TPORT:"+tPort+",UPORT:"+uPort+",PHONE:"+phone) ;
    }

    public void setServer(String tHost, int tPort , int uPort){
        this.tHost = tHost ;
        this.tPort = tPort ;
        this.uPort = uPort ;
    }

    public void setLoginSucc(boolean loginSucc) {
        isLoginSucc = loginSucc;
    }

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        if (!PublicConstants.ApiConstants.USAUTHCODE.equals(authCode))  PublicConstants.ApiConstants.USAUTHCODE = authCode ;
        this.authCode = authCode;
    }

    //private void link(){
//    socketClient= new SocketClient() ;
////        socketClient.openSocket("192.168.1.123", 7808, false, 7808);
////        socketClient.openSocket("60.205.226.132", 7808, false, 7808);
//        socketClient.openSocket("222.222.19.34", 7808, false, 7808);
//    socketClient.listener =  socketClientListener ;
//        MsgData.getInstance().setPhone("12345678901");
//}

    private USManager() {
        Log.d("USManager", "USManager ------------ \n");
        socketClient = new SocketClient() ;
        socketClient.listener = socketClientListener ;
    }

    public SocketClient getSocketClient() {
        return socketClient;
    }

    /**
     * @Desctription 服务器连接
     *
     * */
    public void ServerLogin(String phone, String tHost, int tPort, int uPort, boolean isOpen){
        setServer(tHost,tPort,uPort);
        Log.d(TAG, "IP:"+tHost+",TPORT:"+tPort+",UPORT:"+uPort) ;
        setPhone(phone);
        connect(isOpen);
    }

    //  登陆 即 socketClientSender register    成功之后自动发送心跳
    public void USLogin(){
//        TerminalRegisterMsg.TerminalRegInfo regInfo = new TerminalRegisterMsg.TerminalRegInfo() ;
//        regInfo.setProvinceId(0x00);
//        regInfo.setCityId(0x00);
//        regInfo.setManufacturerId("12345");
//        regInfo.setTerminalType("12345678901234567890");
//        regInfo.setTerminalId("ABCD123");
//        regInfo.setLicensePlateColor(0x01);
//        regInfo.setLicensePlate("测试03");        // 终端名称 - 别名
//        Log.d(TAG, "SendRegister ---------------------------------"+regInfo) ;
//        SocketClientSender.sendRegister(regInfo, false ,false) ;

        TerminalRegInfo regInfo = new TerminalRegInfo() ;
        regInfo.setProvinceId(0x00);
        regInfo.setCityId(0x00);
        regInfo.setManufacturerId("12345");
        regInfo.setTerminalType("12345678901234567890");
        regInfo.setTerminalId("ABCD123");
        regInfo.setLicensePlateColor(0x01);
        regInfo.setLicensePlate("测试03");        // 终端名称 - 别名
        Log.d(TAG, "SendRegister ---------------------------------"+regInfo) ;
        SocketClientSender.sendRegister(regInfo, false ,false) ;
    }

    //  发送GPS
//    public void SendGPS(Gps gps){
//        TerminalGPSMsg.TerminalGPSInfo terminalGPSInfo = new TerminalGPSMsg.TerminalGPSInfo() ;
//        terminalGPSInfo.setWarningMark(0);
//
//        String radix2State = null;
//        if (isLocation){
//            //  定位成功
//            radix2State = "00000000000001100000000000000011";
//        }else {
//            //  未定位
//            radix2State = "00000000000001100000000000000001";
//        }
//
//        terminalGPSInfo.setState(radix2State);
//        terminalGPSInfo.setLatitude(gps.getLt());
//        terminalGPSInfo.setLongitude(gps.getLg());
//        terminalGPSInfo.setAltitude(gps.getHight());
//        terminalGPSInfo.setSpeed((int) gps.getSpeed());
//        terminalGPSInfo.setAdditionalInformationId(0x01);
//        terminalGPSInfo.setAdditionalInformationLength(4);
//        terminalGPSInfo.setMileage(10);
//        terminalGPSInfo.setDirection(gps.getDirection());
//        Log.d(TAG, "SendGPS ---------------------------------"+gps) ;
//        SocketClientSender.sendGPS(terminalGPSInfo, false, false) ;
//    }

    //  发送鉴权
    public void SendAuth() {
//        TerminalAuthMsg.TerminalAuthInfo authInfo = new TerminalAuthMsg.TerminalAuthInfo();
//        if (authCode == null){
//            if (PublicConstants.ApiConstants.USAUTHCODE.equals("0")){
//                USLogin() ;     //  重新登陆
//            }else {
//                authInfo.setAuth(PublicConstants.ApiConstants.USAUTHCODE);
//            }
//        }else {
//            authInfo.setAuth(authCode);
//        }
//        Log.d(TAG, "SendAuth ---------------------------------"+authCode) ;
//        SocketClientSender.sendAuth(authInfo, false, false) ;
        TerminalAuthInfo authInfo = new TerminalAuthInfo();
        if (authCode == null){
            if (PublicConstants.ApiConstants.USAUTHCODE.equals("0")){
                USLogin() ;     //  重新登陆
            }else {
                authInfo.setAuth(PublicConstants.ApiConstants.USAUTHCODE);
            }
        }else {
            authInfo.setAuth(authCode);
        }
        Log.d(TAG, "SendAuth ---------------------------------"+authCode) ;
        SocketClientSender.sendAuth(authInfo, false, false) ;
    }

    public void SendGeneralResp(int seNum ,int respId, int code){
        Log.d("SocketClient", "SendGeneralResp ---------------------------------") ;
        TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralInfo() ;
        terminalGeneralInfo.setSeNum(seNum);
        terminalGeneralInfo.setRespId(respId);
        terminalGeneralInfo.setResult(code);
        SocketClientSender.sendGeneralReponse(terminalGeneralInfo, false, false) ;
//        TerminalGeneralMsg.TerminalGeneralInfo terminalGeneralInfo = new TerminalGeneralMsg.TerminalGeneralInfo() ;
//        terminalGeneralInfo.setSeNum(seNum);
//        terminalGeneralInfo.setRespId(respId);
//        terminalGeneralInfo.setResult(code);
//        SocketClientSender.sendGeneralReponse(terminalGeneralInfo, false, false) ;
    }

    public void SendParamenter(int seNum){
//        TerminalParametersMsg.TerminalParametersInfo parametersInfo = new TerminalParametersMsg.TerminalParametersInfo() ;
//        parametersInfo.setSerialNumber(seNum);
//        parametersInfo.setSerialNumber(10);
//        parametersInfo.setInstructCount(8);
//        parametersInfo.setItemParamenter("0208");
//        parametersInfo.setGpsSleepInterval(40);
//        parametersInfo.setGpsDefInterval(50);
//        parametersInfo.setStrategy(0);
//        parametersInfo.setPlan(1);
//        parametersInfo.setLoggedOnInterval(10);
//        parametersInfo.setAlarmInterval(20);
//        parametersInfo.setAngelInflection(30);
//        parametersInfo.setThreshold(60);
//        SocketClientSender.sendParamenter(parametersInfo, false, false) ;

        TerminalParametersInfo parametersInfo = new TerminalParametersInfo() ;
        parametersInfo.setSerialNumber(seNum);
        parametersInfo.setSerialNumber(10);
        parametersInfo.setInstructCount(8);
        parametersInfo.setItemParamenter("0208");
        parametersInfo.setGpsSleepInterval(40);
        parametersInfo.setGpsDefInterval(50);
        parametersInfo.setStrategy(0);
        parametersInfo.setPlan(1);
        parametersInfo.setLoggedOnInterval(10);
        parametersInfo.setAlarmInterval(20);
        parametersInfo.setAngelInflection(30);
        parametersInfo.setThreshold(60);
        SocketClientSender.sendParamenter(parametersInfo, false, false) ;
    }

    public void SendAVResourceList(int seNum, List<TerminalResourceInfo> infos){
        SocketClientSender.sendAVResourceList(seNum, infos, false, false ) ;
    }


    public static void SendBytes(byte[] bytes){
        SocketClientSender.send(bytes, false, false) ;
    }


    SocketClient.SocketClientListener socketClientListener = new SocketClient.SocketClientListener() {
        @Override
        public void commomResp(int result, String reason) {
//            Decoder4LoggingOnly decoder4LoggingOnly = new Decoder4LoggingOnly() ;
//            MsgTransformer msgTransformer = new MsgTransformer() ;
//            try {
//                decoder4LoggingOnly.decodeHex(msgTransformer.packageDataToByte(packetData));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }

            Log.d(TAG, "commomResp - - result:"+result+",reason:"+reason);
        }

        @Override
        public void registerResp(int result, String authCode) {
            Log.d(TAG, "registerResp: "+authCode);
            if (result == 0 ){
                setAuthCode(authCode);
                SendAuth();
            }
        }

        @Override
        public void authResp(int resultCode, String reason) {
            Log.d(TAG, "authResp: resultCode:"+resultCode+",reason:"+reason);
        }

        @Override
        public void heartResp(int resultCode, String reason) {
            Log.d(TAG, "heartResp: resultCode:"+resultCode+",reason:"+reason);
        }

        @Override
        public void gpsResp(int resultCode, String reason) {
            Log.d(TAG, "gpsResp: resultCode:"+resultCode+",reason:"+reason);
        }

        @Override
        public void defaultResp(String s, int i) {
            Log.d(TAG, "defaultResp: ");
        }

        @Override
        public void onConnect(int i) {
            Log.d(TAG, "onConnect: "+i);
            if (i == 0){
                    isLoginSucc = true ;
                    if (isReconnect){
                        SendAuth();
                        isReconnect = false ;
                    }else
                    USLogin();
            }
//            Log.d(TAG, "onConnect--sendHB: "+i);
        }

        @Override
        public void onDisConnect() {
            isLoginSucc = false ;
//            ServerLogin(ClientManagers.getSingleton().getLoginResp().getMsId(), PublicConstants.ApiConstants.USLOGIN_IP,
//                    PublicConstants.ApiConstants.USLOGIN_TPORT, PublicConstants.ApiConstants.USLOGIN_UPORT,false);
            reconnect();
            Log.d(TAG, "onDisConnect: ");
        }

        @Override
        public void onSend(byte[] bytes, boolean b) {
            Log.d(TAG, "onSend: "+b);
        }

        @Override
        public void onTernimalParameterSetting(int i, int i1, int i2, int i3) {
            int gpsDefInterval = i2 ;
            int gpsRunningInterval = i1 ;
            int code = 0 ;
//            600s -> 10 分钟
            if ((5 <= gpsDefInterval && gpsDefInterval <= 600) && ( 5 <= gpsRunningInterval && gpsRunningInterval <= 600)){
//                ClientInfo.setGpsSleepInterval(gpsDefInterval);
//                ClientInfo.setGpsRunningInterval(gpsRunningInterval);
//                SharedPreferencesUtil.saveDefaultGpsInterval(gpsDefInterval);
//                SharedPreferencesUtil.saveMoveGpsInterval(gpsRunningInterval);
            }else {
                //  设置失败
                code = 1 ;
            }
//            SendGeneralResp(serverParametersMsg.getSerialNumber(), serverParametersMsg.getMsgHeader().getMsgId(), code);
            SendGeneralResp(i3, Constants.TERMINAL_PARAMETERS_SETTING, code);
        }

        @Override
        public void queryTernimalParameterSetting(int i) {
            SendParamenter(i) ;
        }

        @Override
        public void onAVPropertiesQuery() {

        }

        @Override
        public void onTernimalAVTranslate(String s, int i, int i1, int i2, int i3, int i4, int i5) {
            int result = 1 ;
            Log.d(TAG, "onTernimalAVTranslate:"+s+"，服务器 TCP 端口号:"+i+"，服务器 UDP 端口号:"+i1+"，逻辑通道号:"+i2+"，数据类型:"+i3+"，码流类型:"+i4+"，流水号:"+i5) ;
            if (!TextUtils.isEmpty(s)){ //  ip 不为空，返回通用回复成功
                result = 0 ;
//                serverAVTranslateMsg.getHost() ;
//                serverAVTranslateMsg.getTcpPort() ;
//                serverAVTranslateMsg.getChannelNum() ;
//                serverAVTranslateMsg.getSteamType() ;
//                serverAVTranslateMsg.getDataType() ;
                EventBusUtils.sendEvent(new BroadCastMainEvent(EventBusUtils.EventCode.OPEN_VIDEO, s, i, i1, i2, i3, i4, i5));
            }


//            TerminalGeneralInfo info = new TerminalGeneralInfo() ;
//            info.setResult(result);
//            info.setRespId(Constants.SERVER_AVTRANSMISSION_REQUEST);
//            info.setSeNum(i5);
//            SocketClientSender.sendGeneralReponse(info, true , false) ;
            SendGeneralResp(i5, Constants.SERVER_AVTRANSMISSION_REQUEST, result);
//            TerminalGeneralMsg.TerminalGeneralInfo info = new TerminalGeneralMsg.TerminalGeneralInfo() ;
//            info.setResult(result);
//            info.setRespId(Constants.SERVER_AVTRANSMISSION_REQUEST);
//            info.setSeNum(i5);
//            SocketClientSender.sendGeneralReponse(info, true , false) ;
        }

        @Override
        public void onAVControl(int i, int i1, int i2, int i3) {

            if (i == 0){
                EventBusUtils.sendEvent(new BroadCastMainEvent(EventBusUtils.EventCode.CLOSE_VIDEO, null));
            }
        }

        @Override
        public void onQueryResourceReq(int serNum, int channelNum, String startTime, String endTime, String warningMark, int resourceType, int steamType, int memoryType) {
            EventBusUtils.sendEvent(new BroadCastMainEvent(EventBusUtils.EventCode.QUERY_RESOURCE, serNum));
        }

        @Override
        public void onAVReplayReq(ServerVideoReplayMsg msg) {

        }

        @Override
        public void onAVReplayControl(int channelNum, int playbackControl, int multiple, String dragTo) {

        }

        @Override
        public void onFileUploadReq(int seNum, ServerFileUploadMsg msg) {
            EventBusUtils.sendEvent(new BroadCastMainEvent(EventBusUtils.EventCode.FILEUPLOAD_REQ, seNum, msg));
        }

        @Override
        public void onFileUploadControl(int seNum, int uploadControl) {

        }

        @Override
        public void onRotateCloudControl(int channelNum, int direction, int speech) {

        }

        @Override
        public void onCloudControl(int controlType, int channelNum, int num) {

        }


    } ;

    /**
     * 退出
     * @throws RemoteException
     */
    public void Out() throws RemoteException {
        socketClient.close();
    }

    public boolean getIsLoginSucc() {
        return isLoginSucc;
    }

    public void reconnect(){
//        if (isNeedReconnect){
//            socketClient.close();
            isReconnect = true ;
            Log.i(TAG, "reconnect()-----------------------------------") ;
//            connect(false);
//        }
    }




}
