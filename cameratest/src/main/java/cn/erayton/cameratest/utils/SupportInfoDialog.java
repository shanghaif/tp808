package cn.erayton.cameratest.utils;

public class SupportInfoDialog extends CameraDialog {
    private String message ;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    String getTitle() {
        return null;
    }

    @Override
    String getMessage() {
        return message;
    }

    @Override
    String getOKBottonMsg() {
        return null;
    }

    @Override
    String getNoBottonMsg() {
        return null;
    }

    @Override
    void onButtonClick(int which) {
        dismiss();
    }


}
