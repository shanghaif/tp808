package cn.com.erayton.usagreement.test;

import java.util.List;

import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.USGate;
import cn.com.erayton.usagreement.utils.Utils;

public class APITest {
    static String msid= "23803641400" ;
    static USGate mUSGate;
    public static void main(String[] args){
//        BitOperator bitOperator = BitOperator.getInstance();
//        int gpsS = 0x01 ;
//        int gpsString = Integer.parseInt(String.valueOf(gpsS), 2) ;
//        byte[] gpsState = bitOperator.integerTo4Bytes(gpsString);
//        System.out.println("gpsS:"+gpsS+
//                "\nString.valueOf(gpsS):"+String.valueOf(gpsS)+
//                "\n gpsString:"+gpsString+
//                "\n gpsState:"+gpsState);


//        new USThread().start();
//        mUSGate = new USGate();
//        mUSGate.setCommand(msid);
//        mUSGate.setIPCallback(new USGate.IPCallback() {
//            @Override
//            public void IpCallback(List<String[]> ip) {
////                for (String[] i :ip){
//////                    USMain usMain = new USMain() ;
//////                    usMain.connection("222.222.19.34", 7808, 7808);
//////                    Utils.print("IpCallback, ip:"+i[1]+",tport:"+i[2]+",uport:"+i[3]);
////                }
//                try {
//                    NettyConnectionManager.getInstance().connect();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            @Override
//            public void IpCallBackError() {
//                Utils.print("IP ,NONE");
////                        808 fail
//            }
//        });
//        try {
//            mUSGate.IpInt();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }


    public static class USThread extends Thread {
        @Override
        public void run() {
            super.run();
            try {
                mUSGate = new USGate();
                mUSGate.setCommand(msid);
                mUSGate.setIPCallback(new USGate.IPCallback() {
                    @Override
                    public void IpCallback(int count, List<String[]> ip) {
                        Utils.print("IpCallback, ip0:"+ip.get(0)[1]+",tport:"+ip.get(0)[2]+",uport:"+ip.get(0)[3]);
                        for (String[] i :ip){
                            Utils.print("IpCallback, ip:"+i[1]+",tport:"+i[2]+",uport:"+i[3]);
                        }
                    }
                    @Override
                    public void IpCallBackError() {
//                        808 fail
                    }
                });
                mUSGate.IpInt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
