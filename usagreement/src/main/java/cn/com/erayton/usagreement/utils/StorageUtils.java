package cn.com.erayton.usagreement.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wenzhe on 9/6/17.
 */

public class StorageUtils {
    private static final String TAG = "StorageUtils";

    public static void writeFile(String path, byte[] data) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(path);
            out.write(data);
        } catch (Exception e) {
            Log.e(TAG, "Failed to write data", e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to close file after write", e);
            }
        }
    }

    public static void writeFile(File file, byte[] data) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(data);
        } catch (Exception e) {
            Log.e(TAG, "Failed to write data", e);
        } finally {
            try {
                out.close();
            } catch (Exception e) {
                Log.e(TAG, "Failed to close file after write", e);
            }
        }
    }

    // Add the image to media store.
    public static Uri addImageToDB(ContentResolver resolver, String title, long date,
            Location location, int orientation, long jpegLength,
            String path, int width, int height, String mimeType) {
        // Insert into MediaStore.
        ContentValues values = new ContentValues(11);
        values.put(MediaStore.Images.ImageColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        if (mimeType.equalsIgnoreCase("jpeg")
                || mimeType.equalsIgnoreCase("image/jpeg")) {
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title + ".jpg");
        } else {
            values.put(MediaStore.Images.ImageColumns.DISPLAY_NAME, title + ".raw");
        }
        values.put(MediaStore.Images.ImageColumns.DATE_TAKEN, date);
        values.put(MediaStore.Images.ImageColumns.MIME_TYPE, mimeType);
        values.put(MediaStore.Images.ImageColumns.ORIENTATION, orientation);
        values.put(MediaStore.Images.ImageColumns.DATA, path);
        values.put(MediaStore.Images.ImageColumns.SIZE, jpegLength);
        if (location != null) {
            values.put(MediaStore.Images.ImageColumns.LATITUDE, location.getLatitude());
            values.put(MediaStore.Images.ImageColumns.LONGITUDE, location.getLongitude());
        }
        return insert(resolver, values, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
    }

    // Add the video to media store.
    public static Uri addVideoToDB(ContentResolver resolver, String title, long date,
                                   Location location, long length, String path,
                                   int width, int height, String mimeType) {
        // Insert into MediaStore.
        ContentValues values = new ContentValues(10);
        values.put(MediaStore.Video.VideoColumns.TITLE, title);
        values.put(MediaStore.MediaColumns.WIDTH, width);
        values.put(MediaStore.MediaColumns.HEIGHT, height);
        values.put(MediaStore.Video.VideoColumns.DISPLAY_NAME, title + ".mp4");
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

    /**
     *
     * */
    private static Uri insert(ContentResolver resolver, ContentValues values, Uri targetUri) {
        Uri uri = null;
        try {
            uri = resolver.insert(targetUri, values);
        } catch (Throwable th) {
            Log.e(TAG, "Failed to write MediaStore:" + th);
        }
        return uri;
    }



    public static List<String> getTFCardPath(){
        List<String> sdList = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        InputStream is = null ;
        InputStreamReader isr = null ;
        BufferedReader br = null ;
        try {
            proc = runtime.exec("mount");
            is = proc.getInputStream();
            isr = new InputStreamReader(is);
            String line;
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;
                if (line.contains("fat")) {
                    String[] columns = line.split(" ");
                    if (columns.length > 1) {
                        if (RegularUtils.isContain(columns[1].trim(), RegularUtils.isStorage)){
                            sdList.add(columns[1]) ;
                        }
                    }
                }else if (line.contains("fuse")) {
                    String[] columns = line.split(" ");
                    if (columns.length > 1) {
                        if (RegularUtils.isContain(columns[1].trim(), RegularUtils.isStorage)){
                            sdList.add(columns[1]) ;
                        }
                    }
                }
            }
            LogUtils.e("\n------------- mount -------------\n"+sdList.size());
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (proc != null) {
                    proc.destroy();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sdList ;
    }


    public static String[] getTFCardList(){
        List<String> sdList = new ArrayList<>();
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        InputStream is = null ;
        InputStreamReader isr = null ;
        BufferedReader br = null ;
        try {
            proc = runtime.exec("mount");
            is = proc.getInputStream();
            isr = new InputStreamReader(is);
            String line;
            br = new BufferedReader(isr);
            while ((line = br.readLine()) != null) {
                if (line.contains("secure")) continue;
                if (line.contains("asec")) continue;
                if (line.contains("fat")) {
                    String[] columns = line.split(" ");
                    if (columns.length > 1) {
                        if (RegularUtils.isContain(columns[1].trim(), RegularUtils.isStorage)){
                            sdList.add(columns[1]) ;
                        }
                    }
                }else if (line.contains("fuse")) {
                    String[] columns = line.split(" ");
                    if (columns.length > 1) {
                        if (RegularUtils.isContain(columns[1].trim(), RegularUtils.isStorage)){
                            sdList.add(columns[1]) ;
                        }
                    }
                }
            }
        }  catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (proc != null) {
                    proc.destroy();
                }
                if (isr != null) {
                    isr.close();
                }
                if (br != null) {
                    br.close();
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        LogUtils.e("\n------------- mount -------------\n"+sdList.size()+"\n"+sdList.toString());
        return sdList.toArray(new String[0])  ;
    }

    public static void reflashMedia(Context context){
        reflashMedia(context,StorageUtils.getTFCardList());
    }

    public static void reflashMedia(Context context, String[] TFCardList){
        reflashMedia(context, TFCardList, new String[]{"video/*", "audio/*"});
    }

    public static void reflashMedia(Context context, String[] TFCardList, String[] typeList){
        reflashMedia(context,
//                        new String[]{"/storage/emulated", "/storage/74A2-A457"},
                TFCardList,
                typeList,
                null
        );
    }

    public static void reflashMedia(Context context, String[] TFCardList, String[] typeList, MediaScannerConnection.OnScanCompletedListener callback){
        MediaScannerConnection.scanFile(context,
                TFCardList,
                typeList,
                callback
        );
        //        MediaScannerConnection.scanFile(context,
//////                        new String[]{"/storage/emulated", "/storage/74A2-A457"},
////                StorageUtils.getTFCardList(),
////                new String[]{"video/*", "audio/*"},
////                null
////        );
    }


//    public static void getTFCardPath(){
//        Runtime runtime = Runtime.getRuntime();
//        Process proc = null;
//        InputStream is = null ;
//        InputStreamReader isr = null ;
//        BufferedReader br = null ;
//        try {
//            proc = runtime.exec("mount");
//
//            is = proc.getInputStream();
//            isr = new InputStreamReader(is);
//            String line;
//
////            String mount = null;
//            String mount = new String();
//            br = new BufferedReader(isr);
//            while ((line = br.readLine()) != null) {
//                if (line.contains("secure")) continue;
//                if (line.contains("asec")) continue;
//                if (line.contains("fat")) {
//                    String[] columns = line.split(" ");
////                    String columns[] = line.split(" ");
////                    if (columns != null && columns.length > 1) {
//                    if (columns.length > 1) {
//                        mount = mount.concat("*" + columns[1] + "\n");
//                    }
//                }else if (line.contains("fuse")) {
//                    String[] columns = line.split(" ");
////                    String columns[] = line.split(" ");
//                    if (columns.length > 1) {
////                    if (columns != null && columns.length > 1) {
//                        mount = mount.concat(columns[1] + "\n");
//                    }
//                }
//            }
//            LogUtils.e("\n------------- mount -------------\n"+mount);
//        }  catch (FileNotFoundException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            try {
//                if (is != null) {
//                    is.close();
//                }
//                if (proc != null) {
//                    proc.destroy();
//                }
//                if (isr != null) {
//                    isr.close();
//                }
//                if (br != null) {
//                    br.close();
//                    br.close();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//        }
//    }
}

