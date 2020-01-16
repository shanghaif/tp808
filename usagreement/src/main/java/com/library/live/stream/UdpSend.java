package com.library.live.stream;

import com.library.common.UdpControlInterface;
import com.library.live.stream.socket.core.TCPClient;
import com.library.util.OtherUtil;
import com.library.util.SingleThreadExecutor;
import com.library.util.mLog;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.erayton.usagreement.sendModel.TerminalAVDataMsg;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * Created by android1 on 2017/9/25.
 */

public class UdpSend {
    public static final int PUBLISH_STATUS_START = 0;
    public static final int PUBLISH_STATUS_STOP = 1;
    private int PUBLISH_STATUS = PUBLISH_STATUS_STOP;

    private UdpControlInterface udpControl = null;

    private TCPManager tcpManager = new TCPManager() ;
    private String phone ;
    private String ip ;
    private int port ;
    //  逻辑通道号
    private int channelNum = 1 ;

    private DatagramSocket socket = null;
    private DatagramPacket packetsendPush = null;
    private int voiceNum = 0;
    private int videoNum = 0;
//    private final int sendUdplength = 480;//视频包长度固定480
    private final int sendUdplength = 950;//    视频包长度固定950 + 30
//    private ByteBuffer buffvideo = ByteBuffer.allocate(548);
    private ByteBuffer buffvideo = ByteBuffer.allocate(980);
    private ByteBuffer buffvoice = ByteBuffer.allocate(1024);
    private boolean ismysocket = false;//用于判断是否需要销毁socket
    private int voiceSendNum = 0;//控制语音包合并发送，5个包发送一次
    private byte weight;//图像比

    private SingleThreadExecutor singleThreadExecutor = null;

    private ArrayBlockingQueue<byte[]> sendQueue = new ArrayBlockingQueue<>(OtherUtil.QueueNum);

    public UdpSend(String ip, int port) {
        initSocket(ip, port);
    }
    public UdpSend(String phone, String ip, int port, int channelNum) {
        this.phone = phone ;
        this.channelNum = channelNum ;
        this.ip = ip ;
        this.port = port ;
        initSocket(ip, port);
    }

    private void initSocket(String ip, int port){
        try {
            socket = new DatagramSocket(port+1);
            socket.setSendBufferSize(1024 * 1024);
            ismysocket = true;
            tcpManager.connectSocket(ip, port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
        init(ip, port);
    }


    public UdpSend(DatagramSocket socket, String ip, int port) {
        this.socket = socket;
        ismysocket = false;
        init(ip, port);
    }

    private void init(String ip, int port) {
        try {
            packetsendPush = new DatagramPacket(new byte[10], 10, InetAddress.getByName("192.168.1.106"), 8765+1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        singleThreadExecutor = new SingleThreadExecutor();
    }

    public void startsend() {
        if (packetsendPush != null) {
            if (!tcpManager.isConnect()) {
                tcpManager.connectSocket(ip, port);
            }
            buffvoice.clear();
            voiceSendNum = 0;
            PUBLISH_STATUS = PUBLISH_STATUS_START;
            starsendThread();
        }
    }

    public void stopsend() {
        PUBLISH_STATUS = PUBLISH_STATUS_STOP;
    }

    public void destroy() {
        tcpManager.closeSocket();
        stopsend();
        if (ismysocket) {
            OtherUtil.close(socket);
        }
        if (singleThreadExecutor != null) {
            singleThreadExecutor.shutdownNow();
            singleThreadExecutor = null;
        }
    }

    public void closeTCPSocket(){
        if (tcpManager!= null){
            tcpManager.closeSocket();
        }
    }

    public void addVideo(byte[] video, int isIFrame) {
        if (PUBLISH_STATUS == PUBLISH_STATUS_START) {
            writeVideo(video, isIFrame);
        }
    }

    public void addVoice(byte[] voice) {
        if (PUBLISH_STATUS == PUBLISH_STATUS_START) {
            writeVoice(voice);
        }
    }

    public void setWeight(double weight) {
        this.weight = OtherUtil.setWeitht(weight);
    }

    public void setUdpControl(UdpControlInterface udpControl) {
        this.udpControl = udpControl;
    }

    public int getPublishStatus() {
        return PUBLISH_STATUS;
    }

    /**
     * 发送视频
     * @param isIFrame 是否为关键帧
     */
    private void writeVideo(byte[] video, int isIFrame) {
        LogUtils.d("phone:"+phone);
        //当前截取位置
        int nowPosition = 0;
        //  是否首次进入
        boolean isOne = true;
        //  记录时间值

        TerminalAVDataMsg.TerminalAVDataInfo terminalAVDataInfo ;
        TerminalAVDataMsg terminalAVDataMsg = new TerminalAVDataMsg() ;
        byte[] tmpVideo = new byte[sendUdplength] ;
        while ((video.length - nowPosition) > sendUdplength) {
            System.arraycopy(video, nowPosition, tmpVideo, 0, sendUdplength);
            if (isOne) {
                //  起始帧
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 1, tmpVideo) ;
            } else {
                //  中间帧
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 11, tmpVideo) ;
            }
            //  添加视频数据
            //  30 数据体  长度不超过 950 byte ,平台要求固定 950 长度
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  UPD发送
            addbytes(terminalAVDataMsg.packageDataBody2Byte());
            isOne = false;
            nowPosition += sendUdplength;
        }
        if ((video.length - nowPosition) > 0) {
            tmpVideo = new byte[video.length - nowPosition] ;
            System.arraycopy(video, nowPosition, tmpVideo, 0, video.length - nowPosition);
            //  添加视频头
            //  数据类型+分包处理
            if (isOne) {
                //  完整帧
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 0, tmpVideo) ;
            } else {
                //  结束帧
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 10, tmpVideo) ;
            }
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  UPD发送
            addbytes(terminalAVDataMsg.packageDataBody2Byte());
        }
    }

