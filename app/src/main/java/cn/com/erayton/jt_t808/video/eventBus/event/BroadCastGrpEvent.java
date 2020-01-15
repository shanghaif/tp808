package cn.com.erayton.jt_t808.video.eventBus.event;


import cn.com.erayton.jt_t808.video.eventBus.EventBusEvent;

public class BroadCastGrpEvent extends EventBusEvent {
    public BroadCastGrpEvent(int code, Object data) {
        super(code, data);
    }
}
