package cn.com.erayton.jt_t808.video.eventBus;

import org.greenrobot.eventbus.EventBus;

/**
 * EventBusUtils工具类
 * Created by jeam on 2018/4/28.
 */

public class EventBusUtils {
    public static void register(Object subscriber) {
        if (!EventBus.getDefault().isRegistered(subscriber)) {
            EventBus.getDefault().register(subscriber);
        }
    }

    public static void unregister(Object subscriber) {
        if(EventBus.getDefault().isRegistered(subscriber)){
            EventBus.getDefault().unregister(subscriber);
        }
    }

    public static void sendEvent(EventBusEvent event) {
        EventBus.getDefault().post(event);
    }

    public static void sendStickyEvent(EventBusEvent event) {
        EventBus.getDefault().postSticky(event);
    }

    /**
     * 通过code码区分事件类型
     */
    public static final class EventCode {
        public static final int OPEN_VIDEO = 1;
        public static final int CLOSE_VIDEO = 2;
        public static final int QUERY_RESOURCE = 3;
        public static final int FILEUPLOAD_REQ = 4;

        // other more
    }
}
