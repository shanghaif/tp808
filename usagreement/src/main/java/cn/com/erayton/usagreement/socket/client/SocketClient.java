package cn.com.erayton.usagreement.socket.client;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.com.erayton.usagreement.data.Constants;
import cn.com.erayton.usagreement.data.ResponseReason;
import cn.com.erayton.usagreement.model.decode.PacketData;
import cn.com.erayton.usagreement.model.decode.ServerAVTranslateControlMsg;
import cn.com.erayton.usagreement.model.decode.ServerAVTranslateMsg;
import cn.com.erayton.usagreement.model.decode.ServerCouldControlMsg;
import cn.com.erayton.usagreement.model.decode.ServerFileUploadControlMsg;
import cn.com.erayton.usagreement.model.decode.ServerFileUploadMsg;
import cn.com.erayton.usagreement.model.decode.ServerGeneralMsg;
import cn.com.erayton.usagreement.model.decode.ServerParametersMsg;
import cn.com.erayton.usagreement.model.decode.ServerRegisterMsg;
import cn.com.erayton.usagreement.model.decode.ServerResourceQueryMsg;
import cn.com.erayton.usagreement.model.decode.ServerRotateMsg;
import cn.com.erayton.usagreement.model.decode.ServerTransferStatusMsg;
import cn.com.erayton.usagreement.model.decode.ServerVideoReplayControlMsg;
import cn.com.erayton.usagreement.model.decode.ServerVideoReplayMsg;
import cn.com.erayton.usagreement.socket.core.TCPClient;
import cn.com.erayton.usagreement.socket.core.UDPClient;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
import cn.com.erayton.usagreement.utils.LogUtils;
import cn.com.erayton.usagreement.utils.Utils;

/**
 * TCP 连接
 * */
public class SocketClient implements TCPClient.TCPClientListener, UDPClient.UDPClientListener, Runnable {
    public interface SocketClientListener{
//        0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
        /**
         * @param result 0：成功/确认；1：失败；2：消息有误；
         *               3：不支持；4：报警处理确认；
         * @param reason 返回原因
         * */
        void commomResp(int result, String reason) ;        //  通用应答
        /**
         * @param result    0：成功；1：车辆已被注册；2：数据库中无该车辆；
         *                  3：终端已被注册；4：数据库中无该终端
         * @param authCode  成功之后返回鉴权码，失败返回 对应原因
         * */
        void registerResp(int result, String authCode) ;       //  注册应答，返回鉴权码
        void authResp(int result, String reason) ;           //  鉴权
        void heartResp(int result, String reason) ;          //  心跳
        void gpsResp(int result, String reason) ;            //  GPS 数据
        void defaultResp(String errorMsg, int replyCode) ;       //  未加应答码
        void onConnect(int i) ;          //  是否连接
        void onDisConnect() ;
        void onSend(byte[] data, boolean result) ;
        /**     设置终端参数
         * @param instructCount 指令数
         * @param GpsSleepInterval GPS 休眠上传间隔
         * @param GpsDefInterval GPS 缺省上传间隔
         * @param flowId 流水号
         * */
        void onTernimalParameterSetting(int instructCount, int GpsSleepInterval, int GpsDefInterval, int flowId) ;
        void queryTernimalParameterSetting(int serNum) ;

        /** 查询终端音视频属性
         */
        void onAVPropertiesQuery() ;

        /**     实时视频传输请求
         * @param  host 服务器 IP 地址
         * @param tcpPort 服务器 TCP 端口号
         * @param udpPort 服务器 UDP 端口号
         * @param channelNum 逻辑通道号
         * @param dataType 数据类型
         * @param steamType 码流类型
         * @param flowId 流水号
         * */
        void onTernimalAVTranslate(String host, int tcpPort, int udpPort,
                                   int channelNum, int dataType, int steamType, int flowId) ;
        /**     音视频传输控制
         * @param controlCode 控制指令  0,关闭音视频传输指令 1,切换码流  2,暂停该通道所有流的发送   3,恢复暂停前流的发送,与暂停前的流类型一致  4,关闭双向对讲
         * @param channelNum 逻辑通道号
         * @param avCode 音频类型
         * @param steamType 码流类型    0,主码流   1,子码流
         * */
        void onAVControl(int controlCode, int channelNum, int avCode, int steamType) ;

