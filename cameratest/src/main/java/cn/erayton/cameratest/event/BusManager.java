package cn.erayton.cameratest.event;


import org.greenrobot.eventbus.EventBus;

public class BusManager {

    private static IEvents events = new Events();

    public static void register(Object arg0) {
        EventBus.getDefault().register(arg0);
    }

    public static void unregister(Object arg0) {
        EventBus.getDefault().unregister(arg0);
    }

    public static boolean isRegistered(Object subscriber) {
        return EventBus.getDefault().isRegistered(subscriber);
    }

    public static IEvents getEvents() {
        return events;
    }
}