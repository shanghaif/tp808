//
// Created by Android on 2020/8/19.
//
#include <jni.h>
#include <android/log.h>
#include <unistd.h>

#include "g726.h"

#define ABS(a) (((a)<0)?-(a):a)


// reduce size from 480 16bit PCM (960 bytes) to 120 bytes (ratio 1:8)
// running 8kHz makes ---> 16 frames with 480 samples = 16*480=8000,
// 16 frames encoded to 120*16=1920 bytes/sec
static int16_t pcm_in[480];
static int16_t pcm_out[480];
static int8_t bitstream[120];

// Android 打印 Log
#define LOGE(FORMAT,...) __android_log_print(ANDROID_LOG_ERROR, "g726", FORMAT, ##__VA_ARGS__);

//extern "C"
//JNIEXPORT void JNICALL
//Java_com_library_util_AudioUtils_pcmToG726(JNIEnv *env, jobject instance, jbyteArray pcm_,
//                                           jbyteArray result_) {
//    int i ;
//    jbyte *pcm = env->GetByteArrayElements(pcm_, NULL);
//    jbyte *result = env->GetByteArrayElements(result_, NULL);
//
//    LOGE("Test G726\n");
//
//    for (i = 0; i < 480; i++) {
//        pcm_in[i]  = -0x7800;
//        pcm_out[i] = 0;
//    }
//
//    LOGE("Encode...\n");
//    g726_encode(reinterpret_cast<int16_t *>(pcm),
//                result);
//
//    memcpy(result, result, sizeof(result)) ;
//
//    LOGE("Decode...\n");
//    g726_decode(bitstream,
//                pcm_out);
//
//    for (i = 0; i < 480; i++) {
//        int16_t cin  = pcm_in[i];
//        int16_t cout = pcm_out[i];
//        int16_t diff = ABS(cin - cout);
//        printf("%04x: [%04x - %04x: %d]\n", i, cin & 0xFFFF, cout & 0xFFFF, diff & 0xFFFF);
//    }
//
//    LOGE("Done.\n");
//
//
//
//    env->ReleaseByteArrayElements(pcm_, pcm, 0);
//    env->ReleaseByteArrayElements(result_, result, 0);
//
//}

extern "C"
JNIEXPORT jbyteArray JNICALL
Java_com_library_util_AudioUtils_pcmToG726(JNIEnv *env, jobject instance, jbyteArray pcm_) {
    int i ;
    jbyte *pcm = env->GetByteArrayElements(pcm_, NULL);
    jbyteArray result = NULL;
    LOGE("Test jbyteArray G726\n");

    for (i = 0; i < 480; i++) {
        pcm_in[i]  = -0x7800;
        pcm_out[i] = 0;
    }
//
    LOGE("jbyteArray Encode...\n");
    g726_encode(pcm_in,
                bitstream);

    env->ReleaseByteArrayElements(pcm_, pcm, 0);
    return result ;
}