        /**
         * 查询资源列表
         * @param serNum    流水号
         * @param channelNum    逻辑通道号
         * @param startTime     开始时间
         * @param endTime       结束时间
         * @param warningMark   报警标志
         * @param resourceType  音视频资源类型
         * @param steamType     码流类型
         * @param memoryType    存储器类型
         */
        void onQueryResourceReq(int serNum, int channelNum, String startTime, String endTime, String warningMark,
                                int resourceType, int steamType, int memoryType
                                ) ;

        /** 平台下发远程录像回放请求
         *
         * @param msg
         */
        void onAVReplayReq(ServerVideoReplayMsg msg) ;
        /** 平台下发远程录像回放控制
         *
         * @param channelNum        逻辑通道号
         * @param playbackControl   回放控制
         * @param multiple          快进或快退倍数
         * @param dragTo            拖动回放位置
         */
        void onAVReplayControl(int channelNum, int playbackControl, int multiple, String dragTo) ;

        /** 文件上传指令
         *
         * @param seNum
         * @param msg
         */
        void onFileUploadReq(int seNum, ServerFileUploadMsg msg) ;

        /** 文件上传控制
         *
         * @param seNum
         * @param uploadControl 上传控制, 0,暂停  1,继续    2,取消
         */
        void onFileUploadControl(int seNum, int uploadControl) ;


        /** 云台旋转
         *
         * @param channelNum    逻辑通道号
         * @param direction 0 停止, 1上, 2下, 3左, 4右
         * @param speech    0 ~ 255
         */
        void onRotateCloudControl(int channelNum, int direction, int speech) ;

        /**
         * 云台控制
         * @param controlType 控制类型, 消息id
         * @param channelNum    逻辑通道号
         * @param num 0 调大/停止      1 调小/启动
         */
        void onCloudControl(int controlType, int channelNum, int num) ;
    }



    private Thread hBThread = new Thread(this) ;                    //  心跳发送线程，登陆成功之后发送
    private boolean isHBThread = false ;                                   //   心跳线程是否已启用

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
    public void updateTcpLastHbDt() {synchronized (tcpLastHbDtLock){
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

    private long hBInterval = 90 ;            //  心跳时间间隔    3 分钟
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


//    private boolean mIsOpenHbDetectionThread = false;
//    private Object mIsOpenHbDetectionThreadLock = new Object();
//    private boolean getIsOpenHbDetectionThread() { synchronized (mIsOpenHbDetectionThreadLock) { return mIsOpenHbDetectionThread; } }
//    private void setIsOpenHbDetectionThread(boolean isOpenHbDetectionThread) { synchronized (mIsOpenHbDetectionThreadLock) { mIsOpenHbDetectionThread = isOpenHbDetectionThread; } }



    private boolean isLogin = false ;
    private Object isLoginLock = new Object() ;
    public boolean getIsLogin() {synchronized (isLoginLock){
        return isLogin;
    }}
    public void setIsLogin(boolean login) {synchronized (isLoginLock){
        isLogin = login;
    }}

    private boolean isOpenUdp = true ;

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
        if (isOpenUdp){
            udpClient.close();
        }else
            tcpClient.close();
        setIsOpenHB(false);
    }

    public boolean isConnected(){
        return !getIsNeedReconnect() ;
    }

    public boolean sendTcpMsg(PacketData packetData, boolean isAsyn){
//        MsgTransformer msgTransformer = new MsgTransformer() ;
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!isAsyn){
            if (!tcpClient.sendAsyn(packetData.packageDataToByte())) {
//            if (!tcpClient.sendAsyn(msgTransformer.packageDataToByte(packetData))) {
                LogUtils.d( "Send " + packetData.packageDataToByte()  + " to fail. [Asyn][TCP] error:" + packetData.packageDataToByte() );
//                LogUtils.d( "Send " + msgTransformer.packageDataToByte(packetData)  + " to fail. [Asyn][TCP] error:" + msgTransformer.packageDataToByte(packetData) );
                return false;
            }
        }else {
//            LogUtils.d( "------------------------------ isAsyn ----------------------------") ;
            if (!tcpClient.send(packetData.packageDataToByte())) {
//            if (!tcpClient.send(msgTransformer.packageDataToByte(packetData))) {
                LogUtils.d( "Send " + packetData.packageDataToByte() + " to fail. [Sync][TCP] error:" + packetData.packageDataToByte());
//                LogUtils.d( "Send " + msgTransformer.packageDataToByte(packetData) + " to fail. [Sync][TCP] error:" + msgTransformer.packageDataToByte(packetData));
                return false;
            } else {
//                LogUtils.d( "Send " + msgTransformer.packageDataToByte(packetData) + " success.[TCP][Sync]");
                LogUtils.d( "Send " + packetData.packageDataToByte() + " success.[TCP][Sync]");
            }
        }

        return true ;
    }

