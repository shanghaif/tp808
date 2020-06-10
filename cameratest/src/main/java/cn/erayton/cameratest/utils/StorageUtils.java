package cn.erayton.cameratest.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.os.storage.StorageManager;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class StorageUtils {

    public static void writeFile(String path, byte[] data){
        FileOutputStream out = null ;
        try {
            out = new FileOutputStream(path) ;
            out.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void writeFile(File file, byte[] data){
        FileOutputStream out = null ;
        try {
            out = new FileOutputStream(file) ;
            out.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // Add the image to media store.
    public static Uri addImageToDB(ContentResolver resolver, String  title, long date,
                                   Location location, int orientation, long jpegLength,
                                   String path, int width, int height, String mimeType){
        // Insert into MediaStore.
        ContentValues values = new ContentValues(11) ;
        values.put(MediaStore.Images.ImageColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        if (mimeType.equalsIgnoreCase("jpeg") ||
            mimeType.equalsIgnoreCase("image/jpeg")){
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title+"jpg");
        }else {
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title+"raw");
        }
        values.put(MediaStore.Images.ImageColumns.DATE_ADDED, date);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation);
        values.put(MediaStore.Images.ImageColumns.DATA, path);
        values.put(MediaStore.Images.ImageColumns.SIZE, jpegLength);
        if (location!=null){
            values.put(MediaStore.Images.ImageColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Images.ImageColumns.LONGITUDE, location.getLongitude());
        }
        return insert(resolver, values, MediaStore.Images.Media.EXTERNAL_CONTENT_URI) ;
    }

    // Add the video to media store.
    public static Uri addVideoToDB(ContentResolver resolver, String title, long date,
                                   Location location, long length, String path,
                                   int width, int height, String mimeType) {
        // Insert into MediaStore.
        ContentValues values = new ContentValues(10) ;
        values.put(MediaStore.Video.VideoColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        values.put(MediaStore.Video.VideoColumns.DISPLAY_NAME, title+".mp4");
        values.put(MediaStore.Video.VideoColumns.DATE_TAKEN, date);
        values.put(MediaStore.Video.VideoColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.Video.VideoColumns.DATA, path);
        values.put(MediaStore.Video.VideoColumns.SIZE, length);
        if (location != null) {
            values.put(MediaStore.Video.VideoColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Video.VideoColumns.LONGITUDE, location.getLongitude());
        }
        return insert(resolver, values, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);


    }

    private static Uri insert(ContentResolver resolver, ContentValues values, Uri targetUri) {
        Uri uri = null ;
        try {
            uri = resolver.insert(targetUri, values);
        } catch (Throwable th) {
//            Log.e(TAG, "Failed to write MediaStore:" + th);
        }
        return uri ;
    }


    /*
        获取全部存储设备信息封装对象
    */
    public static ArrayList<Volume> getVolume(Context context) {
        ArrayList<Volume> list_storagevolume = new ArrayList<Volume>();

        StorageManager storageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);

        try {
            Method method_volumeList = StorageManager.class.getMethod("getVolumeList");

            method_volumeList.setAccessible(true);

            Object[] volumeList = (Object[]) method_volumeList.invoke(storageManager);
            if (volumeList != null) {
                Volume volume;
                for (int i = 0; i < volumeList.length; i++) {
                    try {
                        volume = new Volume();
                        volume.setPath((String) volumeList[i].getClass().getMethod("getPath").invoke(volumeList[i]));
                        volume.setRemovable((boolean) volumeList[i].getClass().getMethod("isRemovable").invoke(volumeList[i]));
                        volume.setState((String) volumeList[i].getClass().getMethod("getState").invoke(volumeList[i]));
                        list_storagevolume.add(volume);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }

                }
            } else {
                Log.e("null", "null-------------------------------------");
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return list_storagevolume;
    }



    /*
     存储设备信息封装类
     */
    public static class Volume {
        protected String path;
        protected boolean removable;
        protected String state;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public boolean isRemovable() {
            return removable;
        }

        public void setRemovable(boolean removable) {
            this.removable = removable;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }
    }
}
