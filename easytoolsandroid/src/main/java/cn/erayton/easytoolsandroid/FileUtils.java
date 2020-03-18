package cn.erayton.easytoolsandroid;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.erayton.easytoolsandroid.constants.PublicConstants;
/**文件类
 *
 * 临时文件地址   getDiskCachePath(Context context)
 * 添加文件         createDir(String fileDir)   createDir(File file)
 * 修改文件         writeFile( String msg, String filePath, boolean isAppend)
 * 删除
 * */
public class FileUtils {
    //	get file tmp folder /Android/data/packageName
    public static String getDiskCachePath(Context context) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            return context.getExternalCacheDir().getPath();
        } else {
            return context.getCacheDir().getPath();
        }
    }







    //region file create
    private static void createDir(String fileDir){
        createDir(new File(fileDir));
    }

    private static void createDir(File file){
        if (!file.exists()){
            File parentDir = file.getParentFile();
            assert parentDir != null;
            createDir(parentDir.getPath());
            file.mkdir() ;
        }
    }
    //endregion




    //region file write

    /**
     *
     * @param msg
     * @param filePath
     * @param isAppend
     */
    private static void writeFile( String msg, String filePath, boolean isAppend){
        writeFile(msg.getBytes(), filePath, isAppend);
    }

    private static boolean writeFile( byte[] bytes, String filePath, boolean isAppend){
        //  内存检查
        if (!StorageUtils.checkExternalStorageStatus()) {
            return false;
        }
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }

        FileOutputStream stream = null;
        try {
            int index = filePath.lastIndexOf("/");
            if (index == -1) {
                return false;
            }
            String fileDir = filePath.substring(0, index);
            createDir(fileDir);

            stream = new FileOutputStream(filePath, isAppend);
            stream.write(bytes);
            stream.flush();
            stream.close();
            return true;
        } catch (IOException e) {
            LogUtils.e(e);
        } catch (Exception e) {
            LogUtils.e(e);
        } catch (Error e) {
            LogUtils.e(e);
        }

        return false;
    }
    //endregion



    //region file save
    private static void saveLogFile(Context context, String msg){
        if (TextUtils.isEmpty(getDiskCachePath(context))) {
            return;
        }
        FileUtils.deleteOldFile(3, getDiskCachePath(context));
        String filePath = getDiskCachePath(context) + String.format("/%sLog.txt", TimeUtils.timeNowFormat(PublicConstants.TimeFormatConstants.TIME_STANDARD));
        msg = TimeUtils.timeNowFormat(PublicConstants.TimeFormatConstants.yMdHmssE) + "------" + msg + "\r\n\r\n";
        FileUtils.writeFile(msg, filePath, true);
    }
    //endregion

    //region file delete

    /** 删除指定天数的文件
     *
     * @param days  指定天数
     * @param dir   文件夹路径
     */
    private static void deleteOldFile(int days, String dir){
        if (TextUtils.isEmpty(dir)) {
            return;
        }
        File directory = new File(dir);
        if (!directory.isDirectory()) {
            return;
        }
//        try {
//            File[] files = directory.listFiles();
//            if (files != null) {
//                for (int i = 0; i < files.length; i++) {
//                    File file = files[i];
//                    if (file.isFile()) {
//                        DateTime lastModified = new DateTime(file.lastModified());
//                        lastModified.addDayOfMonth(days);
//                        if (lastModified.getStandardDate().compareTo(DateTime.now().getStandardDate()) <= 0) {
//                            file.delete();
//                        }
//                    }
//                }
//            }
//        } catch (Exception e) {
//            LogUtils.e(e);
//        } catch (Error e) {
//            LogUtils.e(e);
//        }
    }
    //endregion
}
