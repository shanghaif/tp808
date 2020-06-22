package cn.com.erayton.usagreement.utils;

import android.content.ContentResolver;
import android.database.Cursor;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import cn.com.erayton.usagreement.utils.LogUtils;

public class FileUtils {

    // 获取当前目录下所有的mp4文件
    public static Vector<String > getVideoFileName(String fileAbsolutePath) {
        try {
            Vector<String> vecFile = new Vector<String>();
            LogUtils.d("fileAbsolutePath:" + fileAbsolutePath);
            File file = new File(fileAbsolutePath);
            File[] subFile = file.listFiles();
            LogUtils.d("subFile:" + (subFile == null)+","+subFile.length);

            for (int i = 0; i < subFile.length; i++) {
                // 判断是否为文件夹
                if (!subFile[i].isDirectory()) {
                    String filename = subFile[i].getName();
                    // 判断是否为MP4结尾
                    if (filename.trim().toLowerCase().endsWith(".mp4")) {
                        vecFile.add(filename);
                        LogUtils.d("filename:" + filename);
                    }
                }
            }
            return vecFile;
        }catch (NullPointerException e){
            e.printStackTrace();
        }
        return null ;
    }

    public static void getVideoMsg(String path){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        LogUtils.d("path:" + path);
        try {
            mmr.setDataSource(path);
            String title = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_TITLE); // api level 10, 即从GB2.3.3开始有此功能
            LogUtils.d("title:" + title);
            String album = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ALBUM);
            LogUtils.d("album:" + album);
            String mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            LogUtils.d("mime:" + mime);
            String artist = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_ARTIST);
            LogUtils.d("artist:" + artist);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); // 播放时长单位为毫秒
            LogUtils.d("duration:" + duration);
            String bitrate = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_BITRATE); // 从api level 14才有，即从ICS4.0才有此功能
            LogUtils.d("bitrate:" +bitrate);
            String date = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DATE);
            LogUtils.d("date:" + date);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    public static String getFileLastModifiedTime(File file) {

        String mformatType = "yyyy/MM/dd HH:mm:ss";
        Calendar cal = Calendar.getInstance();
        long time = file.lastModified();
        SimpleDateFormat formatter = new SimpleDateFormat(mformatType);
        cal.setTimeInMillis(time);
        LogUtils.d("getFileLastModifiedTime:" + formatter.format(cal.getTime()));
        // 输出：修改时间[2] 2009-08-17 10:32:38

        LogUtils.d("title:" + file.getName());
        LogUtils.d("getUsableSpace:" + file.getUsableSpace());
        return formatter.format(cal.getTime());
    }

    public static void getFileMsg(String path){
        File f = new File(path);
        if(f.exists())
        {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
                String time=new SimpleDateFormat("yyyy-MM-dd").format(new Date(f.lastModified()));
                System.out.println("文件文件创建时间"+time);
                System.out.println("文件大小:"+ ShowLongFileSzie(f.length()));//计算文件大小  B,KB,MB,
                System.out.println("文件大小:"+fis.available()+"B");
                System.out.println("文件名称：" + f.getName());
                System.out.println("文件是否存在：" + f.exists());
                System.out.println("文件的相对路径：" + f.getPath());
                System.out.println("文件的绝对路径：" + f.getAbsolutePath());
                System.out.println("文件可以读取：" + f.canRead());
                System.out.println("文件可以写入：" + f.canWrite());
                System.out.println("文件上级路径：" + f.getParent());
                System.out.println("文件大小：" + f.length() + "B");
                System.out.println("文件最后修改时间：" + new Date(f.lastModified()));
                System.out.println("是否是文件类型：" + f.isFile());
                System.out.println("是否是文件夹类型：" + f.isDirectory());
                LogUtils.d("文件文件创建时间:"+time+"\n"+"文件大小:"+ShowLongFileSzie(f.length())+"\n"+"文件名称：" + f.getName()
                        +"\n"+"文件是否存在：" + f.exists()+"\n"+"文件的相对路径：" + f.getPath()+"\n"+"文件的绝对路径：" + f.getAbsolutePath()+"\n"+"文件可以写入：" + f.canWrite()
                        +"\n"+"是否是文件夹类型：" + f.isDirectory());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /****
     *计算文件大小
     * @param length
     * @return
     */
    private static String ShowLongFileSzie(Long length)
    {
        if(length>=1048576)
        {
            return (length/1048576)+" MB";
        }
        else if(length>=1024)
        {
            return (length/1024)+" KB";
        }
        else if(length<1024) {
            return length + " B";
        }else{
            return "0 KB";
        }
    }

//    /**
//     * 获取SD卡视频文件
//     * @param mResolver
//     */
//    public static void getNativeVideo(ContentResolver mResolver){
//
//        //获取视频的名称
//        // String[] projection = new String[]{MediaStore.Video.Media.TITLE};
//        //获取视频路径
////        String[] projection = new String[]{MediaStore.Video.Media.DATA};
//        //  获取视频路径所有信息
//        Cursor cursor = mResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null,
//                null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
//        cursor.moveToFirst();
//        int fileNum = cursor.getCount();
//        for(int counter = 0; counter < fileNum; counter++){
//            //获取视频的名称
//            LogUtils.d("----------------------file is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)) );
//            //获取视频路径
//            LogUtils.d("----------------------DATA is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)) );
////            LogUtils.d("----------------------SIZE is: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) );
//            LogUtils.d("----------------------SIZE is: " + ShowLongFileSzie(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) ));
//            LogUtils.d("----------------------DISPLAY_NAME is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)) );
//            LogUtils.d("----------------------DATE_ADDED is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)) );
//            LogUtils.d("----------------------WIDTH is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)) );
//            LogUtils.d("----------------------HEIGHT is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)) );
//            LogUtils.d("----------------------DATE_MODIFIED is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)) );
//            LogUtils.d("----------------------MIME_TYPE is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)) );
//            LogUtils.d("----------------------DESCRIPTION is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION)) );
//            LogUtils.d("----------------------DURATION is: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) );
//            cursor.moveToNext();
//        }
//        cursor.close();
//    }
    /**
     * 获取SD卡视频文件
     * @param mResolver
     * @param type MediaStore.Video.Media.EXTERNAL_CONTENT_URI ->0 , MediaStore.Audio.Media.EXTERNAL_CONTENT_URI->1
     */
    public static List<FileMsg> getNativeVideo(ContentResolver mResolver, int type){
        List<FileMsg> fileMsgList = new ArrayList<>() ;

        //获取视频的名称
        // String[] projection = new String[]{MediaStore.Video.Media.TITLE};
        //获取视频路径
//        String[] projection = new String[]{MediaStore.Video.Media.DATA};
        //  获取视频路径所有信息
        Cursor cursor = mResolver.query(type ==0?MediaStore.Video.Media.EXTERNAL_CONTENT_URI:MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null,
                null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int fileNum = cursor.getCount();
        //  防止太多,只显示100条
        for(int counter = 0; counter < (fileNum>20?20:fileNum); counter++){
            //获取视频的名称
//            LogUtils.d("----------------------file is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)) );
//            //获取视频路径
//            LogUtils.d("----------------------DATA is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)) );
//            LogUtils.d("----------------------SIZE is: " + cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) );
//            LogUtils.d("----------------------SIZE is: " + ShowLongFileSzie(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)) ));
//            LogUtils.d("----------------------DISPLAY_NAME is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)) );
//            LogUtils.d("----------------------DATE_ADDED is: " + cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)) );
//            LogUtils.d("----------------------timeStamp2Date is: " + TimeUtils.timeStamp2Date(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)), "yyMMddHHmmss"));
//            LogUtils.d("----------------------WIDTH is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.WIDTH)) );
//            LogUtils.d("----------------------HEIGHT is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.HEIGHT)) );
//            LogUtils.d("----------------------DATE_MODIFIED is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_MODIFIED)) );
//            LogUtils.d("----------------------MIME_TYPE is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.MIME_TYPE)) );
//            LogUtils.d("----------------------DESCRIPTION is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DESCRIPTION)) );
//            LogUtils.d("----------------------DURATION is: " + cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) );
            FileMsg fileMsg = new FileMsg() ;
            fileMsg.setFileName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE)));
            fileMsg.setStartTime(TimeUtils.timeStamp2Date(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED)), "yyMMddHHmmss"));
            fileMsg.setFilePath(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA)));
            fileMsg.setFileSize(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.SIZE)));
            fileMsg.setFileType(type);
            fileMsg.setDisplayName(cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)));
            fileMsg.setDuration(cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)));
            fileMsg.setEndTime(TimeUtils.timeStamp2Date(String.valueOf((cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))+cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))/1000)),"yyMMddHHmmss" ));
            LogUtils.d("----------------------parseLong is: " +(cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))+cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION)) ));
            LogUtils.d("----------------------endTime  is: " +TimeUtils.timeStamp2Date(String.valueOf((cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED))+cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media.DURATION))/1000)),"yyMMddHHmmss" ));
            fileMsgList.add(fileMsg) ;
            cursor.moveToNext();
        }
        cursor.close();
        return fileMsgList ;
    }
    /**
     * 获取SD卡音频文件
     * @param mResolver
     */
    public static void getNativeAudio(ContentResolver mResolver){

        Cursor cursor = mResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
        cursor.moveToFirst();
        int counter = cursor.getCount();
        String title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE));

        LogUtils.d("------------before looping, title = " + title);
        for(int j = 0 ; j < counter; j++){
            LogUtils.d("-----------title = "
                    + cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)));
            cursor.moveToNext();
            LogUtils.d("-----------path = "+ cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA)));
            cursor.moveToNext();

        }
        cursor.close();
    }

}
