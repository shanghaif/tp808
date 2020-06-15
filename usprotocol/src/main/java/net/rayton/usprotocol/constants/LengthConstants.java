package net.rayton.usprotocol.constants;

public class LengthConstants {


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




}
