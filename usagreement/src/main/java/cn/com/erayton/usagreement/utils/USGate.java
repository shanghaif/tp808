package cn.com.erayton.usagreement.utils;

import com.speedtalk.protocol.utils.MessageUtils;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/5/15.
 */

public class USGate
{
    private final static String TAG = "USGate" ;
    private boolean isInit=false;
    private String Command;
    private IPCallback callback;
    private Socket client;
    public USGate() {
    }

    public String getCommand() {
//        Utils.print("Command"+Command) ;
        return Command;
    }

    public void setCommand(String msid) {
        Command ="B:"+msid;
    }

    private Socket conn()throws Exception
    {
        client = new Socket("poc.rayton.com.cn",29992);
        // client = new Socket(InetAddress.getByName("kirisun.stpoc.cn"),9992);
        return client;
    }
    private void send(String req)throws Exception
    {
        if (isInit){
            return;
        }
        String reqStr = new String(MessageUtils.encryptAndDecrypt(req.getBytes()));
        reqStr += "\n";
        client.getOutputStream().write(reqStr.getBytes());
//		System.out.println("send:" + reqStr);
    }
    private void receive()throws Exception
    {
        byte[] resp = new byte[256];
        int read = client.getInputStream().read(resp);
        if (read<0){
            return;
        }
        byte[] temp = new byte[read - 1];
        System.arraycopy(resp,0,temp,0,temp.length);
        String respStr = new String(MessageUtils.encryptAndDecrypt(temp));
        String [] temp1;
        temp1 = respStr.split(",");
        for (String i: temp1){
            Utils.print("i============"+i);
        }
        List<String[]> ipList = new ArrayList<>() ;
        if (callback!=null ){
            if (!temp1[1].equals("0")) {                 //  成功判断
                for (int i = 1; i < temp1.length; i++) {
//                    temp1[i].split(":") ;                    //  [1,222.222.19.34,7808,7808]
                    ipList.add(temp1[i].split(":"));

                }
                isInit = true;
                if (temp1[0] .equals("0")){
                    callback.IpCallBackError();
                }else
                callback.IpCallback(Integer.parseInt(temp1[0]), ipList);
            }else {
                isInit = false;
                callback.IpCallBackError();
            }
        }
    }

    private void close() throws IOException
    {
        if (client != null)
        {
            client.close();
        }
    }
    public  void IpInt() throws Exception {
       conn();
       send(getCommand());
       receive();
       close();
//        Utils.print("client.isClosed():"+client.isClosed()+
//                "\n client.isConnected():"+client.isConnected()+
//                "\n client.isBound():"+client.isBound()+
//                "\n client.isInputShutdown():"+client.isInputShutdown()+
//                "\n client.isOutputShutdown():"+client.isOutputShutdown());
    }


    public void setIPCallback(IPCallback buttonCallback) {
        callback = buttonCallback;
    }
    public interface IPCallback {
        void IpCallback(int ipCount, List<String[]> ipList);
        void IpCallBackError();
    }
}