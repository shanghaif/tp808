package cn.com.erayton.usagreement.socket.client;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.data.ResponseReason;
import cn.com.erayton.usagreement.model.PacketData;
import cn.com.erayton.usagreement.model.ServerGeneralMsg;
import cn.com.erayton.usagreement.model.ServerRegisterMsg;
import cn.com.erayton.usagreement.socket.core.TCPClient;
import cn.com.erayton.usagreement.socket.core.UDPClient;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.Decoder4LoggingOnly;
import cn.com.erayton.usagreement.utils.MsgTransformer;
import cn.com.erayton.usagreement.utils.Utils;

public class SocketClient implements TCPClient.TCPClientListener, UDPClient.UDPClientListener, Runnable {
    public interface SocketClientListener{
//        0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
        /**
         * @param result 0：成功/确认；1：失败；2：消息有误；
         *               3：不支持；4：报警处理确认；
         * @param reason 返回原因
         * */
        void commomResp(int result, String reason, PacketData packetData) ;        //  通用应答
        /**
         * @param result    0：成功；1：车辆已被注册；2：数据库中无该车辆；
         *                  3：终端已被注册；4：数据库中无该终端
         * @param authCode  成功之后返回鉴权码，失败返回 对应原因
         * */
        void registerResp(int result, String authCode) ;       //  注册应答，返回鉴权码
        void authResp(String authCode) ;           //  鉴权
        void heartResp() ;          //  心跳
        void gpsResp() ;            //  GPS 数据
        void defaultResp(PacketData packetData, String errorMsg, int replyCode) ;       //  未加应答码
        void onConnect(int i) ;          //  是否连接
        void onDisConnect() ;
        void onSend(byte[] data, boolean result) ;

    }



    private Thread hBThread = new Thread(this) ;                    //  心跳发送线程，登陆成功之后发送
    private boolean isHBThread = false ;                                   //   心跳线程是否已启用

    private static final String TAG = SocketClient.class.getSimpleName();
    private BitOperator bitOperator;
    private UDPClient udpClient = new UDPClient() ;
    private TCPClient tcpClient = new TCPClient() ;
    //  protected JT808ProtocolUtils jt808ProtocolUtils;
    public SocketClientListener listener = null ;

    private String tcpIp = "" ;
    private Object tcpLock = new Object() ;
    public String getTcpIp(){synchronized (tcpLock){
        return tcpIp;
    }}
    public void setTcpIp(String tcpIp) {synchronized (tcpLock){
        this.tcpIp = tcpIp;
    }}
    private int tcpPort = 0 ;

    private Object tcpPortLock = new Object() ;
    public int getTcpPort(){synchronized (tcpPortLock) {
        return tcpPort;
    }}
    public void setTcpPort(int tcpPort){synchronized (tcpPortLock) {
        this.tcpPort = tcpPort;
    }}

    private long tcpLastHbDt = 0 ;          //  TCP 心跳返回最后一次时间
    private Object tcpLastHbDtLock = new Object() ;
    public long getTcpLastHbDt() {synchronized (tcpLastHbDtLock){
        return tcpLastHbDt;
    }}
    public void setTcpLastHbDt() {synchronized (tcpLastHbDtLock){
        tcpLastHbDt = System.currentTimeMillis();
    }}

    private int tcpTimeOutCount = 0 ;
    private Object tcpTimeOutCountLock = new Object() ;
    public int getTcpTimeOutCount() {synchronized (tcpTimeOutCountLock){
        return tcpTimeOutCount;
    }}
    public void addTcpTimeOutCount() {synchronized (tcpTimeOutCountLock){
        tcpTimeOutCount ++;
    }}

    private long tcpHBInterval = 5             ;//  TCP时间间隔
    private Object tcpHBIntervalLock = new Object() ;
    public long getTcpHBInterval() {synchronized (tcpHBIntervalLock){
        return tcpHBInterval;
    }}
    public void setTcpHBInterval(long tcpHBInterval) {synchronized (tcpHBIntervalLock){
        this.tcpHBInterval = tcpHBInterval;
    }}


    private String udpIp = "" ;
    private Object udpLock = new Object() ;
    public String getUdpIp() {synchronized (udpLock){
        return udpIp;
    }}
    public void setUdpIp(String udpIp){synchronized (udpLock) {
        this.udpIp = udpIp;
    }}

    private int udpPort = 0 ;
    private Object updPortLock = new Object() ;
    public int getUdpPort(){synchronized (updPortLock) {
        return udpPort;
    }}
    public void setUdpPort(int udpPort){synchronized (updPortLock) {
        this.udpPort = udpPort;
    }}

