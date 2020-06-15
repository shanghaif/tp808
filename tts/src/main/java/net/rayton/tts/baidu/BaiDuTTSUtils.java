package net.rayton.tts.baidu;

import android.content.Context;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.os.Environment;
import android.util.Log;

import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import net.rayton.tts.ApplicationProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

public class BaiDuTTSUtils implements SpeechSynthesizerListener {
    private static final String TAG = "TTSUtils";
    private static volatile BaiDuTTSUtils instance = null;
    private SpeechSynthesizer mSpeechSynthesizer;

    private static final String SAMPLE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath() + "/baiduTTS/";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";

    private static final String APIKEY = "dUewi3mj059bYwGC3trZHnrG";
    private static final String SECRETKEY = "dQtw9HkDwU0v9tP4nMYEKYm3lxeQCDZ6";
    private static final String APPID = "16779464";

    private BaiDuTTSUtils() {
    }

    public static BaiDuTTSUtils getInstance() {
        if (instance == null) {
            synchronized (BaiDuTTSUtils.class) {
                if (instance == null) {
                    instance = new BaiDuTTSUtils();
                }
            }
        }
        return instance;
    }

    public void init() {
        Context context = ApplicationProvider.context;
//        File file = new File(SAMPLE_DIR);
//        if (!file.exists()) {
//            file.mkdirs();
//        }
//        File textModelFile = new File(SAMPLE_DIR + TEXT_MODEL_NAME);
//        if (!textModelFile.exists()) {
//            copyAssetsFile2SDCard(context, TEXT_MODEL_NAME, SAMPLE_DIR + TEXT_MODEL_NAME);
//        }
//        File speechModelFile = new File(SAMPLE_DIR + SPEECH_FEMALE_MODEL_NAME);
//        if (!speechModelFile.exists()) {
//            copyAssetsFile2SDCard(context, SPEECH_FEMALE_MODEL_NAME, SAMPLE_DIR + SPEECH_FEMALE_MODEL_NAME);
//        }
        try {
            initOfflineVoiceType(VOICE_MALE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 获取语音合成对象实例
        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        // 设置context
        mSpeechSynthesizer.setContext(context);
        // 设置语音合成状态监听器
        mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        mSpeechSynthesizer.setApiKey(APIKEY, SECRETKEY);
        // 设置离线语音合成授权，需要填入从百度语音官网申请的app_id
        mSpeechSynthesizer.setAppId(APPID);

        // 设置语音合成文本模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, SAMPLE_DIR + TEXT_MODEL_NAME);
        // 设置语音合成声音模型文件
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, SAMPLE_DIR + SPEECH_FEMALE_MODEL_NAME);
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "2");
        // 设置合成的音量，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME,"1");
        // 设置合成的语速，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");
        // 设置合成的语调，0-9 ，默认 5
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "5");

