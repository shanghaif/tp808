package com.library.common;

/**
 * Created by android1 on 2017/10/12.
 */

public interface UdpControlInterface {
    byte[] Control(byte[] bytes, int offset, int length);
}