    private long udpLastHbDt = 0 ;          //  UDP 心跳返回最后一次时间
    private Object udpLastHbDtLock = new Object() ;
    public long getUdpLastHbDt() {synchronized (udpLastHbDtLock){
        return udpLastHbDt;
    }}
    private void setUdpLastHbDt() {synchronized (udpLastHbDtLock){
        udpLastHbDt = System.currentTimeMillis();
    }}

    private int udpTimeOutCount = 0 ;
    private Object udpTimeOutCountLock = new Object() ;
    private int getUdpTimeOutCount() {synchronized (udpTimeOutCountLock){
        return udpTimeOutCount;
    }}
    private void addUdpTimeOutCount() {synchronized (udpTimeOutCountLock){
        udpTimeOutCount ++;
    }}

    private boolean isOpenHB = false ;            //  是否开启心跳
    private Object isOpenHBLock = new Object() ;
    public boolean getIsOpenHB() {synchronized (isOpenHBLock){
        return isOpenHB;
    }}
    public void setIsOpenHB(boolean isOpenHB){synchronized (isOpenHBLock) {
        this.isOpenHB = isOpenHB;
    }}

    private long hBInterval = 30 ;            //  心跳时间间隔
    private Object hBIntervalLock = new Object() ;
    public long getHBInterval() {synchronized (hBIntervalLock){
        return hBInterval;
    }}
    public void setHBInterval(long hBInterval){synchronized (hBIntervalLock) {
        this.hBInterval = hBInterval;
    }}

    private long udpHBInterval = 5             ;//  UDP时间间隔
    private Object udpHBIntervalLock = new Object() ;
    public long getUdpHBInterval() {synchronized (udpHBIntervalLock){
        return udpHBInterval;
    }}
    public void setUdpHBInterval(long udpHBInterval) {synchronized (udpHBIntervalLock){
        this.udpHBInterval = udpHBInterval;
    }}

    private boolean isNeedReconnect = false ;
    private Object isNeedReconnectLock = new Object();
    public boolean getIsNeedReconnect() {synchronized (isNeedReconnectLock){
        return isNeedReconnect;
    }}
    public void setIsNeedReconnect(boolean needReconnect) {synchronized (isNeedReconnectLock){
        isNeedReconnect = needReconnect;
    }}

    private boolean isLogin = false ;
    private Object isLoginLock = new Object() ;
    public boolean getIsLogin() {synchronized (isLoginLock){
        return isLogin;
    }}
    public void setIsLogin(boolean login) {synchronized (isLoginLock){
        isLogin = login;
    }}

    private boolean isOpenUdp = true ;
    private boolean isConnect = false ;


    public SocketClient() {
        bitOperator = BitOperator.getInstance();
        udpClient.listener = this;
        tcpClient.listener = this;
        SocketClientSender.setInstall(this);
    }

    /**
     * @param host ip
     * @param isOpenUdp 是否开启 UDP
     * @param tcpPort TCP 端口
     * @param udpPort UDP 端口
     * */
    public void openSocket(String host, int tcpPort, boolean isOpenUdp, int udpPort){
        if (!isConnected()) close();
        setTcpIp(host);
        setTcpPort(tcpPort);
        setUdpIp(host);
        setUdpPort(udpPort);
        tcpClient.openAsyn(host, tcpPort);              //  开启 TCP
        this.isOpenUdp = isOpenUdp ;
//        udpClient.start() ;                             //  开启 UDP
//        SocketClientSender.sendRegister(true);      //  获取授权码
    }

    public void close(){
        udpClient.close();
    }

    public boolean isConnected(){
        return true ;
    }

    public boolean sendTcpMsg(PacketData packetData, boolean isAsyn){
        MsgTransformer msgTransformer = new MsgTransformer() ;
        Decoder4LoggingOnly decoder4LoggingOnly = new Decoder4LoggingOnly() ;
        try {
            decoder4LoggingOnly.decodeHex(msgTransformer.packageDataToByte(packetData));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isAsyn){
            if (!tcpClient.sendAsyn(msgTransformer.packageDataToByte(packetData))) {
                Log.d(TAG, "Send " + msgTransformer.packageDataToByte(packetData)  + " to fail. [Asyn][TCP] error:" + msgTransformer.packageDataToByte(packetData) );
                return false;
            }
        }else {
//            Log.d("cjh", "------------------------------ isAsyn ----------------------------") ;
            if (!tcpClient.send(msgTransformer.packageDataToByte(packetData))) {
                Log.d(TAG, "Send " + msgTransformer.packageDataToByte(packetData) + " to fail. [Sync][TCP] error:" + msgTransformer.packageDataToByte(packetData));
                return false;
            } else {
                Log.d(TAG, "Send " + msgTransformer.packageDataToByte(packetData) + " success.[TCP][Sync]");
            }
        }

        return true ;
    }

