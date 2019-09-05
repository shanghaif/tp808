package cn.com.erayton.usagreement.data;

public class ResponseReason {
    private static ResponseReason instance ;
    public static ResponseReason getInstance(){
        if (instance == null){
            instance = new ResponseReason() ;
        }
        return instance ;
    }

    private ResponseReason() {
    }

    //    0：成功/确认；1：失败；2：消息有误；3：不支持；4：报警处理确认；
    public final String[] GENERALRESULT = new String[]{SUCC, FAILURE, MESSAGE_WRONG, NONSUPPORT, ALARM_PROCESSING} ;
//    0：成功；1：车辆已被注册；2：数据库中无该车辆；3：终端已被注册；4：数据库中无该终端

    private static final String SUCC = "成功" ;
    private static final String FAILURE = "失败" ;
    private static final String MESSAGE_WRONG = "消息有误" ;
    private static final String NONSUPPORT = "不支持" ;
    private static final String ALARM_PROCESSING = "报警处理确认" ;

//    0：成功；1：车辆已被注册；2：数据库中无该车辆；3：终端已被注册；4：数据库中无该终端
    public final String[] REGISTERRESULT = new String[]{SUCC, VEHICLE_REGISTERED, NONVEHICLE_INDATABASE, TERMINAL_REGISTERED, NOTERMINAL_INDATABASE} ;
    private static final String VEHICLE_REGISTERED = "车辆已被注册" ;
    private static final String NONVEHICLE_INDATABASE = "数据库中无该车辆" ;
    private static final String TERMINAL_REGISTERED = "终端已被注册" ;
    private static final String NOTERMINAL_INDATABASE = "数据库中无该终端" ;
}