    public boolean sendTcpMsg(byte[] data, boolean isAsyn){
        if (isAsyn) {
//            LogUtils.d( "------------------------------ isAsyn ----------------------------") ;
//            tcpClient.sendAsyn(data) ;
            if (!tcpClient.sendAsyn(data)) {
                LogUtils.d( "Send " + data + " to fail. [Asyn][TCP] error:" + data);
                return false;
            }
        }else {
//            LogUtils.d( "------------------------------ !isAsyn ----------------------------") ;
            if (!tcpClient.send(data)) {
                LogUtils.d( "Send " + data + " to fail. [Sync][TCP] error:" + data);
                return false;
            } else {
                LogUtils.d( "Send " + data + " success.[TCP][Sync]:"+data.length);
            }
        }
        return true ;
    }
    public boolean sendUdpMsg(PacketData packetData, boolean isAsyn){
        return true ;
    }

    public boolean sendUdpMsg(byte[] data, boolean isAsyn){
        LogUtils.d( "Send " + HexStringUtils.toHexString(data));
        if (!isAsyn) {
            udpClient.sendAsyn(data) ;
            if (!udpClient.sendAsyn(data)) {
                LogUtils.d( "Send " + data + " to fail. [Asyn][UDP] error");
                return false;
            }
        }else {
            if (!udpClient.send(data)) {
                LogUtils.d( "Send " + data + " to fail. [Sync][UDP] error");
                return false;
            } else {
                LogUtils.d( "Send " + data + " success.[UDP][Sync]");
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

    /**
     * 鉴权成功才允许发送心跳
     * */
    @Override
    public void onTcpConnect(int result) {
        LogUtils.d( "OnConnect:" + result);
        if (result == 0){

            setIsNeedReconnect(false) ;
            //  连接成功
        }else if(result == -1){
            //  无法连接服务器, 与服务器断开
//            if (tcpClient != null){
//                tcpClient.close();
//                LogUtils.d( "-------------------- restart") ;
//                tcpClient.start();
//            }
            setIsNeedReconnect(true) ;
        }else {
            LogUtils.d( "OnConnect else -- " + result);
        }

        listener.onConnect(result);
    }

    @Override
    public void onTcpDisConnect() {
        if (listener != null){
            listener.onDisConnect();
        }
        LogUtils.d( "onDisConnect -- ");
        setIsNeedReconnect(true);
    }

    @Override
    public void onTcpSend(byte[] data, boolean result) {
//      something here

        if (listener != null) {
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
        LogUtils.d( "heart run -------------------------------------------") ;
        //  心跳规则
        while (true){

            //  等待开启心跳
            if (!getIsOpenHB()){
                try {
                    LogUtils.d( "wait for heartbert -------------------------------------------") ;
                    Thread.sleep(Constants.HBTHREAD_TOMEOUT);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            }

            final long tcpLastHbDt = getTcpLastHbDt() ;
            final long curDt = System.currentTimeMillis();

//            if (!isConnected() || (tcpLastHbDt + (getHBInterval() + 30) * 1000) > curDt) {
            //  两次心跳未收到回复即进行重连
            if (!isConnected() || (tcpLastHbDt + (getHBInterval() * 3 + 30) * 1000) < curDt) {
                LogUtils.d( "Hb is timeout, will be reconnect.");
                setIsNeedReconnect(true);
            }

            if (getIsNeedReconnect()){
                LogUtils.d( "reconnecting") ;
                tcpClient.close();
                if (tcpClient.connect(getTcpIp(), getTcpPort()) == 0) {
                    LogUtils.d( "reconnect success.");
                    //Reset QoS
                    //QoS.ReSet();
                    updateTcpLastHbDt() ;
                    setIsNeedReconnect(false);
                } else {
                    LogUtils.d( "reconnect to fail.");
                }

                try {
                    Thread.sleep(Constants.RECONNECT_INTERVAL);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continue;
            } else{
                if ((tcpLastHbDt + getHBInterval() * 1000) <= curDt) {
                    //  有信息发送的情况下不再发送心跳
                    SocketClientSender.sendHB(false, false);
                }
            }

            try {
                Thread.sleep(Constants.HBTHREAD_SLEEPIME);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

    }


    //  解析指令
    private void OnDispathCmd(final byte[] page, final boolean isUpd){
        poolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //  编译之前需要切割字符集数组
                byte[] data = new byte[page.length-2] ;
                System.arraycopy(page, 1, data, 0, page.length-2);
                try {
                    data = Utils.doEscape4Receive(data, 0, data.length - 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (isUpd){
                    setUdpLastHbDt();
                }else updateTcpLastHbDt();

                PacketData packetData = null;
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
                if (msgHeader.isHasSubPackage()) {      //  有分包
                    msgHeader.setPackageInfoField(bitOperator.parseIntFromBytes(data, 12, 4));
                    msgHeader.setTotalSubPackage(bitOperator.parseIntFromBytes(data, 12, 2));
                    msgHeader.setSubPackageSeq(bitOperator.parseIntFromBytes(data, 12, 2));
                }
                LogUtils.d("--------------------------- hex  --------------------------- \n"+HexStringUtils.toHexString(page)) ;
                switch (msgHeader.getMsgId()){
                    case Constants.SERVER_COMMOM_RSP:           //  平台通用应答
                        packetData = new ServerGeneralMsg();
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(data);
                        //        0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
                        int generalResult = ((ServerGeneralMsg) packetData).getResult() ;
                        if (Constants.TERMINAL_LOCATION_UPLOAD == ((ServerGeneralMsg) packetData).getAnswerId()){
                            listener.gpsResp(generalResult, ResponseReason.getInstance().GENERALRESULT[generalResult]);
                        }else if (Constants.TERMINAL_HEART_BEAT == ((ServerGeneralMsg) packetData).getAnswerId()){
                            listener.heartResp(generalResult, ResponseReason.getInstance().GENERALRESULT[generalResult]);
                        }else if (Constants.TERMINAL_AUTHEN == ((ServerGeneralMsg) packetData).getAnswerId()){
                            if (generalResult == 0)  setIsOpenHB(true);
                            listener.authResp(generalResult, ResponseReason.getInstance().GENERALRESULT[generalResult]);
                        }else {
                            listener.commomResp(generalResult, ResponseReason.getInstance().GENERALRESULT[generalResult]);
                        }
                        LogUtils.d( "----------------------平台通用应答-------- OnDispathCmd " +
                                "--------------46------------\n packetData -"+packetData+
                                "\n 应答流水号((ServerGeneralMsg) packetData).getSerialNumber():"+((ServerGeneralMsg) packetData).getSerialNumber()+
                                "\n 应答 ID((ServerGeneralMsg) packetData).getAnswerId():"+((ServerGeneralMsg) packetData).getAnswerId()) ;
                        break;
                    case Constants.SERVER_REGISTER_RSP:         //  注册应答
                        packetData = new ServerRegisterMsg();
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(data);
                        int registerResult = ((ServerRegisterMsg) packetData).getRegisterResult() ;
                        LogUtils.d( "----------------------注册应答-------- OnDispathCmd --------------45------------\n registerResult:"+registerResult+
                                "auth:"+((ServerRegisterMsg) packetData).getAuthentication()
                                ) ;

                        if (registerResult == 0 ){      //  注册成功
                            listener.registerResp(registerResult, ((ServerRegisterMsg) packetData).getAuthentication());
                            setIsOpenHB(true);                                  // 开启心跳
                            if (!isHBThread){
                                hBThread.setName("Hb detection thread");
                                hBThread.start();                                   // 心跳线程启动
                            }                           //  心跳线程启动之后不再次启动

                            isHBThread =true ;
                        }else {
                            listener.registerResp(registerResult, ResponseReason.getInstance().REGISTERRESULT[registerResult]);
                        }
                        LogUtils.d( "-------------------"+((ServerRegisterMsg) packetData).getAuthentication()+
                                "---- 注册应答 ------- OnDispathCmd -------------------46--------\n packetData -"+packetData) ;
                        break;

                    case Constants.TERMINAL_PARAMETERS_SETTING:
                        packetData = new ServerParametersMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        LogUtils.d( "----------------------- 设置终端参数 ---------------------------\n packetData -"+packetData) ;
                        listener.onTernimalParameterSetting(((ServerParametersMsg) packetData).getInstructCount(), ((ServerParametersMsg) packetData).getGpsSleepInterval(),
                                ((ServerParametersMsg) packetData).getGpsDefInterval(), packetData.getMsgHeader().getFlowId());
                        break;
                    case Constants.TERMINAL_PARAMETERS_SPECIFY_QUERY:
                        listener.queryTernimalParameterSetting(msgHeader.getFlowId());
                        LogUtils.d( "----------------------- 查询指定终端参数 ---------------------------\n packetData -"+packetData) ;
                        break;


                    case Constants.SERVER_AVPROPERTIES_QUERY:
                        LogUtils.d( "----------------------- 查询终端音视频属性 0x9003 消息体为空---------------------------\n packetData -"+packetData) ;

//                        TerminalAVPropertieInfo info = new TerminalAVPropertieInfo() ;
//                        info.setAudioEncoding(1);
//                        info.setAudioChannel(1);
//                        info.setAudioRate(0);
//                        info.setAudioNum(0);
//                        info.setAudioLength(1234);
//                        info.setAudioSupport(1);
//                        info.setVideoEncoding(98);
//                        info.setMaxAudioChannel(1);
//                        info.setMaxVideoChannel(1);
//                        SocketClientSender.sendAVPropertie(info, false, false) ;
                        listener.onAVPropertiesQuery();

                        break;
                    case Constants.SERVER_AVTRANSMISSION_REQUEST:
                        packetData = new ServerAVTranslateMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
//                        listener.onTernimalAVTranslate((ServerAVTranslateMsg) packetData);
                        listener.onTernimalAVTranslate(((ServerAVTranslateMsg) packetData).getHost(), ((ServerAVTranslateMsg) packetData).getTcpPort(),
                                ((ServerAVTranslateMsg) packetData).getUdpPort(), ((ServerAVTranslateMsg) packetData).getChannelNum(),
                                ((ServerAVTranslateMsg) packetData).getDataType(), ((ServerAVTranslateMsg) packetData).getSteamType(),
                                packetData.getMsgHeader().getFlowId());
                        LogUtils.d( "----------------------- 实时音视频传输请求 0x9101 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_AVTRANSMISSION_CONTROL:
                        packetData = new ServerAVTranslateControlMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        listener.onAVControl(((ServerAVTranslateControlMsg) packetData).getControlCode(), ((ServerAVTranslateControlMsg) packetData).getChannelNum(),
                                ((ServerAVTranslateControlMsg) packetData).getCloseType(), ((ServerAVTranslateControlMsg) packetData).getSteamType());
                        LogUtils.d( "----------------------- 音视频实时传输控制 0x9102 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_AVSTATUS_NOTIC:
                        packetData = new ServerTransferStatusMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        LogUtils.d( "----------------------- 实时音视频传输状态通知 0x9105 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_RESOURCE_QUERY:
                        packetData = new ServerResourceQueryMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        LogUtils.d( "----------------------- 查询资源列表 0x9205 ---------------------------\n packetData -"+packetData) ;
//                        SocketClientSender.sendAVResourceList(msgHeader.getFlowId(),false, false) ;
                        listener.onQueryResourceReq(msgHeader.getFlowId(), ((ServerResourceQueryMsg) packetData).getChannelNum(),
                        ((ServerResourceQueryMsg) packetData).getStartTime(), ((ServerResourceQueryMsg) packetData).getEndTime(),
                        String.valueOf(((ServerResourceQueryMsg) packetData).getWarningMark()), ((ServerResourceQueryMsg) packetData).getResourceType(),
                        ((ServerResourceQueryMsg) packetData).getSteamType(), ((ServerResourceQueryMsg) packetData).getMemoryType()
                        );
                        break;

                    case Constants.SERVER_AVREPLAY_REQUEST:
                        packetData = new ServerVideoReplayMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        listener.onAVReplayReq((ServerVideoReplayMsg)packetData);
                        LogUtils.d( "----------------------- 平台下发远程录像回放请求 0x9201 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_AVREPLAY_CONTROL:
                        packetData = new ServerVideoReplayControlMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        listener.onAVReplayControl(((ServerVideoReplayControlMsg) packetData).getChannelNum(), ((ServerVideoReplayControlMsg) packetData).getPlaybackControl(),
                                ((ServerVideoReplayControlMsg) packetData).getMultiple(), ((ServerVideoReplayControlMsg) packetData).getDragTo()
                                ) ;
                        LogUtils.d( "----------------------- 平台下发远程录像回放控制 0x9202 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_FILEUPLOAD_REQUEST:
                        packetData = new ServerFileUploadMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        LogUtils.d( "----------------------- 文件上传指令 0x9206 ---------------------------\n packetData -"+packetData) ;
//                        SocketClientSender.sendUploadResp(msgHeader.getFlowId(), 0) ;
                        listener.onFileUploadReq(msgHeader.getFlowId(), (ServerFileUploadMsg)packetData);

                        break;
                    case Constants.SERVER_FILEUPLOAD_CONTROL:
                        packetData = new ServerFileUploadControlMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        listener.onFileUploadControl(((ServerFileUploadControlMsg) packetData).getSerNum(), ((ServerFileUploadControlMsg) packetData).getUploadControl());
                        LogUtils.d( "----------------------- 文件上传控制 0x9207 ---------------------------\n packetData -"+packetData) ;
                        break;


                    case Constants.SERVER_CLOUD_CONTROL_ROTATE:
                        packetData = new ServerRotateMsg() ;
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        listener.onRotateCloudControl(((ServerRotateMsg) packetData).getChannelNum(), ((ServerRotateMsg) packetData).getDirection(), ((ServerRotateMsg) packetData).getSpeech()) ;
                        LogUtils.d( "----------------------- 云台旋转 0X9301 ---------------------------\n packetData -"+packetData) ;
                        break;
                    case Constants.SERVER_CLOUD_CONTROL_FOCALLENGTH:
                    case Constants.SERVER_CLOUD_CONTROL_APERTURE:
                    case Constants.SERVER_CLOUD_CONTROL_WIPER:
                    case Constants.SERVER_CLOUD_CONTROL_INFRAREDLIGHT:
                    case Constants.SERVER_CLOUD_CONTROL_ZOOM:
                        packetData = new ServerCouldControlMsg();
                        packetData.setMsgHeader(msgHeader);
                        packetData.inflatePackageBody(page);
                        LogUtils.d( "----------------------- 云台控制 0X9302 0X9303 0X9304 0X9305 0X9306 ---------------------------\n packetData -"+packetData) ;
                        listener.onCloudControl(msgHeader.getMsgId(), ((ServerCouldControlMsg) packetData).getChannelNum(), ((ServerCouldControlMsg) packetData).getNum());
                        break;
//                    case Constants.SERVER_CLOUD_CONTROL_FOCALLENGTH:
//                        packetData = new ServerFocalLengthMsg() ;
//                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(page);
//                        LogUtils.d( "----------------------- 云台调整焦距 0X9302 ---------------------------\n packetData -"+packetData) ;
//                        break;
//                    case Constants.SERVER_CLOUD_CONTROL_APERTURE:
//                        packetData = new ServerApertureMsg() ;
//                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(page);
//                        LogUtils.d( "----------------------- 云台调整光圈 0X9303 ---------------------------\n packetData -"+packetData) ;
//                        break;
//                    case Constants.SERVER_CLOUD_CONTROL_WIPER:
//                        packetData = new ServerWiperMsg() ;
//                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(page);
//                        LogUtils.d( "----------------------- 云台控制雨刷 0X9304 ---------------------------\n packetData -"+packetData) ;
//                        break;
//                    case Constants.SERVER_CLOUD_CONTROL_INFRAREDLIGHT:
//                        packetData = new ServerInfraredlightMsg() ;
//                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(page);
//                        LogUtils.d( "----------------------- 红外补光 0X9305 ---------------------------\n packetData -"+packetData) ;
//                        break;
//                    case Constants.SERVER_CLOUD_CONTROL_ZOOM:
//                        packetData = new ServerZoomMsg() ;
//                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(page);
//                        LogUtils.d( "----------------------- 云台变倍 0X9306 ---------------------------\n packetData -"+packetData) ;
//                        break;

                    default:
                        packetData = new ServerRegisterMsg();
                        packetData.setMsgHeader(msgHeader);
//                        packetData.inflatePackageBody(data);                    //  有可能为空
                        listener.defaultResp(Constants.NO_REPLY_CODE, msgHeader.getMsgId());

                        LogUtils.d( "----------------------- 其它应答 ------- OnDispathCmd ---------------------------\n packetData -"+packetData+"\n hex:"+HexStringUtils.toHexString(page)) ;
                        break;
                }

            }
        });
    }


}