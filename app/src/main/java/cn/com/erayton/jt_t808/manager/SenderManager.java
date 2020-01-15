package cn.com.erayton.jt_t808.manager;
import cn.com.erayton.usagreement.model.TerminalAuthMsg;
import cn.com.erayton.usagreement.model.TerminalRegisterMsg;
import cn.com.erayton.usagreement.socket.client.SocketClientSender;
import cn.com.erayton.usagreement.utils.LogUtils;

public class SenderManager {

    public static void SendLogin(){
        TerminalRegisterMsg.TerminalRegInfo regInfo = new TerminalRegisterMsg.TerminalRegInfo() ;
        regInfo.setProvinceId(0x00);
        regInfo.setCityId(0x00);
        regInfo.setManufacturerId("12345");
        regInfo.setTerminalType("12345678901234567890");
        regInfo.setTerminalId("ABCD123");
        regInfo.setLicensePlateColor(0x01);
        regInfo.setLicensePlate("测试03");        // 终端名称 - 别名
        LogUtils.d("SendRegister ---------------------------------"+regInfo);
        SocketClientSender.sendRegister(regInfo, false ,false) ;
    }


    //  发送鉴权
    public static void SendAuth(String authCode) {
        TerminalAuthMsg.TerminalAuthInfo authInfo = new TerminalAuthMsg.TerminalAuthInfo();
        authInfo.setAuth(authCode);
        LogUtils.d("SendAuth ---------------------------------"+authCode) ;
        SocketClientSender.sendAuth(authInfo, false, false) ;
    }

}
