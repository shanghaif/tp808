package cn.com.erayton.usagreement.data;

import java.nio.charset.Charset;

/**
 * Created by android on 2017/3/31.
 */

public class Constants {

    /**
     * GBK编码格式
     */
    private static final String string_encoding = "UTF-8";

    //  标识位
    public static final int PKG_DELIMITER = 0x7e;

    public static final Charset string_charset = Charset.forName(string_encoding);

    //  平台通用应答
    public static final int SERVER_COMMOM_RSP = 0x8001;
    //  补传分包请求
    public static final int SERVER_SUBCONTRACT_REQ = 0x8003;
    //  终端注册应答
    public static final int SERVER_REGISTER_RSP = 0x8100;
    //  位置信息查询
//    public static final int SERVER_LOCATION_REQ = 0x8100;

    //  设置终端参数
    public static final int TERMINAL_PARAMETERS_SETTING = 0x8103 ;
    //  终端参数子参数
        //  DWORD 位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
        public static final int TERMINAL_PARAMETERS_SETTING_0X0020 = 0x0020 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0021 = 0x0021 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0022 = 0x0022 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0027 = 0x0027 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0028 = 0x0028 ;
        //  DWORD 缺省时间汇报间隔，单位为秒（s），>0
        public static final int TERMINAL_PARAMETERS_SETTING_0X0029 = 0x0029 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0030 = 0x0030 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0031 = 0x0031 ;

    //  查询终端参数
    public static final int TERMINAL_PARAMETERS_QUERY = 0x8104 ;
    //  终端控制
    public static final int TERMINAL_CONTROL = 0x8105 ;
    //  查询指定终端参数
    public static final int TERMINAL_PARAMETERS_SPECIFY_QUERY = 0x8106 ;
    //  查询终端属性
    public static final int TERMINAL_PROPERTIES_QUERY = 0x8107 ;
    //  下发终端升级包
    public static final int TERMINAL_ISSUE_UPGRADE_PACKAGE = 0x8108 ;
    //  位置信息查询
    public static final int TERMINAL_LOCATION_INFORMATION_QUERY = 0x8201;
    //  临时位置跟踪控制
    public static final int SERVER_LOCATION_TMP_REQ = 0x8202;
    //  人工确认报警消息
    public static final int TERMINAL_CONFIRM_ALARM = 0x8203;
    //  文本信息下发
    public static final int SERVER_DISTRIBUTION_MSG = 0x8300;
    //  事件设置
    public static final int SERVER_EVENT_SET = 0x8301;
    //  提问下发
    public static final int SERVER_QUESTIONS_ISSUED = 0x8302;
    //  信息点播菜单设置
    public static final int SERVER_INFORMATION_DEMAND = 0x8303;
    //  信息服务
    public static final int SERVER_INFORMATION_SERVICE = 0x8304;
    //  电话回拨
    public static final int SERVER_TEL_RESPONSE = 0x8400;
    //  设置电话本
    public static final int SERVER_PHONE_BOOK = 0x8401;
    //  车辆控制
    public static final int SERVER_VEHICLE_CONTROL = 0x8500;

//    p32

    //  实时音视频传输请求
    public static final int SERVER_AVTRANSMISSION_REQUEST = 0x9101;
    //  音视频实时传输控制
    public static final int SERVER_AVTRANSMISSION_CONTROL = 0x9102;


















    //  终端通用应答
    public static final int TERMINAL_CONMOM_RSP = 0x0001;
    //  终端心跳
    public static final int TERMINAL_HEART_BEAT = 0x0002;
    //  终端注销
    public static final int TERMINAL_UNREGISTER = 0x0003;
    //  终端注册
    public static final int TERMINAL_REGISTER = 0x0100;
    //  终端鉴权
    public static final int TERMINAL_AUTHEN = 0x0102;
    //  查询终端参数应答
    public static final int SERVER_PARAMETERS_QUERY_RSP = 0x0104;
    //  查询终端属性应答
    public static final int SERVER_PROPERTIES_REQ = 0x0107;
//      终端升级结果通知
//    public static final int TERMINAL_UPGRADE_RESULTS = 0x0107;
    //  位置信息汇报
    public static final int TERMINAL_LOCATION_UPLOAD = 0x0200;
    //  位置信息查询应答
    public static final int TERMINAL_LOCATION_RSP = 0x0201;
    //  事件报告
    public static final int TERMINAL_INCIDENT_REPORT = 0x0301;
    //  提问应答
    public static final int TERMINAL_QUESTIONS_ANSWER = 0x0302;
    //  信息点播/取消
    public static final int TERMINAL_INFORMATION_OPERATION = 0x0303;
    //  车辆控制应答
    public static final int TERMINAL_VEHICLE_CONTROL_RESPONSE = 0x0500;
    //  定位数据批量上传
    public static final int TERMINAL_LOCATION_BATCH_UPLOAD = 0x0704;

    public static int TCP_CLIENT_IDLE_MINUTES = 30;

    public static final int HEART_BEAT_INTERVAL = 15 * 1000;

    public static final String NO_REPLY_CODE = "应答码错误" ;





//    ============================================================================================
    public static final int MILLISECONDSTOSECONDS = 1000 ;
    //    public static final int UDPThreadTime = 1 * 1000 ;     //  sec
    public static final int UDPTHREADT_SLEEPIME = 1 * MILLISECONDSTOSECONDS ;     //  sec
    public static final int UDPRECONNECT_TIMEOUT = 5 ;       //  重连超时次数

    public static final int TCPTHREAD_TIMEOUT = 30 * MILLISECONDSTOSECONDS ;     //  sec
    public static final int TCPTHREAD_SO_TIMEOUT = 8 * 61 * MILLISECONDSTOSECONDS ;     //  8 min 8 sec
    public static final int TCPTHREAD_SLEEPIME = 1 * MILLISECONDSTOSECONDS ;     //  sec
    public static final int TCPRECONNECT_TIMEOUT = 5 ;       //  重连超时次数

    public static final int HBOPENTHREAD_SLEEPIME = 1 * MILLISECONDSTOSECONDS ;     //  sec


//    =============================== socketclient ===============================
    /**
     * 心跳间隔 2 分钟
     * */
    public static final int HBTHREAD_SLEEPIME = 3 * 60 * MILLISECONDSTOSECONDS ;     //  3 分钟

    /**
     * 心跳时间间隔检测时间 3 分钟
     * */
    public static final int HBTHREAD_INTERVAL = 2 * HBTHREAD_SLEEPIME  ;           //  2次心跳间隔没有信息交互即发送心跳

    /**
     * 重连间隔 10 sec
     * */
    public static final int RECONNECT_INTERVAL = 10 * MILLISECONDSTOSECONDS ;         //  重连间隔时间 10 sec

    /**
     * 心跳开启等待时间 5 秒检测一次
     * */
    public static final int HBTHREAD_TOMEOUT = 5 * MILLISECONDSTOSECONDS;           //  心跳开启检测时间


//    ============================================================================================
    //    0：成功；1：车辆已被注册；2：数据库中无该车辆；3：终端已被注册；4：数据库中无该终端
    public static final int CODE_ZERO = 0 ;
    public static final int CODE_ONE = 1 ;
    public static final int CODE_TWO = 2 ;
    public static final int CODE_THREE = 3 ;
    public static final int CODE_FOUR = 4 ;





//    private static final int THREADTIME =  1000;     // 进制
//    public static final int UDPTHREADSEC = UDPThreadTime * THREADTIME;     //  realTime

}
