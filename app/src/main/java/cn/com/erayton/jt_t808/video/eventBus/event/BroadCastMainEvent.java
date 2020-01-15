package cn.com.erayton.jt_t808.video.eventBus.event;


import cn.com.erayton.jt_t808.video.eventBus.EventBusEvent;

public class BroadCastMainEvent extends EventBusEvent {
    public BroadCastMainEvent(int code, Object data) {
        super(code, data);
    }
}