    /**
     * 发送音频
     */
    private void writeVoice(byte[] voice) {
        TerminalAVDataMsg.TerminalAVDataInfo terminalAVDataInfo ;
        TerminalAVDataMsg terminalAVDataMsg = new TerminalAVDataMsg() ;
        if (voiceSendNum == 0) {
//            //  添加udp头
//            buffvoice.put((byte) 0);//  音频TAG
//            buffvoice.putInt(voiceNum++);// 序号
//            //  添加音频头
//            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
//            buffvoice.putShort((short) voice.length);// 长度

            terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                    false, channelNum, 11, 0, voice) ;
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  添加音频数据
            buffvoice.put(terminalAVDataMsg.packageDataBody2Byte());// 数据
//            buffvoice.put(voice);// 数据

            voiceSendNum++;
        } else {
            //  添加音频头
//            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
//            buffvoice.putShort((short) voice.length);// 长度
            terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo(phone, videoNum++,
                    false, channelNum, 11, 0, voice) ;
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  添加音频数据
//            buffvoice.put(voice);// 数据
            buffvoice.put(terminalAVDataMsg.packageDataBody2Byte());// 数据
            voiceSendNum++;
        }

        if (voiceSendNum == 5) {
            voiceSendNum = 0;// 5帧一包，标志置0
            //  UPD发送
            addbytes(buffvoice);
            buffvoice.clear();
        }
    }

