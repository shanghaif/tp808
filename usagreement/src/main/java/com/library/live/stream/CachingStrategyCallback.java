package com.library.live.stream;

/**
 * Created by android1 on 2017/11/16.
 */

public interface CachingStrategyCallback {
    void videoStrategy(byte[] video);

    void voiceStrategy(byte[] voice);
}
