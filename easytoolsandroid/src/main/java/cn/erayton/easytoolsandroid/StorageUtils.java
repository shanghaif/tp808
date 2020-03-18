package cn.erayton.easytoolsandroid;

import android.os.Environment;

public class StorageUtils {

    /**
     * Check if external storage is enabled
     * */
    public static boolean checkExternalStorageStatus(){

        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }
}