    public boolean sendTcpMsg(byte[] data, boolean isAsyn){
        if (!isAsyn) {
//            Log.d("cjh", "------------------------------ !isAsyn ----------------------------") ;
//            tcpClient.sendAsyn(data) ;
            if (!tcpClient.sendAsyn(data)) {
                Log.d(TAG, "Send " + data + " to fail. [Asyn][TCP] error:" + data);
                return false;
            }
        }else {
//            Log.d("cjh", "------------------------------ isAsyn ----------------------------") ;
            if (!tcpClient.send(data)) {
					Log.d(TAG, "Send " + data + " to fail. [Sync][TCP] error:" + data);
					return false;
				} else {
					Log.d(TAG, "Send " + data + " success.[TCP][Sync]");
				}
        }
        return true ;
    }
    public boolean sendUdpMsg(PacketData packetData, boolean isAsyn){
        return true ;
    }

    public boolean sendUdpMsg(byte[] data, boolean isAsyn){
        if (!isAsyn) {
            udpClient.sendAsyn(data) ;
            if (!udpClient.sendAsyn(data)) {
                Log.d(TAG, "Send " + data + " to fail. [Asyn][UDP] error:" + data);
                return false;
            }
        }else {
            if (!udpClient.send(data)) {
					Log.d(TAG, "Send " + data + " to fail. [Sync][UDP] error:" + data);
					return false;
				} else {
					Log.d(TAG, "Send " + data + " success.[UDP][Sync]");
				}
        }
        return true ;
    }

