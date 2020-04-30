package cn.com.erayton.usagreement.data;

import com.library.live.vd.VDEncoder;

import java.nio.charset.Charset;

import cn.com.erayton.usagreement.service.VideoPushService;

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

        //  终端心跳发送间隔，单位为秒（s）
        public static final int TERMINAL_PARAMETERS_SETTING_0X0001 = 0x0001 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0002 = 0x0002 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0003 = 0x0003 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0004 = 0x0004 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0005 = 0x0005 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0006 = 0x0006 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0007 = 0x0007 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0010 = 0x0010 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0011 = 0x0011 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0012 = 0x0012 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0013 = 0x0013 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0014 = 0x0014 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0015 = 0x0015 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0016 = 0x0016 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0017 = 0x0017 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0018 = 0x0018 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0019 = 0x0019 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X001A = 0x001A ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X001B = 0x001B ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X001C = 0x001C ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X001D = 0x001D ;

        //  DWORD 位置汇报策略，0：定时汇报；1：定距汇报；2：定时和定距汇报
        public static final int TERMINAL_PARAMETERS_SETTING_0X0020 = 0x0020 ;
        //  位置汇报方案
        public static final int TERMINAL_PARAMETERS_SETTING_0X0021 = 0x0021 ;
        //  驾驶员未登录汇报时间间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X0022 = 0x0022 ;
        //  休眠时汇报时间间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X0027 = 0x0027 ;
        //  紧急报警时汇报时间间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X0028 = 0x0028 ;
        //  DWORD 缺省时间汇报间隔，单位为秒（s），>0
        public static final int TERMINAL_PARAMETERS_SETTING_0X0029 = 0x0029 ;
        //  缺省距离汇报间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X002C = 0x002C ;
        //  驾驶员未登录汇报距离间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X002D = 0x002D ;
        //  休眠时汇报距离间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X002E = 0x002E ;
        //  紧急报警时汇报距离间隔
        public static final int TERMINAL_PARAMETERS_SETTING_0X002F = 0x002F ;
        //  拐点补传角度
        public static final int TERMINAL_PARAMETERS_SETTING_0X0030 = 0x0030 ;
        //  电子围栏半径（非法位移阈值）
        public static final int TERMINAL_PARAMETERS_SETTING_0X0031 = 0x0031 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0040 = 0x0040 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0041 = 0x0041 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0042 = 0x0042 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0043 = 0x0043 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0044 = 0x0044 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0045 = 0x0045 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0046 = 0x0046 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0047 = 0x0047 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0048 = 0x0048 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0049 = 0x0049 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0050 = 0x0050 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0051 = 0x0051 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0052 = 0x0052 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0053 = 0x0053 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0054 = 0x0054 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0055 = 0x0055 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0056 = 0x0056 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0057 = 0x0057 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0058 = 0x0058 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0059 = 0x0059 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X005A = 0x005A ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X005B = 0x005B ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X005C = 0x005C ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X005D = 0x005D ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X005E = 0x005E ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0064 = 0x0064 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0065 = 0x0065 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0070 = 0x0070 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0071 = 0x0071 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0072 = 0x0072 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0073 = 0x0073 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0074 = 0x0074 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0080 = 0x0080 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0081 = 0x0081 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0082 = 0x0082 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0083 = 0x0083 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0084 = 0x0084 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0090 = 0x0090 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0091 = 0x0091 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0092 = 0x0092 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0093 = 0x0093 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0094 = 0x0094 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0095 = 0x0095 ;

        public static final int TERMINAL_PARAMETERS_SETTING_0X0100 = 0x0100 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0101 = 0x0101 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0102 = 0x0102 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0103 = 0x0103 ;
        public static final int TERMINAL_PARAMETERS_SETTING_0X0110 = 0x0110 ;

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

    //  查询终端音视频属性
    public static final int SERVER_AVPROPERTIES_QUERY = 0x9003;
    //  实时音视频传输请求
    public static final int SERVER_AVTRANSMISSION_REQUEST = 0x9101;
    //  音视频实时传输控制
    public static final int SERVER_AVTRANSMISSION_CONTROL = 0x9102;
    //  实时音视频传输状态通知
    public static final int SERVER_AVSTATUS_NOTIC = 0x9105;


    //  平台下发远程录像回放请求
    public static final int SERVER_AVREPLAY_REQUEST = 0x9201;
    //  平台下发远程录像回放控制
    public static final int SERVER_AVREPLAY_CONTROL = 0x9202;
    //  查询资源列表
    public static final int SERVER_RESOURCE_QUERY = 0x9205;
    //  文件上传指令
    public static final int SERVER_FILEUPLOAD_REQUEST = 0x9206;
    //  文件上传控制
    public static final int SERVER_FILEUPLOAD_CONTROL = 0x9207;


    //  云台控制指令
    //  云台旋转
    public static final int SERVER_CLOUD_CONTROL_ROTATE = 0X9301;
    //  云台调整焦距
    public static final int SERVER_CLOUD_CONTROL_FOCALLENGTH = 0X9302;
    //  云台调整光圈
    public static final int SERVER_CLOUD_CONTROL_APERTURE = 0X9303;
    //  云台控制雨刷
    public static final int SERVER_CLOUD_CONTROL_WIPER = 0X9304;
    //  红外补光
    public static final int SERVER_CLOUD_CONTROL_INFRAREDLIGHT = 0X9305;
    //  云台变倍
    public static final int SERVER_CLOUD_CONTROL_ZOOM = 0X9306;
















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
    //  终端上传音视频属性
    public static final int TERMINAL_AVPROPERTIE_UPLOAD = 0x1003;
    //  终端上传乘客流量
    public static final int TERMINAL_RIDERSHIP_UPLOAD = 0x1005;
    //  音视频资源列表上传
    public static final int TERMINAL_RESOURCE_LIST_UPLOAD= 0x1205;
    //  文件上传完成通知
    public static final int TERMINAL_RESOURCE_STUTUS_UPLOAD= 0x1206;




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
     * 心跳间隔 3 分钟
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