//    /**
//     * 发送音频
//     */
//    private void writeVoice(byte[] voice) {
//        if (voiceSendNum == 0) {
//            //  添加udp头
//            buffvoice.put((byte) 0);//  音频TAG
//            buffvoice.putInt(voiceNum++);// 序号
//            //  添加音频头
//            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
//            buffvoice.putShort((short) voice.length);// 长度
//            //  添加音频数据
//            buffvoice.put(voice);// 数据
//
//            voiceSendNum++;
//        } else {
//            //  添加音频头
//            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
//            buffvoice.putShort((short) voice.length);// 长度
//            //  添加音频数据
//            buffvoice.put(voice);// 数据
//            voiceSendNum++;
//        }
//
//        if (voiceSendNum == 5) {
//            voiceSendNum = 0;// 5帧一包，标志置0
//            //  UPD发送
////            addbytes(buffvoice);
//            buffvoice.clear();
//        }
//    }

    private synchronized void addbytes(ByteBuffer buff) {
        if (udpControl != null) {
            //如果自定义UPD发送
            OtherUtil.addQueue(sendQueue, udpControl.Control(buff.array(), 0, buff.position()));
        } else {
            OtherUtil.addQueue(sendQueue, Arrays.copyOfRange(buff.array(), 0, buff.position()));//  复制数组
        }
    }

    private synchronized void addbytes(byte[] bytes) {
        if (udpControl != null) {
            //如果自定义UPD发送
            OtherUtil.addQueue(sendQueue, udpControl.Control(bytes, 0, bytes.length));
        } else {
            OtherUtil.addQueue(sendQueue, Arrays.copyOfRange(bytes, 0, bytes.length));//  复制数组
        }
    }

    /**
     * 真正发送数据
     */
    private void starsendThread() {
        singleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                byte[] data;
                try {
                    while (PUBLISH_STATUS == PUBLISH_STATUS_START) {
                        data = sendQueue.take();
                        if (data != null) {
                            packetsendPush.setData(data);
//                            try {
                                tcpManager.send(data);
//                                SocketClientSender.send(data, false, false) ;
                                LogUtils.d("starsendThread:"+data.length);
//                                socket.send(packetsendPush);
//                            } catch (IOException e) {
//                                mLog.log("senderror", "发送失败");
//                                e.printStackTrace();
//                            }
                            Thread.sleep(10);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mLog.log("interrupt_Thread", "关闭发送线程");
            }
        });
    }

    public byte getVPXCC(int value){
        byte V = (byte)(value >> 6);
        byte P = (byte)(value >> 5 & 0x01);
        byte X = (byte)(value >> 4 & 0x01);
        byte CC = (byte)(value & 0xF);
        return (byte)((V << 6) | (byte)(P << 5) | (byte)(X << 4) | CC);
    }

    public byte getMPT(int value){
        byte M =  (byte)(value >> 7);
        byte PT = (byte)(value & 0x7f);
        return (byte)((M << 7) | PT);
    }

//    public byte get15th(int value){
//        byte M =  (byte)(value >> 7);
//        byte PT = (byte)(value & 0x7f);
//        return (byte)(((byte)DataType << 4) | (byte)SubpackageType);
//    }

    class TCPManager implements Runnable{
        TCPClient tcpClient = new TCPClient() ;
        private boolean isRunning = false ;
        Thread thread = null ;

        public TCPManager() {
            this.tcpClient.listener = listener;
        }

        public void connectSocket(String ip, int port){
            tcpClient.openAsyn(ip, port);
            thread = new Thread(this);
            thread.setName("tcpvideo thread");
        }

        public void send(byte[] bytes){
            LogUtils.d("onTcpSend:"+tcpClient.send(bytes));
        }

        public void closeSocket(){
            if (tcpClient != null){
                tcpClient.close();
                isRunning = false ;
            }
        }

        public boolean isConnect(){
            return tcpClient.isConnect();
        }

        TCPClient.TCPClientListener listener = new TCPClient.TCPClientListener() {
            @Override
            public void onTcpConnect(int result) {
                if (result == 0){
                    if (!isRunning){
                        thread.start();
                        isRunning = true ;
                    }
                }
                LogUtils.d("onTcpConnect:"+result);
            }

            @Override
            public void onTcpDisConnect() {
                LogUtils.e("onTcpDisConnect:");
            }

            @Override
            public void onTcpSend(byte[] data, boolean result) {
                LogUtils.d("onTcpSend:"+result);
            }

            @Override
            public void onTcpReceive(byte[] receiveBytes) {
                LogUtils.d("onTcpReceive:"+receiveBytes.length);
            }
        } ;

        @Override
        public void run() {
//            while (isRunning){
//                try {
//                    Thread.sleep(1000);
//                    send("111".getBytes());
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            isRunning = false ;
            LogUtils.d("other thread");
        }
    }


    //  ByteBuffer to byte[]
    public byte[] decodeValue(ByteBuffer bytes) {
        int len = bytes.limit() - bytes.position();
        byte[] bytes1 = new byte[len];
        bytes.get(bytes1);
        return bytes1;
    }
}
