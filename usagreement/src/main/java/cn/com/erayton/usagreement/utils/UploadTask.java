package cn.com.erayton.usagreement.utils;

import android.os.AsyncTask;
import android.os.Environment;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

class UploadTask extends AsyncTask<String, Object, Integer> {

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Integer doInBackground(String... params) {
//        "60.13.227.76", 21, "31gpsftp1", "1q2w3e"
        String ftp_url = "60.13.227.76";
        String ftp_name = "31gpsftp1";
        String ftp_pwd = "1q2w3e";

        String fileName = params[0];

        String basePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String filePath = basePath + File.separator + "caiyun" + File.separator + fileName;
        String remotePath = File.separator + fileName.substring(0, 2);
        FTPClient ftpClient = new FTPClient();
        FileInputStream fis;
        int returnMessage = 0;
        try {
            ftpClient.connect(ftp_url, 21);
            boolean loginResult = ftpClient.login(ftp_name, ftp_pwd);
            int returnCode = ftpClient.getReplyCode();
            if (loginResult && FTPReply.isPositiveCompletion(returnCode)) {// 如果登录成功
                ftpClient.makeDirectory(remotePath);
                // 设置上传目录
                ftpClient.changeWorkingDirectory(remotePath);
                ftpClient.setBufferSize(1024);
                ftpClient.setControlEncoding("UTF-8");
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                fis = new FileInputStream(filePath);

                //不计算进度条
                //ftpClient.storeFile(fileName, fis);

                //计算进度条
                int n = -1;
                long pContentLength = fis.available();
                long trans = 0;
                int bufferSize = ftpClient.getBufferSize();
                byte[] buffer = new byte[bufferSize];
                OutputStream outputstream = ftpClient.storeFileStream(new String(fileName.getBytes("utf-8"), "iso-8859-1"));
                while ((n = fis.read(buffer)) != -1) {
                    outputstream.write(buffer, 0, n);
                    trans += n;
                    //trans已传输字节  pContentLength总字节
                    publishProgress(trans, pContentLength);
                }
                fis.close();
                outputstream.flush();
                outputstream.close();

                returnMessage = 1;   //上传成功
            } else {// 如果登录失败
                returnMessage = 0;
            }


        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("FTP客户端出错！", e);
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException("关闭FTP连接发生异常！", e);
            }
        }
        return returnMessage;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if (result == 1) {
            //上传成功后调用
            LogUtils.e("FTP", "success ---------------------");
        }

    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        //获取进度
        long trans = (long) values[0];
        long pContentLength = (long) values[1];
        int progress = (int) (trans * 100 / pContentLength);
        LogUtils.e("FTP", "progress ---"+progress);
    }
}
