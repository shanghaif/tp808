package net.rayton.phonestatelib;

import android.content.Context;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneStateListener extends PhoneStateListener {
    private PhoneListener listener ;
    private static TelephonyManager telephonyManager ;
    private static MyPhoneStateListener instance;

     public static MyPhoneStateListener getInstance() {
         synchronized (MyPhoneStateListener.class) {
             if (null == instance) {
                 instance = new MyPhoneStateListener();
             }
         }
         return instance;
     }


    public void init(Context context){
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(this, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void setListener(PhoneListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCallStateChanged(int state, String phoneNumber) {
//        super.onCallStateChanged(state, phoneNumber);
        System.out.println("phone state==>"+state);
        //具体判断下电话得状态
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:  //  结束
                //System.out.println("停止");
                //  打开声音
//                javaCallVs.runGoOpenVoice();
                listener.onHub();
                break;
            case TelephonyManager.CALL_STATE_RINGING:   //  电话响铃状态
                //System.out.println("准备");
                //  降低音量
//                javaCallVs.runGoCloseVoice();
                listener.onRing();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:   //  接听状态
                listener.onAnswer();
                //System.out.println("开始");
                //  关闭声音
//                javaCallVs.runGoCloseVoice();
                break;
            default:
//                javaCallVs.runGoCloseVoice();
                break;
        }
    }

    interface PhoneListener{
        //  响铃
        void onRing() ;
        //  接听
        void onAnswer() ;
        //  挂断
        void onHub() ;
    }
}
