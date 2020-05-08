package com.library.util;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import cn.com.erayton.usagreement.R;

public class MediaFunc {
    private static String TAG = "MediaFunc";
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int MEDIA_TYPE_YUV = 3;
    private static Uri mCurrentUri = null;


    /** 获取图片缩略图
     *
     * @param context
     * @param path  路径
     * @return
     */
    public static Bitmap getThumb(Context context, String path) {
        String selection = MediaStore.Images.Media.DATA + " like ?";
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                .getPath() + "/" + SAVE_PATH;
        String[] selectionArgs = {path + "%"};
        Uri originalUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(originalUri, null, selection, selectionArgs,
                MediaStore.Images.Media.DATE_TAKEN + " desc");
        Bitmap bitmap = null;
        if (cursor != null && cursor.moveToFirst()) {
            long thumbNailsId = cursor.getLong(cursor.getColumnIndex("_ID"));
            //generate uri
            mCurrentUri = Uri.parse("content://media/external/images/media/");
            mCurrentUri = ContentUris.withAppendedId(mCurrentUri, thumbNailsId);
            bitmap = MediaStore.Images.Thumbnails.getThumbnail(cr,
                    thumbNailsId, MediaStore.Images.Thumbnails.MICRO_KIND, null);
        }
        cursor.close();
        return bitmap;
    }

    /** 获取视频缩略图
     *
     * @param context
     * @param path  路径
     * @return
     */
    public static Bitmap getVideoThumb(Context context, String path) {
        String selection = MediaStore.Video.Media.DATA + " like ?";
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                .getPath() + "/" + SAVE_PATH;
        String[] selectionArgs = {path + "%"};
        Uri originalUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(originalUri, null, selection, selectionArgs,
                MediaStore.Video.Media.DATE_TAKEN + " desc");
        Bitmap bitmap = null;
        if (cursor != null && cursor.moveToFirst()) {
            long thumbNailsId = cursor.getLong(cursor.getColumnIndex("_ID"));
            //generate uri
            mCurrentUri = Uri.parse("content://media/external/video/media/");
            mCurrentUri = ContentUris.withAppendedId(mCurrentUri, thumbNailsId);
            bitmap = MediaStore.Video.Thumbnails.getThumbnail(cr,
                    thumbNailsId, MediaStore.Video.Thumbnails.MICRO_KIND, null);
        }
        cursor.close();
        return bitmap;
    }

    public static Cursor getAllImage(Context context, String path) {
        String selection = MediaStore.Images.Media.DATA + "" + " like ?";
//        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
//                .getPath() + "/" + SAVE_PATH;
        String[] selectionArgs = {path + "%"};
        Cursor c = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, selection, selectionArgs,
                MediaStore.Images.Media.DATE_TAKEN + " desc");
        if (c != null && c.moveToFirst()) {
            return c;
        } else {
            return null;
        }
    }

    public static void setCurrentUri(Uri uri) {
        mCurrentUri = uri;
    }

    /** 跳转到图片/视频位置
     *
     * @param context
     */
    public static void goToGallery(Context context) {
        if (mCurrentUri == null) {
            Log.e(TAG, "uri is null");
            return;
        }
        try {
            Intent intent = new Intent(Intent.ACTION_MAIN).setClassName(
                    "com.android.gallery3d", "com.android.gallery3d.app.GalleryActivity");
            intent.setAction(Intent.ACTION_VIEW);
            //intent.setDataAndType(uri,"image/*");
            intent.setData(mCurrentUri);
            context.startActivity(intent);
        } catch (Exception e) {
            Intent intent = new Intent(Intent.ACTION_VIEW, mCurrentUri);
            ComponentName componentName = intent.resolveActivity(context.getPackageManager());
            if (componentName == null) {
                Toast.makeText(context, R.string.open_file_error, Toast.LENGTH_LONG).show();
                return;
            }
            context.startActivity(intent);
        }
    }
}
