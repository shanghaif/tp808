package cn.com.erayton.usagreement.model.model;

public class TerminalAuthInfo {

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
