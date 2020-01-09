package cn.com.erayton.usagreement.model;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 鉴权
 * */
public class TerminalAuthMsg extends PacketData {

    private TerminalAuthInfo terminalAuthInfo ;

    public TerminalAuthInfo getTerminalAuthInfo() {
        return terminalAuthInfo;
    }

    public void setTerminalAuthInfo(TerminalAuthInfo terminalAuthInfo) {
        this.terminalAuthInfo = terminalAuthInfo;
    }

    @Override
    public void inflatePackageBody(byte[] data) {

    }

    @Override
    public byte[] packageDataBody2Byte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(terminalAuthInfo.getAuth().getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return baos.toByteArray() ;
    }


    public static class TerminalAuthInfo{
        private String auth ;

        public String getAuth() {
            return auth;
        }

        public void setAuth(String auth) {
            this.auth = auth;
        }

        @Override
        public String toString() {
            return "TerminalAuthInfo{" +
                    "auth='" + auth + '\'' +
                    '}';
        }
    }
}
