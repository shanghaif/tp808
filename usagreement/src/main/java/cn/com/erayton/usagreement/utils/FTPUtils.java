package cn.com.erayton.usagreement.utils;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;

import cn.com.erayton.usagreement.data.Constants;

public class FTPUtils {

    //ftp服务器地址
    private String hostname = "";
    //ftp服务器端口号默认为21
    private Integer port = 21;
    //ftp登录账号
    private String username = "";
    //ftp登录密码
    private String password = "";
    //超时时间
    public int timeOut = 300;
    //  被动模式开关 如果不开被动模式 有防火墙 可能会上传失败， 但被动模式需要ftp支持
    private boolean enterLocalPassiveMode = true;
    //  启用或禁用远程主机参与的验证; True启用验证，启用false禁用验证
    private boolean isRemoteVerification = true;

    private FTPClient ftpClient = null;

    private static FTPUtils instance = null ;
//    volatile private static FTPUtils instance = null ;

    private FTPUtils() {}

    public static FTPUtils getInstance() {
        if (null == instance){
            synchronized (FTPUtils.class){
                if (null == instance){
                    instance = new FTPUtils() ;
                }
            }
        }
        return instance;
    }
//    public static FTPUtils getInstance() {
//        if (null == instance){
//            instance = new FTPUtils() ;
//        }
//        return instance;
//    }

    //    private static class FTPUtilsHolder{
//        final static FTPUtils instant = new FTPUtils() ;
//    }
//
//    public static FTPUtils getInstance() {
//        return FTPUtilsHolder.instant;
//    }


    /**
     * 初始化配置  全局只需初始化一次
     * @param hostname  ftp服务器地址
     * @param port ftp服务器端口号默认为21
     * @param username ftp登录账号
     * @param password ftp登录密码
     */
    public void initFtpClient(String hostname, int port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;

        //初始化ftpclient对象
        ftpClient = new FTPClient();
        //设置超时时间以毫秒为单位使用时，从数据连接读。
        ftpClient.setDefaultTimeout(timeOut * 1000);
        ftpClient.setConnectTimeout(timeOut * 1000);
        ftpClient.setDataTimeout(timeOut * 1000);
        ftpClient.setControlEncoding(Constants.ENCODING_UTF8);
    }




