package cn.com.erayton.usagreement.model;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.com.erayton.usagreement.utils.BitOperator;


/**
 *  终端通用回复
 * */
public class TerminalGeneralMsg extends PacketData {

    private TerminalGeneralInfo terminalGeneralInfo ;

    public TerminalGeneralInfo getTerminalGeneralInfo() {
        return terminalGeneralInfo;
    }

    public void setTerminalGeneralInfo(TerminalGeneralInfo terminalGeneralInfo) {
        this.terminalGeneralInfo = terminalGeneralInfo;
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        BitOperator bitOperator = BitOperator.getInstance();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            baos.write(bitOperator.integerTo2Bytes(terminalGeneralInfo.getSeNum()));
            baos.write(bitOperator.integerTo2Bytes(terminalGeneralInfo.getRespId()));
            baos.write(bitOperator.integerTo1Byte(terminalGeneralInfo.getResult()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray() ;
    }


    public static class TerminalGeneralInfo{
//        结果
        private int result ;
        //  应答 ID
        private int respId ;
        //  应答流水号
        private int seNum ;

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public int getRespId() {
            return respId;
        }

        public void setRespId(int respId) {
            this.respId = respId;
        }

        public int getSeNum() {
            return seNum;
        }

        public void setSeNum(int seNum) {
            this.seNum = seNum;
        }

        @Override
        public String toString() {
            return "TerminalGeneralInfo{" +
                    "result=" + result +
                    ", respId=" + respId +
                    ", seNum=" + seNum +
                    '}';
        }
    }
}
