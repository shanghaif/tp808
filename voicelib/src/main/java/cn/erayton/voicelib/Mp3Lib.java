package cn.erayton.voicelib;

public class Mp3Lib {
    static {
        System.loadLibrary("lame-utils");
    }

    //  测试环境是否成功
    public static native String getHello();
    //  测试lame环境是否安装成功
    public static native String getLameVersion();

    /**
     * init lame
     * @param inSampleRate
     *              input sample rate in Hz
     * @param channel
     *              number of channels
     * @param mode
     *              0 = CBR, 1 = VBR, 2 = ABR.  default = 0
     * @param outSampleRate
     *              output sample rate in Hz
     * @param outBitRate
     *              rate compression ratio in KHz
     * @param quality
     *              quality=0..9. 0=best (very slow). 9=worst.<br />
     *              recommended:<br />
     *              2 near-best quality, not too slow<br />
     *              5 good quality, fast<br />
     *              7 ok quality, really fast
     */
    public native static void init(int inSampleRate, int channel, int mode,
                                   int outSampleRate, int outBitRate, int quality);

    /**
     * file convert to mp3
     * it may cost a lot of time and better put it in a thread
     * @param inputPath
     *          file path to be converted
     * @param mp3Path
     *          mp3 output file path
     */
    public native  static void convertMp3(String inputPath, String mp3Path);

    /**
     * get converted bytes in inputBuffer
     * @return
     *          converted bytes in inputBuffer
     *          to ignore the deviation of the file size,when return to -1 represents convert complete
     */
    public native static long getConvertBytes();
}
