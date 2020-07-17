package com.frank.live;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

//public class LiveApplication extends Application {
//
//    private static LiveApplication context;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        context = this;
//    }
//
//    public static LiveApplication getInstance() {
//        return context;
//    }
//
//}
public class LiveApplication extends ContentProvider {

    private static Context context;

    @Override
    public boolean onCreate() {
//        super.onCreate();
        context = getContext();
        return true ;
    }


    public static Context getInstance() {
        return context;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }


}


//public class ApplicationProvider extends ContentProvider {
//    public static Context context ;
//    @Override
//    public boolean onCreate() {
//        context = getContext() ;
//        return true;
//    }
//
//    @Override
//    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
//        return null;
//    }
//
//    @Override
//    public String getType(Uri uri) {
//        return null;
//    }
//
//    @Override
//    public Uri insert(Uri uri, ContentValues values) {
//        return null;
//    }
//
//    @Override
//    public int delete(Uri uri, String selection, String[] selectionArgs) {
//        return 0;
//    }
//
//    @Override
//    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
//        return 0;
//    }
//
//
//
//    public void setContext(Context context) {
//        this.context = context;
//    }
//}

