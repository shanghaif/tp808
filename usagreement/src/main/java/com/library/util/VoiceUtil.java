package com.library.util;

/**
 * Created by android1 on 2017/12/21.
 */

public class VoiceUtil {
    /**
     * 算法降噪，但效果并不是很理想
     *
     * @param src       原数据
     * @param srclength 原数据长度
     * @return
     */
    public static byte[] noise(byte[] src, int srclength) {
        byte[] bytes = new byte[srclength];
        srclength = srclength >> 1;
        for (int i = 0, dest; i < srclength; i++) {
            dest = (short) (((short) ((src[i * 2] & 0xff) | ((src[2 * i + 1] & 0xff) << 8))) >> 2);
            bytes[i * 2] = (byte) dest;
            bytes[i * 2 + 1] = (byte) (dest >> 8);
        }
        return bytes;
    }


    /**
     * PCM放大
     *
     * @param src      原数据
     * @param multiple 放大倍数
     * @return
     */
    public static byte[] increasePCM(byte[] src, int multiple) {
        if (multiple == 1) {
            return src;
        }
        int datalength = src.length >> 1;
        for (int i = 0, dest; i < datalength; i++) {
            dest = (short) ((src[i * 2] & 0xff) | (src[2 * i + 1] & 0xff) << 8) * multiple;
            //爆音处理
            dest = Math.max(-32768, Math.min(32767, dest));
            src[i * 2] = (byte) dest;
            src[i * 2 + 1] = (byte) (dest >> 8);
        }
        return src;
    }

    /**
     * aac音频添加adts头
     *  SCE: Single Channel Element单通道元素。单通道元素基本上只由一个ICS组成。一个原始数据块最可能由16个SCE组成。
     *
     * CPE: Channel Pair Element 双通道元素，由两个可能共享边信息的ICS和一些联合立体声编码信息组成。一个原始数据块最多可能由16个SCE组成。
     *
     * CCE: Coupling Channel Element 藕合通道元素。代表一个块的多通道联合立体声信息或者多语种程序的对话信息。
     *
     * LFE: Low Frequency Element 低频元素。包含了一个加强低采样频率的通道。
     *
     * DSE: Data Stream Element 数据流元素，包含了一些并不属于音频的附加信息。
     *
     * PCE: Program Config Element 程序配置元素。包含了声道的配置信息。它可能出现在ADIF 头部信息中。
     *
     * FIL: Fill Element 填充元素。包含了一些扩展信息。如SBR，动态范围控制信息等。
     *
     *  freqIdx: 3 -> 48000HZ  4 -> 44100HZ
     */
    public static void addADTStoPacket(byte[] packet, int packetLen) {
        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF1;
        packet[2] = (byte) (((2/*profile AAC LC*/ - 1) << 6) + (4/*freqIdx 44.1KHz*/ << 2) + (1/*chanCfgCPE*/ >> 2));
        packet[3] = (byte) (((1/*chanCfgCPE*/ & 3) << 6) + (packetLen >> 11));
//        packet[2] = (byte) (((2/*profile AAC LC*/ - 1) << 6) + (4/*freqIdx 44.1KHz*/ << 2) + (2/*chanCfgCPE*/ >> 2));
//        packet[3] = (byte) (((2/*chanCfgCPE*/ & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }
}