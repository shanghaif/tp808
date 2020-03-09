#include <cstdio>
#include <jni.h>
#include <string>
#include "libmp3lame/lame.h"
#include "android/log.h"
#define LOG_TAG "lameUtils"
#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG, LOG_TAG, __VA_ARGS__)
#define BUFFER_SIZE 8192

extern "C"
JNIEXPORT jstring JNICALL
Java_cn_erayton_voicelib_Mp3Lib_getHello(
        JNIEnv *env,
        jobject /* this */) {
    std::string hello = "Hello from C++";
    return env->NewStringUTF(hello.c_str());
}

static lame_global_flags *lame = NULL;
long nowConvertBytes = 0;
int channelInt = 2 ;


void resetLame() {
    if (lame != NULL) {
        lame_close(lame);
        lame = NULL;
    }
}

void lameInit(jint inSampleRate,
              jint channel, jint mode, jint outSampleRate,
              jint outBitRate, jint quality) {
    resetLame();
    lame = lame_init();
    lame_set_in_samplerate(lame, inSampleRate);
    lame_set_num_channels(lame, channel);
    lame_set_out_samplerate(lame, outSampleRate);
    lame_set_brate(lame, outBitRate);
    lame_set_quality(lame, quality);

    if(channel ==1){
//        设置最终mp3编码输出的声道模式，如果不设置则和输入声道数一样。
//        参数是枚举，STEREO代表双声道，MONO代表单声道。
        lame_set_mode(lame, MONO) ;
    } else{
        lame_set_mode(lame, STEREO) ;
    }

//    设置比特率控制模式，默认是CBR，但是通常我们都会设置VBR。
//    参数是枚举，vbr_off代表CBR，vbr_abr代表ABR（因为ABR不常见，所以本文不对ABR做讲解）vbr_mtrh代表VBR。
    if(mode == 0) { // use CBR
        lame_set_VBR(lame, vbr_default);
//        设置CBR的比特率，只有在CBR模式下才生效。
//        lame_set_brate(lame, 1000);
    } else if(mode == 1){ //use VBR
        lame_set_VBR(lame, vbr_abr);
    } else{ // use ABR
        lame_set_VBR(lame, vbr_mtrh);
//        设置VBR的比特率，只有在VBR模式下才生效。
//        lame_set_VBR_mean_bitrate_kbps(lame, 1000) ;
    }
    lame_init_params(lame);
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_erayton_voicelib_Mp3Lib_init(JNIEnv *env, jclass type, jint inSampleRate,
                                     jint channel, jint mode, jint outSampleRate,
                                     jint outBitRate, jint quality) {
    lameInit(inSampleRate, channel, mode, outSampleRate, outBitRate, quality);
    channelInt = channel ;
}

extern "C"
JNIEXPORT void JNICALL
Java_cn_erayton_voicelib_Mp3Lib_convertMp3(JNIEnv * env, jobject obj, jstring jInputPath, jstring jMp3Path) {
    const char* cInput = env->GetStringUTFChars(jInputPath, 0);
    const char* cMp3 = env->GetStringUTFChars(jMp3Path, 0);
//    int channel = 1 ;
    //open input file and output file
    FILE* fInput = fopen(cInput,"rb");
    //  丢弃 wav 头
    fseek(fInput, 4*1024, SEEK_CUR);
    FILE* fMp3 = fopen(cMp3,"wb+");
//    short int inputBuffer[BUFFER_SIZE * channel];
    short int inputBuffer[BUFFER_SIZE * channelInt];
    unsigned char mp3Buffer[BUFFER_SIZE];//You must specified at least 7200
    int read = 0; // number of bytes in inputBuffer, if in the end return 0
    int write = 0;// number of bytes output in mp3buffer.  can be 0
    long total = 0; // the bytes of reading input file
    nowConvertBytes = 0;
    //if you don't init lame, it will init lame use the default value
    if(lame == NULL){
        lameInit(44100, 2, 0, 44100, 96, 7);
    }
//    if (channel !=1){
    if (channelInt !=1){
        //convert to mp3
        do{
//        read = static_cast<int>(fread(inputBuffer, sizeof(short int) * channel, BUFFER_SIZE, fInput));
//        total +=  read * sizeof(short int)*channel;
        read = static_cast<int>(fread(inputBuffer, sizeof(short int) * channelInt, BUFFER_SIZE, fInput));
        total +=  read * sizeof(short int)*channelInt;
            nowConvertBytes = total;
        if(read != 0){
            write = lame_encode_buffer_interleaved(lame, inputBuffer, read, mp3Buffer, BUFFER_SIZE);
            //write the converted buffer to the file
            fwrite(mp3Buffer, sizeof(unsigned char), static_cast<size_t>(write), fMp3);
        }
        //if in the end flush origin
        if(read == 0){
            lame_encode_flush(lame,mp3Buffer, BUFFER_SIZE);
        }
        }while(read != 0);
    } else{
        //convert to mp3
        do{
//            read = static_cast<int>(fread(inputBuffer, sizeof(short int) * channel, BUFFER_SIZE, fInput));
//            total +=  read * sizeof(short int)*channel;
            read = static_cast<int>(fread(inputBuffer, sizeof(short int) * channelInt, BUFFER_SIZE, fInput));
            total +=  read * sizeof(short int)*channelInt;
            nowConvertBytes = total;
    //        if(read != 0){
    ////            write = lame_encode_buffer_interleaved(lame, inputBuffer, read, mp3Buffer, BUFFER_SIZE);
    //            write = lame_encode_buffer(lame, inputBuffer, NULL ,read, mp3Buffer, BUFFER_SIZE);
    //            //write the converted buffer to the file
    //            fwrite(mp3Buffer, sizeof(unsigned char), static_cast<size_t>(write), fMp3);
    //        }
    //        //if in the end flush origin
    //        if(read == 0){
    //            lame_encode_flush(lame,mp3Buffer, BUFFER_SIZE);
    //        }

            if(read!=0){
                write = lame_encode_buffer(lame, inputBuffer, NULL, read, mp3Buffer, BUFFER_SIZE);
                //write = lame_encode_buffer_interleaved(lame,wav_buffer,read,mp3_buffer,8192);
    //            fwrite(mp3Buffer, sizeof(unsigned char), static_cast<size_t>(write), fMp3);
            }else{
                write = lame_encode_flush(lame, mp3Buffer, BUFFER_SIZE);
            }
    //        //把转化后的mp3数据写到文件里
            fwrite(mp3Buffer, sizeof(unsigned char), static_cast<size_t>(write), fMp3);

            //把转化后的mp3数据写到文件里
        }while(read != 0);
    }

    //  修正文件秒数不正确
    lame_mp3_tags_fid(lame, fMp3) ;
    //release resources
    resetLame();
    fclose(fInput);
    fclose(fMp3);
    env->ReleaseStringUTFChars(jInputPath, cInput);
    env->ReleaseStringUTFChars(jMp3Path, cMp3);
    nowConvertBytes = -1;
}

extern "C" JNIEXPORT jstring JNICALL
Java_cn_erayton_voicelib_Mp3Lib_getLameVersion(
        JNIEnv *env, jobject /* this */) {
    return env->NewStringUTF(get_lame_version());
}

extern "C"
JNIEXPORT jlong JNICALL
Java_cn_erayton_voicelib_Mp3Lib_getConvertBytes(JNIEnv *env, jclass type) {
    return nowConvertBytes;
}