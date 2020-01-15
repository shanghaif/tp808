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

import cn.com.erayton.usagreement.model.TerminalAVDataMsg;
import cn.com.erayton.usagreement.utils.BCD8421Operator;
import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.HexStringUtils;
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

    private DatagramSocket socket = null;
    private DatagramPacket packetsendPush = null;
    private int voiceNum = 0;
    private int videoNum = 0;
//    private final int sendUdplength = 480;//视频包长度固定480
    private final int sendUdplength = 950;//视频包长度固定950 + 30
//    private ByteBuffer buffvideo = ByteBuffer.allocate(548);
    private ByteBuffer buffvideo = ByteBuffer.allocate(980);
    private ByteBuffer buffvoice = ByteBuffer.allocate(1024);
    private boolean ismysocket = false;//用于判断是否需要销毁socket
    private int voiceSendNum = 0;//控制语音包合并发送，5个包发送一次
    private byte weight;//图像比

    private SingleThreadExecutor singleThreadExecutor = null;

    private ArrayBlockingQueue<byte[]> sendQueue = new ArrayBlockingQueue<>(OtherUtil.QueueNum);

    public UdpSend(String ip, int port) {
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
//            packetsendPush = new DatagramPacket(new byte[10], 10, InetAddress.getByName(ip), port);
            packetsendPush = new DatagramPacket(new byte[10], 10, InetAddress.getByName("192.168.1.106"), 8765+1);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        singleThreadExecutor = new SingleThreadExecutor();
    }

    public void startsend() {
        if (packetsendPush != null) {
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

    public void addVideo(byte[] video, String isIFrame) {
        if (PUBLISH_STATUS == PUBLISH_STATUS_START) {
//            writeVideo(video);
            writeVideo(video, isIFrame);
        }
    }
    public void addVideo(byte[] video, int isIFrame) {
        LogUtils.e("-------------------------------------------------------"+isIFrame);
        if (PUBLISH_STATUS == PUBLISH_STATUS_START) {
//            writeVideo(video);
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
    private long lastIFrameTime = 0 ;
    private long betIFrameTime = 0 ;
    private long lastFrameTime = 0 ;

    private static final byte[] FRAMETIMES = {0, 0, 0, 0};//帧时间
    private static final byte V_P_X_CC = (byte) Integer.parseInt("10000001",2);
    private static final byte M_PT_VIDEO = Byte.parseByte("01100010",2);    //  视频
    private static final byte M_PT_AUDIO = Byte.parseByte("00001000",2);    //  音频
    private static final byte[] LOGO = {0x30, 0x31, 0x63, 0x64};

    /**
     * 发送视频
     * @param isIFrame 是否为关键帧
     */
    private void writeVideo(byte[] video, int isIFrame) {
        BitOperator bitOperator = BitOperator.getInstance() ;

//        if (isIFrame.equals("0000"))
//            LogUtils.e("isFrame -------------------------------------------------------");
        //当前截取位置
        int nowPosition = 0;
        //  是否首次进入
        boolean isOne = true;
        //  记录时间值
        long time_vd_vaule = OtherUtil.getTime();

        TerminalAVDataMsg.TerminalAVDataInfo terminalAVDataInfo ;
        TerminalAVDataMsg terminalAVDataMsg = new TerminalAVDataMsg() ;
        byte[] tmpVideo = new byte[sendUdplength] ;
        while ((video.length - nowPosition) > sendUdplength) {
            System.arraycopy(video, nowPosition, tmpVideo, 0, sendUdplength);
            if (isOne) {
                //  起始帧
//                buffvideo.put(Byte.parseByte(isIFrame + "0001", 2));
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo("23803641388", videoNum++,
                        true, 1, isIFrame, 1, tmpVideo) ;
                LogUtils.e("起始帧 -------------------------------------------------------"+isIFrame);
            } else {
                //  中间帧
//                buffvideo.put(Byte.parseByte(isIFrame + "0011", 2));
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo("23803641388", videoNum++,
                        true, 1, isIFrame, 1, tmpVideo) ;
                LogUtils.e("中间帧 -------------------------------------------------------"+isIFrame);
            }
            //  添加视频数据
            //  30 数据体  长度不超过 950 byte ,平台要求固定 950 长度
//            buffvideo.put(video, nowPosition, sendUdplength);
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  UPD发送
            LogUtils.e("length:"+terminalAVDataMsg.packageDataBody2Byte().length+"\n "
                    + HexStringUtils.toHexString(terminalAVDataMsg.packageDataBody2Byte()));
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
//                buffvideo.put(Byte.parseByte(isIFrame + "0000", 2));
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo("23803641388", videoNum++,
                        true, 1, isIFrame, 0, tmpVideo) ;
                LogUtils.e("完整帧 -------------------------------------------------------"+isIFrame+",tmp length:"+tmpVideo.length);
            } else {
                //  结束帧
//                buffvideo.put(Byte.parseByte(isIFrame + "0010", 2));
                terminalAVDataInfo = new TerminalAVDataMsg.TerminalAVDataInfo("23803641388", videoNum++,
                        true, 1, isIFrame, 10, tmpVideo) ;
                LogUtils.e("结束帧 -------------------------------------------------------"+isIFrame+",tmp length:"+tmpVideo.length);
            }
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  UPD发送
            LogUtils.e("(video.length - nowPosition) ---------------------length:"+terminalAVDataMsg.packageDataBody2Byte().length+"\n "
                    + HexStringUtils.toHexString(terminalAVDataMsg.packageDataBody2Byte()));
            addbytes(terminalAVDataMsg.packageDataBody2Byte());
        }
    }


    /**
     * 发送视频
     * @param isIFrame 是否为关键帧
     */
    private void writeVideo(byte[] video, String isIFrame) {
        BitOperator bitOperator = BitOperator.getInstance() ;

//        if (isIFrame.equals("0000"))
//            LogUtils.e("isFrame -------------------------------------------------------");
        //当前截取位置
        int nowPosition = 0;
        //是否首次进入
        boolean isOne = true;
        //记录时间值
        long time_vd_vaule = OtherUtil.getTime();

        while ((video.length - nowPosition) > sendUdplength) {
            buffvideo.clear();
            //  0 帧头标识
            buffvideo.put(LOGO);
            //  4 V / P / X / CC    bit
            buffvideo.put(V_P_X_CC);
            //  5 M / PT    bit
            buffvideo.put(M_PT_VIDEO);
            //  6 包序号
            buffvideo.put(bitOperator.integerTo2Bytes(videoNum++));
            //  8 SIM 卡号
            buffvideo.put(BCD8421Operator.getInstance().string2Bcd("23803641388"));
//          //  14 逻辑通道信号
            buffvideo.put((byte) 1);
            //  添加视频头
            //  数据类型+分包处理
            if (isOne) {
                //  起始帧
                buffvideo.put(Byte.parseByte(isIFrame + "0001", 2));
                LogUtils.e("起始帧 -------------------------------------------------------"+isIFrame);
            } else {
                //  中间帧
                buffvideo.put(Byte.parseByte(isIFrame + "0011", 2));
                LogUtils.e("中间帧 -------------------------------------------------------"+isIFrame);
            }
            //  16 时间戳
            buffvideo.put(bitOperator.toDDbyte(time_vd_vaule, 8));
            //  26 上一帧间隔
            buffvideo.put(FRAMETIMES);
            //  28 数据体长度
            buffvideo.put(bitOperator.toDDbyte(sendUdplength, 2));
            //  添加视频数据
            //  30 数据体  长度不超过 950 byte ,平台要求固定 950 长度
            buffvideo.put(video, nowPosition, sendUdplength);
            //  UPD发送
            addbytes(buffvideo);
            isOne = false;
            nowPosition += sendUdplength;

            LogUtils.e("length:"+buffvideo.array().length+"\n "
                    + HexStringUtils.toHexString(buffvideo.array()));
        }
        if ((video.length - nowPosition) > 0) {
            buffvideo.clear();
            //    添加udp头
            //  0 帧头标识
            buffvideo.put(LOGO);
            //  4 V / P / X / CC    bit
            buffvideo.put(V_P_X_CC);
            //  5 M / PT    bit
            buffvideo.put(M_PT_VIDEO);
            //  6 包序号
            buffvideo.put(bitOperator.integerTo2Bytes(videoNum++));
            //  8 SIM 卡号
            buffvideo.put(BCD8421Operator.getInstance().string2Bcd("23803641388"));
            //  14 逻辑通道信号
            buffvideo.put((byte) 1);
            //  添加视频头
            //  数据类型+分包处理
            if (isOne) {
                //  完整帧
                buffvideo.put(Byte.parseByte(isIFrame + "0000", 2));
                LogUtils.e("完整帧 -------------------------------------------------------"+isIFrame);
            } else {
                //  结束帧
                buffvideo.put(Byte.parseByte(isIFrame + "0010", 2));
                LogUtils.e("结束帧 -------------------------------------------------------"+isIFrame);
            }
            buffvideo.put(bitOperator.toDDbyte(time_vd_vaule, 8));
            //  26 上一帧间隔
            buffvideo.put(FRAMETIMES);
            //  28 数据体长度
            buffvideo.put(bitOperator.toDDbyte(video.length - nowPosition, 2));
            //  添加视频数据
            //  30 数据体  长度不超过 950 byte
            buffvideo.put(video, nowPosition, video.length - nowPosition);
            LogUtils.e("(video.length - nowPosition) ---------------------length:"+buffvideo.array().length+"\n "
                    + HexStringUtils.toHexString(buffvideo.array()));
            //  UPD发送
            addbytes(buffvideo);
        }
    }

//    /**
//     * 发送视频
//     */
//    private void writeVideo(byte[] video) {
//        //当前截取位置
//        int nowPosition = 0;
//        //是否首次进入
//        boolean isOne = true;
//        //记录时间值
//        int time_vd_vaule = OtherUtil.getTime();
//
//        while ((video.length - nowPosition) > sendUdplength) {
//            buffvideo.clear();
//            //添加udp头
//            buffvideo.put((byte) 1);//视频TAG
//            buffvideo.putInt(videoNum++);//序号
//            //添加视频头
//            if (isOne) {
//                buffvideo.put((byte) 0);//起始帧
//            } else {
//                buffvideo.put((byte) 1);//中间帧
//            }
//            buffvideo.put(weight);//图像比
//            buffvideo.putInt(time_vd_vaule);//时戳
//            buffvideo.putShort((short) sendUdplength);//长度
//            //添加视频数据
//            buffvideo.put(video, nowPosition, sendUdplength);
//            //UPD发送
//            addbytes(buffvideo);
//            isOne = false;
//            nowPosition += sendUdplength;
//        }
//        if ((video.length - nowPosition) > 0) {
//            buffvideo.clear();
//            //添加udp头
//            buffvideo.put((byte) 1);//视频TAG
//            buffvideo.putInt(videoNum++);//序号
//            //添加视频头
//            if (isOne) {
//                buffvideo.put((byte) 3);//完整帧
//            } else {
//                buffvideo.put((byte) 2);//结束帧
//            }
//            buffvideo.put(weight);//图像比
//            buffvideo.putInt(time_vd_vaule);//时戳
//            buffvideo.putShort((short) (video.length - nowPosition));//长度
//            //添加视频数据
//            buffvideo.put(video, nowPosition, video.length - nowPosition);
//            //UPD发送
//            addbytes(buffvideo);
//        }
//    }

    /**
     * 发送视频
     */
    private void writeVideo(byte[] video) {
        //  当前截取位置
        int nowPosition = 0;
        //  是否首次进入
        boolean isOne = true;
        //  记录时间值
        int time_vd_vaule = OtherUtil.getTime(1);

        while ((video.length - nowPosition) > sendUdplength) {
            buffvideo.clear();
            //  添加udp头
            buffvideo.put((byte) 1);//  视频TAG
            buffvideo.putInt(videoNum++);// 序号
            //  添加视频头
            if (isOne) {
                buffvideo.put((byte) 0);//  起始帧
            } else {
                buffvideo.put((byte) 1);//  中间帧
            }
            buffvideo.put(weight);//    图像比
            buffvideo.putInt(time_vd_vaule);//  时戳
            buffvideo.putShort((short) sendUdplength);//    长度
            //  添加视频数据
            buffvideo.put(video, nowPosition, sendUdplength);
            //  UPD发送
            addbytes(buffvideo);
            isOne = false;
            nowPosition += sendUdplength;
        }
        if ((video.length - nowPosition) > 0) {
            buffvideo.clear();
            //  添加udp头
            buffvideo.put((byte) 1);//  视频TAG
            buffvideo.putInt(videoNum++);// 序号
            //  添加视频头
            if (isOne) {
                buffvideo.put((byte) 3);//  完整帧
            } else {
                buffvideo.put((byte) 2);//  结束帧
            }
            buffvideo.put(weight);//    图像比
            buffvideo.putInt(time_vd_vaule);//  时戳
            buffvideo.putShort((short) (video.length - nowPosition));// 长度
            //  添加视频数据
            buffvideo.put(video, nowPosition, video.length - nowPosition);
            //  UPD发送
            addbytes(buffvideo);
        }
    }

    /**
     * 发送音频
     */
    private void writeVoice(byte[] voice) {
        if (voiceSendNum == 0) {
            //  添加udp头
            buffvoice.put((byte) 0);//  音频TAG
            buffvoice.putInt(voiceNum++);// 序号
            //  添加音频头
            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
            buffvoice.putShort((short) voice.length);// 长度
            //  添加音频数据
            buffvoice.put(voice);// 数据

            voiceSendNum++;
        } else {
            //  添加音频头
            buffvoice.putInt(OtherUtil.getTime(1));//   时戳
            buffvoice.putShort((short) voice.length);// 长度
            //  添加音频数据
            buffvoice.put(voice);// 数据
            voiceSendNum++;
        }

        if (voiceSendNum == 5) {
            voiceSendNum = 0;// 5帧一包，标志置0
            //  UPD发送
//            addbytes(buffvoice);
            buffvoice.clear();
        }
    }

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
            LogUtils.e("ip:"+ip+",port:"+port);
            thread = new Thread(this);
            thread.setName("new tcp thread");
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
