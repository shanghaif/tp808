package com.library.live.stream;

import java.nio.ByteBuffer;

public abstract class Sender {

    protected void send(){
        System.out.println("send ----------------------------------");
    }

    protected void initSocket(String ip, int port){

    }
    abstract void destroy() ;
    abstract void startsend() ;
    abstract void stopsend() ;
    //    abstract void send(byte[] bytes) ;
    abstract void setWeight(double weight) ;
    abstract int getPublishStatus() ;

    abstract void addVideo(byte[] video, int isIFrame) ;
    abstract void addVoice(byte[] voice) ;
    abstract void writeVideo(byte[] video, int isIFrame) ;
    abstract void writeVoice(byte[] voice) ;



}
