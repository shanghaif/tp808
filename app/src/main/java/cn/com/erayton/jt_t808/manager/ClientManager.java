package cn.com.erayton.jt_t808.manager;

import java.util.Arrays;

import cn.com.erayton.jt_t808.constants.PublicConstants;
import cn.com.erayton.usagreement.model.PacketData;
import cn.com.erayton.usagreement.model.ServerAVTranslateMsg;
import cn.com.erayton.usagreement.model.ServerParametersMsg;
import cn.com.erayton.usagreement.socket.client.SocketClient;
import cn.com.erayton.usagreement.socket.client.SocketClientSender;
import cn.com.erayton.usagreement.utils.LogUtils;

public class ClientManager {
    private static ClientManager instance = null ;
    private SocketClient socketClient ;


    public static ClientManager getInstance(){
        if (instance == null){
            instance = new ClientManager() ;
        }
        return instance ;
    }

    private ClientManager() {
        socketClient = new SocketClient() ;
        socketClient.listener = socketClientListener ;
    }

    public void ServerLogin(){
        SocketClientSender.setPhone(PublicConstants.ApiConstants.USER_NAME);
        socketClient.openSocket(PublicConstants.ApiConstants.HOST,
                PublicConstants.ApiConstants.PORT, false, PublicConstants.ApiConstants.PORT);
    }

    private SocketClient.SocketClientListener socketClientListener = new SocketClient.SocketClientListener() {

        @Override
        public void commomResp(int i, String s) {

        }

        @Override
        public void registerResp(int i, String s) {
            LogUtils.d("i:"+i+",s:"+s);
            SenderManager.SendAuth(s);
        }

        @Override
        public void authResp(int i, String s) {
            LogUtils.d("i:"+i+",s:"+s);
        }

        @Override
        public void heartResp(int i, String s) {
            LogUtils.d("i:"+i+",s:"+s);
        }

        @Override
        public void gpsResp(int i, String s) {
            LogUtils.d("i:"+i+",s:"+s);
        }

        @Override
        public void defaultResp(String s, int i) {

        }


        @Override
        public void onConnect(int i) {
            LogUtils.d("i:"+i);
        }

        @Override
        public void onDisConnect() {
            LogUtils.d("");
        }

        @Override
        public void onSend(byte[] bytes, boolean b) {
            LogUtils.d("bytes:"+ Arrays.toString(bytes)+",b:"+b);
        }

        @Override
        public void onTernimalParameterSetting(int i, int i1, int i2, int i3) {

        }

        @Override
        public void queryTernimalParameterSetting(int i) {

        }

        @Override
        public void onTernimalAVTranslate(String s, int i, int i1, int i2, int i3, int i4, int i5) {

        }

        @Override
        public void onAVControl(int i, int i1, int i2, int i3) {

        }

    } ;
}
