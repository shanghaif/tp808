package cn.com.erayton.usagreement.model.model;

public class TerminalAVPropertieInfo {
    //  输入音频编码方式
    private int audioEncoding;
    //  输入音频声道数
    private int audioChannel;
    //  输入音频采样率
    private int audioRate;
    //  输入音频采样位数
    private int audioNum;
    //  音频帧长度
    private long audioLength;
    //  是否支持音频输出
    private int audioSupport;
    //  视频编码方式
    private int videoEncoding;
    //  终端支持的最大音频物理通道数量
    private int maxAudioChannel ;
    //  终端支持的最大视频物理通道数量
    private int maxVideoChannel ;


    public int getAudioEncoding() {
        return audioEncoding;
    }

    public void setAudioEncoding(int audioEncoding) {
        this.audioEncoding = audioEncoding;
    }

    public int getAudioChannel() {
        return audioChannel;
    }

    public void setAudioChannel(int audioChannel) {
        this.audioChannel = audioChannel;
    }

    public int getAudioRate() {
        return audioRate;
    }

    public void setAudioRate(int audioRate) {
        this.audioRate = audioRate;
    }

    public int getAudioNum() {
        return audioNum;
    }

    public void setAudioNum(int audioNum) {
        this.audioNum = audioNum;
    }

    public long getAudioLength() {
        return audioLength;
    }

    public void setAudioLength(long audioLength) {
        this.audioLength = audioLength;
    }

    public int getAudioSupport() {
        return audioSupport;
    }

    public void setAudioSupport(int audioSupport) {
        this.audioSupport = audioSupport;
    }

    public int getVideoEncoding() {
        return videoEncoding;
    }

    public void setVideoEncoding(int videoEncoding) {
        this.videoEncoding = videoEncoding;
    }

    public int getMaxAudioChannel() {
        return maxAudioChannel;
    }

    public void setMaxAudioChannel(int maxAudioChannel) {
        this.maxAudioChannel = maxAudioChannel;
    }

    public int getMaxVideoChannel() {
        return maxVideoChannel;
    }

    public void setMaxVideoChannel(int maxVideoChannel) {
        this.maxVideoChannel = maxVideoChannel;
    }

    @Override
    public String toString() {
        return "TerminalAVPropertieInfo{" +
                "audioEncoding=" + audioEncoding +
                ", audioChannel=" + audioChannel +
                ", audioRate=" + audioRate +
                ", audioNum=" + audioNum +
                ", audioLength=" + audioLength +
                ", supportAudio=" + audioSupport +
                ", videoEncoding=" + videoEncoding +
                ", maxAudioChannel=" + maxAudioChannel +
                ", maxVideoChannel=" + maxVideoChannel +
                '}';
    }
}
