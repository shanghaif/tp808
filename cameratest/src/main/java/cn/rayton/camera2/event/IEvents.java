package cn.rayton.camera2.event;

import cn.rayton.camera2.event.entity.RecordEvent;
/**
 * author:android
 * date:2017/6/2.
 */

public interface IEvents {

    /**
     * 录制事件
     * */
    void postRecordStatus(RecordEvent event) ;

//    /**
//     * 登陆事件
//     * */
//    void postLoginStatus(LoginEvent event) ;
//
//    /**
//     * 服务器连接事件
//     * */
//    void postServiceConnectStatus(ServiceConnectEvent event) ;
//
//    /**
//     * 短信控制视频录制事件
//     * */
//    void postSMSControlStatus(SMSControlEvent event) ;
//
//    /**
//     * 协议控制状态事件
//     * */
//    void postUSStatus(USStatusEvent event) ;

}