    private ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            1, 1, 1,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>(),
            new ThreadPoolExecutor.DiscardPolicy()
    );

    @Override
    public void onTcpConnect(int result) {
        if (result == 0){
            setIsNeedReconnect(false) ;
            //  连接成功
        }else if(result == -1){
            //  无法连接服务器, 与服务器断开
//            if (tcpClient != null){
//                tcpClient.close();
//                Log.d(TAG, "-------------------- restart") ;
//                tcpClient.start();
//            }
        }else {
            Log.d(TAG, "OnConnect else -- " + result);
        }

        listener.onConnect(result);
        Log.d(TAG, "OnConnect " + result);
    }

    @Override
    public void onTcpDisConnect() {
        if (listener != null){
            listener.onDisConnect();
        }
        setIsNeedReconnect(true);
    }

    @Override
    public void onTcpSend(byte[] data, boolean result) {
//      something here

        if (listener != null) {
//            Log.d("cjh", "------------------------------ onTcpSend ----------------------------") ;
            listener.onSend(data, result) ;
        }
    }

    @Override
    public void onTcpReceive(byte[] receiveBytes) {
        OnDispathCmd(receiveBytes, false);
    }

    @Override
    public void OnUdpSend(byte[] data, boolean result) {
        //      something here

        if (listener != null) {
            listener.onSend(data, result) ;
        }
    }

    @Override
    public void OnUdpReceive(byte[] receiveBytes) {
        OnDispathCmd(receiveBytes, true);
    }

    @Override
    public void run() {
        //  心跳规则
        while (true){
            //  心跳发送
            isHBThread = true ;
            Log.d(TAG, "Send HB --1---- run ----------") ;
//            SocketClientSender.sendHB(false, false);
            if (!getIsOpenHB()){
                try {
                    Thread.sleep(Constants.HBOPENTHREAD_SLEEPIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Log.d(TAG, "Send HB ---2--- run ----------") ;

            try {
                Thread.sleep(Constants.HBTHREAD_SLEEPIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            //region UDP 心跳
            //            //  UDP  当最后一次时间超过 5 秒时， 超时
//            long udpLastHbDt = getUdpLastHbDt() ;
//            long currentDt = System.currentTimeMillis() ;
//            Log.d(TAG, "udpLastHbDt:"+((udpLastHbDt + Constants.HBTHREAD_TOMEOUT )<currentDt)+"\n"+(udpLastHbDt + Constants.HBTHREAD_TOMEOUT) +"\n"+currentDt);
//            if ((udpLastHbDt+Constants.HBTHREAD_TOMEOUT) < currentDt){
//                Log.d(TAG, "udp timeout ");
//
//                addUdpTimeOutCount() ;
//
//                if (getUdpTimeOutCount() == Constants.UDPRECONNECT_TIMEOUT){
//                    isConnect = false ;
//                    udpClient.close();
//                    udpClient.start() ;
//                    if (listener != null){
//                        listener.onConnect(1);
//                    }
//                }
//                SocketClientSender.sendHB(false, true) ;
//            }else {
//                Log.d(TAG, "udp no time out delay sometime to sendHb");
//                if (!isConnect){
//                    if (listener != null) {
//                        listener.onConnect(0);
//                    }
//                    isConnect=true;
//                }
//                udpTimeOutCount=0;
//                try {
//
//                    if (getUdpHBInterval()>4){
//                        Thread.sleep((getUdpHBInterval()-4)*1000);
//                    }
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //  心跳发送
//                SocketClientSender.sendHB(false, true);
//            }
            //endregion

//            //  当最后一次时间超过 5 秒时， 超时
//            long tcpLast = getTcpLastHbDt() ;
//            long currentDt = System.currentTimeMillis() ;
//            Log.d(TAG, "tcpLast:"+((tcpLast + Constants.HBTHREAD_TOMEOUT )<currentDt)+"\n"+(tcpLast + Constants.HBTHREAD_TOMEOUT) +"\n"+currentDt);
//            if ((tcpLast+Constants.HBTHREAD_TOMEOUT) < currentDt){
//                Log.d(TAG, "tcp timeout ");
//
//                addTcpTimeOutCount();
//
//                if (getTcpTimeOutCount() == Constants.UDPRECONNECT_TIMEOUT){
//                    isConnect = false ;
//                    tcpClient.close();
//                    tcpClient.start();
//                    if (listener != null){
//                        listener.onConnect(1);
//                    }
//                }
//                SocketClientSender.sendHB(false, false) ;
//            }else {
//                Log.d(TAG, "udp no time out delay sometime to sendHb");
//                if (!isConnect){
//                    if (listener != null) {
//                        listener.onConnect(0);
//                    }
//                    isConnect=true;
//                }
//                tcpTimeOutCount =0;
//                try {
//
//                    if (getTcpHBInterval()>4){
//                        Thread.sleep((getTcpHBInterval()-4)*1000);
//                    }
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                //  心跳发送
//                SocketClientSender.sendHB(false, false);
//            }
//            if (getIsNeedReconnect()){
//                    tcpClient.close();
//                    tcpClient.start();
//            }

            SocketClientSender.sendHB(false, false);
            listener.heartResp();

        }

    }


    //  解析指令
    private void OnDispathCmd(final byte[] page, final boolean isUpd){
//        final Decoder4LoggingOnly decoder4LoggingOnly = new Decoder4LoggingOnly() ;
//        try {
//            decoder4LoggingOnly.decodeHex(page);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        Log.d("cjh", "------------------------------ OnDispathCmd ----------------------------") ;
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //  编译之前需要切割字符集数组
                byte[] data = new byte[page.length-2] ;
                System.arraycopy(page, 1, data, 0, page.length-2);
                try {
//                    Log.d("cjh", "1-------------------------data.length--"+data.length+"--- OnDispathCmd ----------------------------") ;
                    data = Utils.doEscape4Receive(data, 0, data.length - 1);
//                    decoder4LoggingOnly.decodeHex(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }


                PacketData packetData = null;
//                PacketData packetData = new ServerRegisterMsg();
                PacketData.MsgHeader msgHeader = new PacketData.MsgHeader();
                msgHeader.setMsgId(bitOperator.parseIntFromBytes(data, 0, 2));
                int msgBodyProps = bitOperator.parseIntFromBytes(data, 2, 2);
                msgHeader.setMsgBodyPropsField(msgBodyProps);
                msgHeader.setMsgBodyLength(msgBodyProps & 0x3ff);
                msgHeader.setEncryptionType((msgBodyProps & 0x1c00) >> 10);
                msgHeader.setHasSubPackage(((msgBodyProps & 0x2000) >> 13) == 1);
                msgHeader.setReservedBit(((msgBodyProps & 0xC000) >> 14));
                msgHeader.setTerminalPhone(bitOperator.parseBcdStringFromBytes(data, 4, 6));
                msgHeader.setFlowId(bitOperator.parseIntFromBytes(data, 10, 2));
//                Log.d("cjh", "-----------------------------29- OnDispathCmd ---------------------------msgHeader\n-"+msgHeader+"\nmsgBodyProps"+msgBodyProps) ;
                if (msgHeader.isHasSubPackage()) {
                    msgHeader.setPackageInfoField(bitOperator.parseIntFromBytes(data, 12, 4));
                    msgHeader.setTotalSubPackage(bitOperator.parseIntFromBytes(data, 12, 2));
                    msgHeader.setSubPackageSeq(bitOperator.parseIntFromBytes(data, 12, 2));
//                    Log.d("cjh", "------------------------------ OnDispathCmd -------40--------------------msgHeader \n-"+msgHeader) ;
                }
                switch (msgHeader.getMsgId()){
                    case Constants.SERVER_COMMOM_RSP:           //  平台通用应答
                        packetData = new ServerGeneralMsg();
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(data);
                        //        0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
                        int generalResult = ((ServerGeneralMsg) packetData).getResult() ;
//                        setHBInterval();    // 设置心跳时间
//                        String generalReason = null ;
//                        switch (generalResult){
//                            case 0:
//                                generalReason = "成功";
//                                break;
//                            case 1:
//                                generalReason = "失败" ;
//                                break;
//                            case 2:
//                                generalReason = "消息有误" ;
//                                break;
//                            case 3:
//                                generalReason = "不支持" ;
//                                break;
//                            case 4:
//                                generalReason = "报警处理确认" ;
//                                break;
//                            default:
//                                generalReason = "请检查错误码" ;
//                                break;
//                        }
                        if (isUpd){
                            setUdpLastHbDt();
                        }else setTcpLastHbDt();
                        listener.commomResp(generalResult, ResponseReason.getInstance().GENERALRESULT[generalResult], packetData);
                        Log.d("cjh", "----------------------平台通用应答-------- OnDispathCmd " +
                                "--------------46------------\n packetData -"+packetData+
                                "\n 应答流水号((ServerGeneralMsg) packetData).getSerialNumber():"+((ServerGeneralMsg) packetData).getSerialNumber()+
                                "\n 应答 ID((ServerGeneralMsg) packetData).getAnswerId():"+((ServerGeneralMsg) packetData).getAnswerId()) ;
                        break;
                    case Constants.SERVER_REGISTER_RSP:         //  注册应答
                        packetData = new ServerRegisterMsg();
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(data);
                        int registerResult = ((ServerRegisterMsg) packetData).getRegisterResult() ;
//                        switch (registerResult){
//                            case 0:
//                                reason = ((ServerRegisterMsg) packetData).getAuthentication() ;
//                                break;
//                            case 1:
//                                reason = "车辆已被注册" ;
//                                break;
//                            case 2:
//                                reason = "数据库中无该车辆" ;
//                                break;
//                            case 3:
//                                reason = "终端已被注册" ;
//                                break;
//                            case 4:
//                                reason = "数据库中无该终端" ;
//                                break;
//                                default:
//                                    reason = "请检查错误码" ;
//                                    break;
//                        }
                        if (registerResult == 0 ){      //  注册成功
                            listener.registerResp(registerResult, ((ServerRegisterMsg) packetData).getAuthentication());
                            Log.d("cjh", "----------------------心跳-------- OnDispathCmd --------------45------------\n isHBThread  -"+isHBThread) ;
                            setIsOpenHB(true);                                  // 开启心跳
                            if (isHBThread){return;}                           //  心跳线程启动之后不再次启动
                            hBThread.setName("Hb detection thread");
                            Log.d("cjh", "----------------------心跳-------- OnDispathCmd --------------45------------ start -") ;
                            hBThread.start();                                   // 心跳线程启动
                        }else {
                            listener.registerResp(registerResult, ResponseReason.getInstance().REGISTERRESULT[registerResult]);
                        }
                        Log.d("cjh", "-------------------"+((ServerRegisterMsg) packetData).getAuthentication()+
                                "---- 注册应答 ------- OnDispathCmd -------------------46--------\n packetData -"+packetData) ;
                        break;
                    default:
                        packetData = new ServerRegisterMsg();
                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(data);                    //  有可能为空
                        listener.defaultResp(packetData, Constants.NO_REPLY_CODE, msgHeader.getMsgId());
                        Log.d("cjh", "----------------------- 其它应答 ------- OnDispathCmd -------------------46--------\n packetData -"+packetData) ;
                        break;
                }

            }
        });
    }
}
