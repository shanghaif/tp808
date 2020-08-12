package com.library.live.stream;

import com.library.live.stream.socket.core.TCPClient;
import com.library.util.OtherUtil;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;

import cn.com.erayton.usagreement.model.encode.TerminalAVDataMsg;
import cn.com.erayton.usagreement.model.model.TerminalAVDataInfo;
import cn.com.erayton.usagreement.utils.LogUtils;

/**
 * Created by android1 on 2017/9/25.
 */

public class TcpSend implements Runnable {

    private TCPClient tcpClient = new TCPClient();
    private boolean isRunning = false;
    private boolean connectSucc = false;
    Thread thread = null;
    public static final int PUBLISH_STATUS_START = 0;
    public static final int PUBLISH_STATUS_STOP = 1;
    private int PUBLISH_STATUS = PUBLISH_STATUS_STOP;

    private String phone;
    private String ip;
    private int port;
    //  逻辑通道号
    private int channelNum;

    private int voiceNum = 0;
    private int videoNum = 0;
    private final int sendUdplength = 950;//    视频包长度固定950 + 30
    private ByteBuffer buffvoice = ByteBuffer.allocate(1024);
    private int voiceSendNum = 0;//控制语音包合并发送，5个包发送一次
    private byte weight;//图像比


    private ArrayBlockingQueue<byte[]> sendQueue = new ArrayBlockingQueue<>(OtherUtil.QueueNum);

    public TcpSend(String phone, String ip, int port, int channelNum) {
        this.phone = phone;
        this.channelNum = channelNum;
        this.ip = ip;
        this.port = port;
        if (port == 0) {
            return;
        }
        initSocket(ip, port);
    }

    public TcpSend(String ip, int port) {
        initSocket(ip, port);
    }

    public void initSocket(String ip, int port) {
        this.tcpClient.listener = listener;
        tcpClient.openAsyn(ip, port);
        thread = new Thread(this);
        thread.setName("tcp video send thread");
    }


    public void startsend() {
        LogUtils.d("startsend ----------------------------------------");
        if (!isConnect()) {
            initSocket(ip, port);
            buffvoice.clear();
            voiceSendNum = 0;
            PUBLISH_STATUS = PUBLISH_STATUS_START;
            LogUtils.d("PUBLISH_STATUS_START ----------------------------------------");
//            starsendThread();
            if (!isRunning) {
                thread.start();
                isRunning = true;
            }
        } else {
            buffvoice.clear();
            voiceSendNum = 0;
            PUBLISH_STATUS = PUBLISH_STATUS_START;
            LogUtils.d("PUBLISH_STATUS_START ----------------------------------------");
//            starsendThread();
            if (!isRunning) {
                thread.start();
                isRunning = true;
            }
        }
    }

    public void stopsend() {
        PUBLISH_STATUS = PUBLISH_STATUS_STOP;
    }

    public void destroy() {
        stopsend();
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
        closeTCPSocket();
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


    public int getPublishStatus() {
        return PUBLISH_STATUS;
    }

    private TerminalAVDataInfo terminalAVDataInfo;
    private TerminalAVDataMsg terminalAVDataMsg = new TerminalAVDataMsg();

    /**
     * 发送视频
     *
     * @param isIFrame 是否为关键帧
     */
    public void writeVideo(byte[] video, int isIFrame) {
        LogUtils.d("phone:" + phone);
        //当前截取位置
        int nowPosition = 0;
        //  是否首次进入
        boolean isOne = true;
        //  记录时间值

        byte[] tmpVideo = new byte[sendUdplength];
        while ((video.length - nowPosition) > sendUdplength) {
            System.arraycopy(video, nowPosition, tmpVideo, 0, sendUdplength);
            if (isOne) {
                //  起始帧
                terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 1, tmpVideo);
            } else {
                //  中间帧
                terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 11, tmpVideo);
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
            tmpVideo = new byte[video.length - nowPosition];
            System.arraycopy(video, nowPosition, tmpVideo, 0, video.length - nowPosition);
            //  添加视频头
            //  数据类型+分包处理
            if (isOne) {
                //  完整帧
                terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 0, tmpVideo);
            } else {
                //  结束帧
                terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
                        true, channelNum, isIFrame, 10, tmpVideo);
            }
            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
            //  UPD发送
            addbytes(terminalAVDataMsg.packageDataBody2Byte());
        }
    }


    private TerminalAVDataInfo terminalACDataInfo;
    private TerminalAVDataMsg terminalACDataMsg = new TerminalAVDataMsg();
    /**
     * 发送音频
     */
    public void writeVoice(byte[] voice) {

        LogUtils.d("cjhvc","writeVoice:"+voice.length);
        buffvoice.put(voice) ;
        voiceSendNum ++ ;

        if (voiceSendNum == 5) {
            voiceSendNum = 0 ;
            terminalACDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
                    false, channelNum, 11, 0, Arrays.copyOfRange(buffvoice.array(), 0, buffvoice.position()));


            LogUtils.d("cjhvc","writeVoice, position1:"+buffvoice.position());
            buffvoice.clear();
//            buffvoice.put(terminalACDataMsg.packageDataBody2Byte());    // 数据
            terminalACDataMsg.setTerminalAVDataInfo(terminalACDataInfo) ;
            //  UPD发送
            addbytes(terminalACDataMsg.packageDataBody2Byte());
        }