//    ============================================================================================
    //  808 协议消息体解析起始位
    /**
     * 无分包消息体解析起始位
     * */
    public static final int MSGBODY_START_INDEX = 13 ;
    public static final int MSGBODY_DEFAULT_START_INDEX = 12 ;
    /**
     * 有分包消息体解析起始位
     * */
    public static final int MSGBODY_SUBPACKAGE_START_INDEX = 17 ;
    public static final int MSGBODY_SUBPACKAGE_DEFAULT_START_INDEX = 16 ;



    //    ============================================================================================

    /** 视频支持分辨率
      *   宽    高
      * 1920--1080
      * 1440--1080
      * 1280--720
      * 960--540
      * 800--600
      * 864--480
      * 800--480
      * 720--480
      * 640--480
      * 480--368
      * 480--320
      * 352--288
      * 320--240
      * 176--144
    */
    //    视频服务器参数
    public static String VIDEO_IP = "video.erayton.cn" ;
    public static int VIDEO_PORT = 7000;

    //  视频初始化时长 sec
    public static int VIDEO_INIT_TIME = 2 ;

    //  kpbs 进制 ？
    private static int SCALE = 1024 ;

    //  帧率
    public static int FRAME_RATE = 30 ;
    //  推流分辨率
//        public static int PUSHER_RESOLUTION_W = 700 ;
//        public static int PUSHER_RESOLUTION_H = 525 ;
    public static int PUSHER_RESOLUTION_W = 480 ;
    public static int PUSHER_RESOLUTION_H = 320 ;
    //  预览分辨率
    public static int PREVIEW_RESOLUTION_W = 1280 ;
    public static int PREVIEW_RESOLUTION_H = 720 ;
//    public static int PREVIEW_RESOLUTION_W = 1400 ;
//    public static int PREVIEW_RESOLUTION_H = 1050 ;

    //  视频推流码率      kpbs
    public static int VIDEO_PUSH_RATE = 400 * SCALE ;
    //  视频采集码率      kpbs
    public static int VIDEO_SAMPLING_RATE = 1200  * SCALE ;

    //  编码方式
    public static String VIDEO_ENCODING = VDEncoder.H264 ;
    //  预览
    public static boolean PREVIEW = true ;
//    public static boolean PREVIEW = false ;
//      摄像头 true 前置， false 后置
//    public static boolean CAMERA = true ;
    public static boolean CAMERA = false ;
    //  音频推流码率
    public static int VOICE_PUSH_RATE = 24 * SCALE ;
    //  音频采集码率
    public static int VOICE_SAMPLING_RATE = 64 * SCALE ;



//    ============================================================================================
    //  默认 authCode
    public static String USAUTHCODE = "0" ;

}
