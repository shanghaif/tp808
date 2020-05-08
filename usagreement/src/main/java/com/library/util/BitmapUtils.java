package com.library.util;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

public class BitmapUtils {

    public static Bitmap getVideoThumbnail(String path) {
        return ThumbnailUtils.createVideoThumbnail(
                path, MediaStore.Video.Thumbnails.MICRO_KIND);
    }
}