//        LogUtils.d("cjhvc","writeVoice:"+voice.length);
//        TerminalAVDataInfo terminalAVDataInfo;
//        TerminalAVDataMsg terminalAVDataMsg = new TerminalAVDataMsg();
//        if (voiceSendNum == 0) {
////            //  添加udp头
////            terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
////                    false, channelNum, 11, 0, voice) ;
//            terminalAVDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                    false, channelNum, 11, 0, voice);
//            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
//            //  添加音频数据
//            buffvoice.put(terminalAVDataMsg.packageDataBody2Byte());// 数据
//            voiceSendNum++;
//        } else {
//            //  添加音频头
////            terminalAVDataInfo = new TerminalAVDataInfo(phone, videoNum++,
////                    false, channelNum, 11, 0, voice) ;
//            terminalAVDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                    false, channelNum, 11, 0, voice);
//            terminalAVDataMsg.setTerminalAVDataInfo(terminalAVDataInfo);
//            //  添加音频数据
//            buffvoice.put(terminalAVDataMsg.packageDataBody2Byte());// 数据
//            voiceSendNum++;
//        }
//
//        if (voiceSendNum == 5) {
////        if (voiceSendNum == 12) {
//            voiceSendNum = 0;// 5帧一包，标志置0
//            //  UPD发送
//            addbytes(buffvoice);
//            buffvoice.clear();
//        }


//        LogUtils.d("cjhvc","writeVoice:"+voice.length);
//        //  当前截取位置
//        int nowPosition = 0;
//        //  是否首次进入
//        boolean isOne = true;
//        //  记录时间值
//
//        byte[] tmpVoice = new byte[sendUdplength];
//        while ((voice.length - nowPosition) > sendUdplength) {
//            System.arraycopy(voice, nowPosition, tmpVoice, 0, sendUdplength);
//            if (isOne) {
//                //  分包第一个包
//                terminalACDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                        false, channelNum, 11, 1, voice);
//            }else {
//
//                terminalACDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                        false, channelNum, 11, 11, voice);
//            }
//            //  添加视频数据
//            //  30 数据体  长度不超过 950 byte ,平台要求固定 950 长度
//            terminalACDataMsg.setTerminalAVDataInfo(terminalACDataInfo);
//            //  UPD发送
//            addbytes(terminalACDataMsg.packageDataBody2Byte());
//            isOne = false;
//            nowPosition += sendUdplength;
//        }
//        if ((voice.length - nowPosition) > 0) {
//            if (isOne){
//                //  完整帧
//                terminalACDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                        false, channelNum, 11, 0, tmpVoice);
//            } else {
//                //  结束帧
//                terminalACDataInfo = new TerminalAVDataInfo(phone, voiceNum++,
//                        false, channelNum, 11, 10, tmpVoice);
//            }
//            terminalACDataMsg.setTerminalAVDataInfo(terminalACDataInfo);
//            //  UPD发送
//            addbytes(terminalACDataMsg.packageDataBody2Byte());
//        }
    }

    private synchronized void addbytes(ByteBuffer buff) {
        OtherUtil.addQueue(sendQueue, Arrays.copyOfRange(buff.array(), 0, buff.position()));//  复制数组
    }

    private synchronized void addbytes(byte[] bytes) {
        OtherUtil.addQueue(sendQueue, Arrays.copyOfRange(bytes, 0, bytes.length));//  复制数组
    }


    public void send(byte[] bytes) {
        tcpClient.send(bytes);
    }

    public void closeTCPSocket() {
        if (tcpClient != null) {
            tcpClient.close();
            isRunning = false;
        }
    }

    public boolean isConnect() {
        return tcpClient.isConnect();
    }

    TCPClient.TCPClientListener listener = new TCPClient.TCPClientListener() {
        @Override
        public void onTcpConnect(int result) {
            if (result == 0) {
                //  连接成功才能启动发送线程
                connectSucc = true;
            } else {
                connectSucc = false;
            }
            LogUtils.d("onTcpConnect:" + result);
        }

        @Override
        public void onTcpDisConnect() {
            connectSucc = false;
            LogUtils.e("onTcpDisConnect:");
        }

        @Override
        public void onTcpSend(byte[] data, boolean result) {
            LogUtils.d("onTcpSend:" + result);
        }

        @Override
        public void onTcpReceive(byte[] receiveBytes) {
            LogUtils.d("onTcpReceive:" + receiveBytes.length);
        }
    };

    /**
     * 真正发送数据
     */
    @Override
    public void run() {
        //  线程所在, 初始为发送心跳
        LogUtils.d("thread run ----------------------------------------");
        byte[] data;
        try {
            while (PUBLISH_STATUS == PUBLISH_STATUS_START) {
                data = sendQueue.take();
                LogUtils.d("sendQueue.take():" + data.length);
                send(data);
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LogUtils.d("interrupt_Thread,关闭发送线程");
    }


}