    /**
     * 连接并登陆ftp
     * @return
     */
    public boolean connectFtp(){
        boolean flag = false;
        try {
            LogUtils.e("FTP", "连接...FTP服务器...");
            ftpClient.connect(hostname, port); //连接ftp服务器
            //  是否开启被动模式
            if (enterLocalPassiveMode) {
                ftpClient.setRemoteVerificationEnabled(isRemoteVerification);
                ftpClient.enterLocalPassiveMode();
            }
            ftpClient.login(username, password); // 登录ftp服务器
            int replyCode = ftpClient.getReplyCode(); //    是否成功登录服务器
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                LogUtils.e("FTP", "--------->连接...FTP服务器...失败: " + this.hostname + ":" + this.port+ "");
            }

            //  连接FTP服务器成功之后设置文件类型,图片为二进制
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            LogUtils.e("FTP","连接...FTP服务器...成功:" + this.hostname + ":" + this.port);
        } catch (MalformedURLException e) {
            LogUtils.e(e.getMessage(), e+"");
        } catch (IOException e) {
            LogUtils.e(e.getMessage(), e+"");
        }
        return flag;
    }

    /**
     * 上传文件
     *
     * @param ftpSavePath     ftp服务保存地址  (不带文件名)
     * @param ftpSaveFileName 上传到ftp的文件名
     * @param originFileName  待上传文件的名称（绝对地址） *
     * @return
     */

    private Object uploadFileStreamObject = new Object() ;
    public boolean uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName) {
//        synchronized (uploadFileStreamObject) {
            boolean flag = false;

            try {
                FileInputStream inputStream = new FileInputStream(new File(originFileName));
                flag = uploadFileStream(ftpSavePath, ftpSaveFileName, inputStream);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LogUtils.e("FTP", "------------>"+e.getMessage() + "  " + e);
            }
            return flag;
//        }
    }

    /**
     * 上传文件
     *
     * @param ftpSavePath     ftp服务保存地址  (不带文件名)
     * @param ftpSaveFileName 上传到ftp的文件名
     * @param originFileName  待上传文件的名称（绝对地址） *
     * @return
     */
    public void uploadFile(String ftpSavePath, String ftpSaveFileName, String originFileName , FTPListener listener) {
//        synchronized (uploadFileStreamObject1) {
            try {
                FileInputStream inputStream = new FileInputStream(new File(originFileName));
                uploadFileStream(ftpSavePath, ftpSaveFileName, inputStream, listener);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                LogUtils.e("FTP", "------------>" + e.getMessage() + "  " + e);
            }
//        }
    }

    /**
     * 上传文件(直接读取输入流形式)
     *
     * @param ftpSavePath    ftp服务保存地址
     * @param ftpSaveFileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */

    private Object uploadFileStreamObject1= new Object() ;
    public void uploadFileStream(String ftpSavePath, String ftpSaveFileName, InputStream inputStream, FTPListener listener) {
        boolean flag = false;
        try {
//            connectFtp();
            //第一次进来,将上传路径设置成相对路径
            if (ftpSavePath.startsWith("/")) {
                ftpSavePath = ftpSavePath.substring(1);
            }
            LogUtils.e("FTP", "上传文件的路径 :" +
                    ftpSavePath + ",上传文件名 :" + ftpSaveFileName);
            //  初始化FTP服务器
            connectFtp();
            //创建文件路径
            if (!createDirecroty(ftpSavePath)) {
                if (listener != null) {
                    listener.Status(123, "创建文件路径失败");
                    listener = null;
                }
                return;
            }

            LogUtils.e("FTP", "开始上传文件...");
            String remote = new String(ftpSaveFileName.getBytes("GBK"), "ISO8859-1");
            flag = ftpClient.storeFile(remote, inputStream);
//            flag = ftpClient.storeFile(new String(ftpSaveFileName.getBytes("GBK"), Constants.ENCODING_UTF8), inputStream);
            inputStream.close();
//            LogUtils.e("FTP","上传文件结束...结果 :" + (flag ? "成功" : "失败 "));
        } catch (IOException e) {
            LogUtils.e(e.getMessage(), e + "");
            e.printStackTrace();
        } finally {
            LogUtils.e("FTP", "上传文件结束...结果 :" + (flag ? "成功" : "失败 ") + ", replyCode:" + ftpClient.getReplyCode());
            if (listener != null) {
                if (flag) {
                    listener.Success();
                } else {
                    listener.Failer("上传失败");
                }
                listener = null;
            }
            closeFTP();
        }
    }

    /**
     * 上传文件(直接读取输入流形式)
     *
     * @param ftpSavePath    ftp服务保存地址
     * @param ftpSaveFileName    上传到ftp的文件名
     * @param inputStream 输入文件流
     * @return
     */
    public boolean uploadFileStream(String ftpSavePath, String ftpSaveFileName, InputStream inputStream) {
        boolean flag = false;
        try {
            //第一次进来,将上传路径设置成相对路径
            if (ftpSavePath.startsWith("/")) {
                ftpSavePath = ftpSavePath.substring(1);
            }
            LogUtils.e("FTP", "上传文件的路径 :" + ftpSavePath + ",上传文件名 :" + ftpSaveFileName);
            //  初始化FTP服务器
            connectFtp();
            //创建文件路径
            if (!createDirecroty(ftpSavePath)) {
                return flag;
            }
            LogUtils.e("FTP", "开始上传文件...");
            String remote = new String(ftpSaveFileName.getBytes("GBK"), "ISO8859-1");
            flag = ftpClient.storeFile(remote, inputStream);
            inputStream.close();
//            LogUtils.e("FTP","上传文件结束...结果 :" + (flag ? "成功" : "失败 "));
        } catch (IOException e) {
            LogUtils.e(e.getMessage(), e + "");
            e.printStackTrace();
        } finally {
            LogUtils.e("FTP", "上传文件结束...结果 :" + (flag ? "成功" : "失败 ") + ", replyCode:" + ftpClient.getReplyCode());
            closeFTP();
        }
        return flag;
    }

    //改变目录路径
    public boolean changeWorkingDirectory(String directory) {
        boolean flag = true;
        try {
            flag = ftpClient.changeWorkingDirectory(directory);
            if (!flag) {
                LogUtils.e("FTP", "所在的目录 : " + ftpClient.printWorkingDirectory() + "\n 进入下一级 " + directory + " 目录失败");
            } else {
                LogUtils.e("FTP","进入目录成功，当前所在目录 :" + ftpClient.printWorkingDirectory());
            }
        } catch (IOException ioe) {
            LogUtils.e(ioe.getMessage(), ioe+"");
        }
        return flag;
    }

    //创建多层目录文件，如果有ftp服务器已存在该文件，则不创建，如果无，则创建
    public boolean createDirecroty(String remote) throws IOException {
        boolean success = true;
        String directory = remote + "/";
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!directory.equalsIgnoreCase("/") && !changeWorkingDirectory(new String(directory))) {
            int start = 0;
            int end = 0;
            if (directory.startsWith("/")) {
                start = 1;
            } else {
                start = 0;
            }
            //从第一个"/"索引之后开始得到下一个"/"的索引
            end = directory.indexOf("/", start);
            while (true) {
                LogUtils.e("FTP","所在的目录 :" + ftpClient.printWorkingDirectory());
//                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), Constants.ENCODING_UTF8);
                String subDirectory = new String(remote.substring(start, end).getBytes("GBK"), "iso-8859-1");
                if (!existFile(subDirectory)) {
                    if (makeDirectory(subDirectory)) {
                        if (!changeWorkingDirectory(subDirectory)) {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    if (!changeWorkingDirectory(subDirectory)) {
                        return false;
                    }
                }

                start = end + 1;
                end = directory.indexOf("/", start);
                // 检查所有目录是否创建完毕
                if (end <= start) {
                    break;
                }
            }
        }
        return success;
    }

    //  判断ftp服务器文件是否存在
    public boolean existFile(String path) throws IOException {
        boolean flag = false;
        FTPFile[] ftpFileArr = ftpClient.listFiles(path);
        if (ftpFileArr.length > 0) {
            flag = true;
        }
        return flag;
    }

    //创建目录
    public boolean makeDirectory(String dir) {
        boolean flag = true;
        try {
            flag = ftpClient.makeDirectory(dir);
            if (!flag) {
                LogUtils.e("FTP","所在的目录 : " + ftpClient.printWorkingDirectory() + " 创建下一级 " + dir + " 目录失败 ");
            } else {
                LogUtils.e("FTP","所在的目录 : " + ftpClient.printWorkingDirectory() + " 创建下一级 " + dir + " 目录成功 ");
            }
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e+"");
        }
        return flag;
    }

    /**
     * 下载文件 *
     *
     * @param pathname  FTP服务器文件目录 *
     * @param filename  文件名称 *
     * @param localpath 下载后的文件路径 *
     * @return
     */
    public boolean downloadFile(String pathname, String filename, String localpath) {
        boolean flag = false;
        OutputStream os = null;
        connectFtp();
        try {
            //第一次进来,将上传路径设置成相对路径
            if (pathname.startsWith("/")) {
                pathname = pathname.substring(1);
            }
//            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //切换FTP目录
            changeWorkingDirectory(pathname);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile file : ftpFiles) {
                if (filename.equalsIgnoreCase(file.getName())) {
                    File localFile = new File(localpath + "/" + file.getName());
                    os = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(file.getName(), os);
                    os.close();
                }
            }
            flag = true;
        } catch (Exception e) {
            LogUtils.e(e.getMessage(), e+"");
            e.printStackTrace();
        } finally {
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                    LogUtils.e(e.getMessage(), e+"");
                    e.printStackTrace();
                }
            }
            closeFTP();
        }
        return flag;
    }

    /**
     * 删除文件 *(未测试)
     *
     * @param pathname FTP服务器保存目录 *
     * @param filename 要删除的文件名称 *
     * @return
     */
    public boolean deleteFile(String pathname, String filename) {
        boolean flag = false;
        try {
            //第一次进来,将上传路径设置成相对路径
            if (pathname.startsWith("/")) {
                pathname = pathname.substring(1);
            }
            connectFtp();
            //切换FTP目录
            changeWorkingDirectory(pathname);
            ftpClient.dele(filename);
            flag = true;
        } catch (Exception e) {
            LogUtils.e("FTP","删除文件失败 ");
            LogUtils.e("FTP",e.getMessage()+e+"");
        } finally {
            closeFTP();
        }
        return flag;
    }

    /**
     * 获取文件的输入流
     *
     * @param dir      ftp定义的存储路径 例如 /ftpFile/images
     * @param filename 上传的文件名
     * @return
     * @throws Exception
     *//*
    public InputStream getInputStream(String dir, String filename) {
        byte[] bytes = null;
        String path = dir + filename;
        InputStream in = null;
        try {
            connectFtp();
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // 转到指定下载目录
            if (path != null) {
                //验证是否有该文件夹，有就转到，没有创建后转到该目录下
                changeWorkingDirectory(path);// 转到指定目录下
            }
            // 不需要遍历，改为直接用文件名取
            String remoteAbsoluteFile = toFtpFilename(path);
            // 下载文件
            ftpClient.setBufferSize(1024 * 1024);
            ftpClient.setControlEncoding("UTF-8");
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(ftpClient.BINARY_FILE_TYPE);
            in = ftpClient.retrieveFileStream(remoteAbsoluteFile);
            *//*
     * bytes = input2byte(in); System.out.println("下载成功!" + bytes.length); //
     * in.read(bytes); in.close();
     *//*
        } catch (SocketException e) {
//            e.printStackTrace();
            Log.e(e.getMessage(), e+"");
        } catch (IOException e) {
//            e.printStackTrace();
            Log.e(e.getMessage(), e+"");
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e(e.getMessage(), e+"");
        }
        return in;
    }*/

    /**
     * 文件转成 byte[]
     *
     * @param inStream
     * @return
     * @throws IOException
     * @see [类、类#方法、类#成员]
     */
    public byte[] input2byte(InputStream inStream) throws IOException {
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        byte[] buff = new byte[inStream.available()];
        int rc = 0;
        while ((rc = inStream.read(buff, 0, 100)) > 0) {
            swapStream.write(buff, 0, rc);
        }
        byte[] in2b = swapStream.toByteArray();
        swapStream.close();
        return in2b;
    }

    /**
     * 转化输出的编码
     */
    private String toFtpFilename(String fileName) throws Exception {
        return new String(fileName.getBytes("UTF-8"), "ISO8859-1");
    }

    private void closeFTP(){
        if (ftpClient.isConnected()) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                LogUtils.e(e.getMessage(), e+"");
                e.printStackTrace();
            }
        }

    }

    public interface FTPListener{
        void Success() ;
        void Status(int code, String msg) ;
        void Failer(String errorMsg) ;
    }


}
