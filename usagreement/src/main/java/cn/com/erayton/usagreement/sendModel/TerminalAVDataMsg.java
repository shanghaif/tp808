package cn.com.erayton.usagreement.sendModel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.utils.BitOperator;
import cn.com.erayton.usagreement.utils.TimeUtils;

public class TerminalAVDataMsg {

    private TerminalAVDataInfo terminalAVDataInfo ;


    public TerminalAVDataInfo getTerminalAVDataInfo() {
        return terminalAVDataInfo;
    }

    public void setTerminalAVDataInfo(TerminalAVDataInfo terminalAVDataInfo) {
        this.terminalAVDataInfo = terminalAVDataInfo;
    }

    public byte[] packageDataBody2Byte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(terminalAVDataInfo.getLOGO());
            baos.write(terminalAVDataInfo.getvPXCc());
            baos.write(terminalAVDataInfo.isIsmPTV()?
                    terminalAVDataInfo.getmPtVideo(): terminalAVDataInfo.getmPtAudio());

            baos.write(BitOperator.getInstance().integerTo2Bytes(terminalAVDataInfo.getPackageNum()));
            baos.write(BitOperator.getInstance().string2Bcd(terminalAVDataInfo.getDevId()));
            baos.write((byte)terminalAVDataInfo.getChannelSignal());
            baos.write(Byte.parseByte(terminalAVDataInfo.getAvHeader(), 2));
            baos.write(BitOperator.getInstance().toDDbyte(terminalAVDataInfo.getTime(), 8));
            if (terminalAVDataInfo.isIsmPTV()){
                baos.write(terminalAVDataInfo.getFRAMETIMES());
            }
            baos.write(BitOperator.getInstance().toDDbyte(terminalAVDataInfo.getLength(), 2));
            baos.write(terminalAVDataInfo.getAvData());


        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray() ;
    }


    public static class TerminalAVDataInfo{
        //  帧头标识
        private static final byte[] LOGO = {0x30, 0x31, 0x63, 0x64};
        //  4 V / P / X / CC    bit
        private static final byte V_P_X_CC = (byte) Integer.parseInt("10000001",2);
        //  5 M / PT    bit
        private boolean ismPTV ;
        private static final byte M_PT_VIDEO = Byte.parseByte("01100010",2);    //  视频
        private static final byte M_PT_AUDIO = Byte.parseByte("00001000",2);    //  音频
        //  6 包序号
        private int packageNum ;
        //  8 SIM 卡号
        private String devId ;
        //  14 逻辑通道信号
        private int channelSignal ;
        //  添加视频头
        private String avHeader ;
        private static String headerFormat = "%04d00%02d" ;
        //  数据类型+分包处理
        private int avType ;
        private int packageType ;
        //  时间
        private long time ;
        //  26 上一帧间隔
        private static final byte[] FRAMETIMES = {0, 0, 0, 0};  //  帧时间
        //  28 数据体长度
        private int length ;
        //  30 数据体  长度不超过 950 byte
        private byte[] avData ;

        /**
         * @param ismPTV 是否为视频帧
         * @param channelSignal 逻辑通道号
         * @param avType 视频类型  I 帧， P 帧， B 帧，音频帧，透传数据(未处理此类型)
         * @param packageType 分包处理标记  原子包，第一包，最后一包，中间包
         * @param avData 音频数据
         * */
        public TerminalAVDataInfo(String devId, int packageNum, boolean ismPTV, int channelSignal,
                                  int avType, int packageType, byte[] avData) {
            this.ismPTV = ismPTV;
            this.packageNum = packageNum;
            this.devId = devId;
            this.channelSignal = channelSignal;
            this.time = TimeUtils.getTime() ;
            this.avType = avType;
            this.packageType = packageType;
            this.length = avData.length;
            this.avData = avData;
        }
        public TerminalAVDataInfo(String devId, int packageNum, boolean ismPTV, int channelSignal,
                                  long time, int avType, int packageType, byte[] avData) {
            this.ismPTV = ismPTV;
            this.packageNum = packageNum;
            this.devId = devId;
            this.channelSignal = channelSignal;
            this.time = time ;
            this.avType = avType;
            this.packageType = packageType;
            this.length = avData.length;
            this.avData = avData;
        }

        public byte[] getLOGO() {
            return LOGO;
        }

        public byte getvPXCc() {
            return V_P_X_CC;
        }

        public boolean isIsmPTV() {
            return ismPTV;
        }


        public byte getmPtVideo() {
            return M_PT_VIDEO;
        }

        public byte getmPtAudio() {
            return M_PT_AUDIO;
        }

        public void setIsmPTV(boolean ismPTV) {
            this.ismPTV = ismPTV;
        }


        public int getPackageNum() {
            return packageNum;
        }

        public void setPackageNum(int packageNum) {
            this.packageNum = packageNum;
        }

        public String getDevId() {
            return devId;
        }

        public void setDevId(String devId) {
            this.devId = devId;
        }

        public int getChannelSignal() {
            return channelSignal;
        }

        public void setChannelSignal(int channelSignal) {
            this.channelSignal = channelSignal;
        }

        public String getAvHeader() {
//            return avHeader;
            return String.format(headerFormat, avType, packageType);
        }

        public void setAvHeader(String avHeader) {
            this.avHeader = avHeader;
        }

        public int getAvType() {
            return avType;
        }

        public void setAvType(int avType) {
            this.avType = avType;
        }

        public int getPackageType() {
            return packageType;
        }

        public void setPackageType(int packageType) {
            this.packageType = packageType;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

        public byte[] getFRAMETIMES() {
            return FRAMETIMES;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public byte[] getAvData() {
            return avData;
        }

        public void setAvData(byte[] avData) {
            this.avData = avData;
        }
    }
}
