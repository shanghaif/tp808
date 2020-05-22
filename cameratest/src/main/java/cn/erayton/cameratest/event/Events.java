package cn.erayton.cameratest.event;


import org.greenrobot.eventbus.EventBus;

import cn.erayton.cameratest.event.entity.RecordEvent;

/**
 * author:zjj
 * date:2017/6/2.
 */

public class Events implements IEvents {
    @Override
    public void postRecordStatus(RecordEvent event) {
        EventBus.getDefault().post(event);
    }


//    @Override
//    public void postLoginStatus(LoginEvent event) {
//        EventBus.getDefault().post(event);
//    }
//
//    @Override
//    public void postServiceConnectStatus(ServiceConnectEvent event) {
//        EventBus.getDefault().post(event);
//    }
//
//    @Override
//    public void postSMSControlStatus(SMSControlEvent event) {
//        EventBus.getDefault().post(event);
//    }
//
//    @Override
//    public void postUSStatus(USStatusEvent event) {
//        EventBus.getDefault().post(event);
//    }
}