        mSpeechSynthesizer.setAudioStreamType(AudioManager.STREAM_ALARM);
//        int result = mSpeechSynthesizer.setStereoVolume (0.1f, 0.1f);
//        Log.d(TAG, "音量: "+result);
        // 设置Mix模式的合成策略
        // 该参数设置为TtsMode.MIX生效。即纯在线模式不生效。
        // MIX_MODE_DEFAULT 默认 ，wifi状态下使用在线，非wifi离线。在线状态下，请求超时6s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI wifi状态下使用在线，非wifi离线。在线状态下， 请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_NETWORK ， 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        // MIX_MODE_HIGH_SPEED_SYNTHESIZE, 2G 3G 4G wifi状态下使用在线，其它状态离线。在线状态下，请求超时1.2s自动转离线
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_HIGH_SPEED_SYNTHESIZE_WIFI);
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.ONLINE);
    }

    //需要合成的msg长度不能超过1024个GBK字节。
    public void speak(String msg) {
        int result = mSpeechSynthesizer.speak(msg);
        if (result < 0) {
            Log.e(TAG, "error,please look up error code = " + result + " in doc or URL:http://yuyin.baidu.com/docs/tts/122 ");
        }
    }

    public void pause() {
        mSpeechSynthesizer.pause();
    }

    public void resume() {
        mSpeechSynthesizer.resume();
    }

    public void stop() {
        mSpeechSynthesizer.stop();
    }

    public void release() {
        if (null != mSpeechSynthesizer) {
            mSpeechSynthesizer.release();
        }
    }

    @Override
    public void onSynthesizeStart(String s) {
        // 监听到合成开始，在此添加相关操作
    }

    @Override
    public void onSynthesizeDataArrived(String s, byte[] bytes, int i, int i1) {
        // 监听到有合成数据到达，在此添加相关操作
    }

    @Override
    public void onSynthesizeFinish(String s) {
        // 监听到合成结束，在此添加相关操作
    }

    @Override
    public void onSpeechStart(String s) {
        // 监听到合成并播放开始，在此添加相关操作
    }

    @Override
    public void onSpeechProgressChanged(String s, int i) {
        // 监听到播放进度有变化，在此添加相关操作
    }

    @Override
    public void onSpeechFinish(String s) {
        // 监听到播放结束，在此添加相关操作
    }

    @Override
    public void onError(String s, SpeechError speechError) {
        // 监听到出错，在此添加相关操作
    }

    public static void copyAssetsFile2SDCard(Context context, String fileName, String path) {
        try {
            InputStream is = context.getAssets().open(fileName);
            FileOutputStream fos = new FileOutputStream(new File(path));
            byte[] buffer = new byte[1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取buffer字节
                fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
            }
            fos.flush();// 刷新缓冲区
            is.close();
            fos.close();
        } catch (IOException e) {
            Log.e(TAG, "copyAssetsFile2SDCard: " + e.toString());
        }
    }
    private static HashMap<String, Boolean> mapInitied = new HashMap<String, Boolean>();
    public static String copyAssetsFile(String sourceFilename) throws IOException {
        String destPath= FileUtils.createTmpDir(ApplicationProvider.context);
        String destFilename = destPath + "/" + sourceFilename;
        boolean recover = false;
        Boolean existed = mapInitied.get(sourceFilename); // 启动时完全覆盖一次
        if (existed == null || !existed) {
            recover = true;
        }
        AssetManager assets = ApplicationProvider.context.getAssets();
        FileUtils.copyFromAssets(assets, sourceFilename, destFilename, recover);
        Log.i(TAG, "文件复制成功：" + destFilename);
        return destFilename;
    }
    //    map.put("离线女声", OfflineResource.VOICE_FEMALE);
//    map.put("离线男声", OfflineResource.VOICE_MALE);
//    map.put("离线度逍遥", OfflineResource.VOICE_DUXY);
//    map.put("离线度丫丫", OfflineResource.VOICE_DUYY);
    public static final String VOICE_FEMALE = "F";

    public static final String VOICE_MALE = "M";


    public static final String VOICE_DUYY = "Y";

    public static final String VOICE_DUXY = "X";
    public static void initOfflineVoiceType(String voiceType) throws IOException {
        String text = "bd_etts_text.dat";
        String model;
        if (VOICE_MALE.equals(voiceType)) {
            model = "bd_etts_common_speech_m15_mand_eng_high_am-mix_v3.0.0_20170505.dat";
        } else if (VOICE_FEMALE.equals(voiceType)) {
            model = "bd_etts_common_speech_f7_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        } else if (VOICE_DUXY.equals(voiceType)) {
            model = "bd_etts_common_speech_yyjw_mand_eng_high_am-mix_v3.0.0_20170512.dat";
        } else if (VOICE_DUYY.equals(voiceType)) {
            model = "bd_etts_common_speech_as_mand_eng_high_am_v3.0.0_20170516.dat";
        } else {
            throw new RuntimeException("voice type is not in list");
        }
        copyAssetsFile(text);
        copyAssetsFile(model);

    }
}
