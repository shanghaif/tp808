package cn.erayton.cameratest;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction ;
import cn.erayton.cameratest.fragment.CameraFragment;
import cn.erayton.cameratest.fragment.SettingFragment;
import cn.erayton.cameratest.manager.Controller;
import cn.erayton.cameratest.utils.Permission;
import cn.erayton.cameratest.utils.PermissionDialog;

public class MainActivity extends AppCompatActivity {

    private static final String SETTING_ACTION = "acction.setting" ;
    private CameraFragment cameraFragment ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFlag() ;
        setContentView(R.layout.activity_main);

    }

    private boolean isSettingShortcut(){
        return SETTING_ACTION.equals(getIntent().getAction());
    }

    private void setWindowFlag() {
        Window window = getWindow() ;
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        WindowManager.LayoutParams params = window.getAttributes() ;
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        window.setAttributes(params);
    }

    private Controller getController(){
        return cameraFragment.getController() ;
    }

    private void initCameraFragment(){
        if (cameraFragment == null){
            cameraFragment = new CameraFragment() ;
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction() ;
            transaction.replace(R.id.app_roots, cameraFragment);
            transaction.commit();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Permission.checkPermission(this) ;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Permission.isPermissionGranted(this)){
            initCameraFragment();
            if (isSettingShortcut()){
                addSettingFragment() ;
                getIntent().setAction(null) ;
            }

        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.REQUEST_CODE){
            for (int result:grantResults){
                if (result != PackageManager.PERMISSION_GRANTED){
                    showPermissionDenyDialog() ;
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void showPermissionDenyDialog() {
        PermissionDialog dialog = new PermissionDialog() ;
        dialog.show(getSupportFragmentManager(), ACCESSIBILITY_SERVICE);
    }


    private void addSettingFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction() ;
        transaction.replace(R.id.app_roots, new SettingFragment(),
                SettingFragment.class.getSimpleName()) ;
        transaction.addToBackStack(SettingFragment.class.getSimpleName()) ;
        transaction.commit() ;

    }

    private void removeSettingFragment() {
        String tag = SettingFragment.class.getSimpleName() ;
        Fragment settingFragment = getSupportFragmentManager().findFragmentByTag(tag) ;
        if (settingFragment == null){
            return;
        }
        if (settingFragment.isAdded()){
            getSupportFragmentManager().popBackStack(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }

    //    protected static final String TAG = MainActivity.class.getName() ;
//
//    public static final String KEY_PICTURE_SIZE = "pref_picture_size";
//    public static final String KEY_PREVIEW_SIZE = "pref_preview_size";
//    public static final String KEY_CAMERA_ID = "pref_camera_id";
//    public static final String KEY_MAIN_CAMERA_ID = "pref_main_camera_id";
//    public static final String KEY_AUX_CAMERA_ID = "pref_aux_camera_id";
//    public static final String KEY_PICTURE_FORMAT = "pref_picture_format";
//    public static final String KEY_RESTART_PREVIEW = "pref_restart_preview";
//    public static final String KEY_SWITCH_CAMERA = "pref_switch_camera";
//    public static final String KEY_FLASH_MODE = "pref_flash_mode";
//    public static final String KEY_ENABLE_DUAL_CAMERA = "pref_enable_dual_camera";
//    public static final String KEY_SUPPORT_INFO = "pref_support_info";
//    public static final String KEY_VIDEO_ID = "pref_video_camera_id";
//    public static final String KEY_VIDEO_SIZE = "pref_video_size";
//
//
//    Button button, uploadBtn ;
//    TextView textView;
//    EditText hostET, nameET, passET, savePathET, saveNameET ;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        initView() ;
////        getStor() ;
//    }
//
//
//
//    private SurfaceTexture mTexture ;
//    private void initView() {
//        mSharedPreference = PreferenceManager.getDefaultSharedPreferences(this);
//        textView = findViewById(R.id.fileselect_tv) ;
//        button = findViewById(R.id.fileselect_btn) ;
//        uploadBtn = findViewById(R.id.fileupload_btn) ;
//        hostET = findViewById(R.id.ftp_host) ;
//        nameET = findViewById(R.id.ftp_name) ;
//        passET = findViewById(R.id.ftp_pass) ;
//        savePathET = findViewById(R.id.ftp_savepath) ;
//        saveNameET = findViewById(R.id.ftp_savename) ;
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//                //intent.setType(“image/*”);//选择图片
//                //intent.setType(“audio/*”); //选择音频
//                //intent.setType(“video/*”); //选择视频 （mp4 3gp 是android支持的视频格式）
//                //intent.setType(“video/*;image/*”);//同时选择视频和图片
//                intent.setType("*/*");//无类型限制
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                startActivityForResult(intent, 1);
//            }
//        });
//        uploadBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FTPUtils.getInstance().initFtpClient(hostET.getText().toString(), 2121,
//                        nameET.getText().toString(), passET.getText().toString());
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
////                        FTPUtils.getInstance().connectFtp() ;
//
//                FTPUtils.getInstance().uploadFile(savePathET.getText().toString(),
//                        saveNameET.getText().toString(), textView.getText().toString());
//                    }
//                }).start();
////                FTPUtils.getInstance().uploadFile(savePathET.getText().toString(),
////                        saveNameET.getText().toString(), textView.getText().toString());
//            }
//        });
//    }
//
//    String path ;
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode == Activity.RESULT_OK){
//            Uri uri = data.getData() ;
//            if ("file".equalsIgnoreCase(uri.getScheme())){
//                path = uri.getPath() ;
//                textView.setText(path);
//                return;
//            }
//
//            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT){
//                path = getPath(this, uri) ;
////                textView.setText(path);
//            }else {
//                path = getRealPathFromURI(uri) ;
////                textView.setText(path);
//            }
//            textView.setText(path);
//        }
//
//    }
//
//    /**
//     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
//     */
//    @SuppressLint("NewApi")
//    public String getPath(final Context context, final Uri uri) {
//
//        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
//
//        // DocumentProvider
//        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
//            // ExternalStorageProvider
//            if (isExternalStorageDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                if ("primary".equalsIgnoreCase(type)) {
//                    return Environment.getExternalStorageDirectory() + "/" + split[1];
//                }
//            }
//            // DownloadsProvider
//            else if (isDownloadsDocument(uri)) {
//
//                final String id = DocumentsContract.getDocumentId(uri);
//                final Uri contentUri = ContentUris.withAppendedId(
//                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
//
//                return getDataColumn(context, contentUri, null, null);
//            }
//            // MediaProvider
//            else if (isMediaDocument(uri)) {
//                final String docId = DocumentsContract.getDocumentId(uri);
//                final String[] split = docId.split(":");
//                final String type = split[0];
//
//                Uri contentUri = null;
//                if ("image".equals(type)) {
//                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//                } else if ("video".equals(type)) {
//                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
//                } else if ("audio".equals(type)) {
//                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//                }
//
//                final String selection = "_id=?";
//                final String[] selectionArgs = new String[]{split[1]};
//
//                return getDataColumn(context, contentUri, selection, selectionArgs);
//            }
//        }
//        // MediaStore (and general)
//        else if ("content".equalsIgnoreCase(uri.getScheme())) {
//            return getDataColumn(context, uri, null, null);
//        }
//        // File
//        else if ("file".equalsIgnoreCase(uri.getScheme())) {
//            return uri.getPath();
//        }
//        return null;
//    }
//
////    private void getStor(){
////        ArrayList<StorageUtils.Volume> list_volume = StorageUtils.getVolume(this);
////        for (int i=0;i<list_volume.size();i++){
////            Log.e("FTP","path:"+list_volume.get(i).getPath()+"----"+
////                    "removable:"+list_volume.get(i).isRemovable()+"---"+
////                    "state:"+list_volume.get(i).getState());
////        }
////        Log.e("FTP", "size:"+getSDAllSize()+","+Environment.isExternalStorageRemovable()) ;
////    }
////
////    public long getSDAllSize(){
////        //取得SD卡文件路径
////        File path = Environment.getExternalStorageDirectory();
////        StatFs sf = new StatFs(path.getPath());
////        //获取单个数据块的大小(Byte)
////        long blockSize = sf.getBlockSize();
////        //获取所有数据块数
////        long allBlocks = sf.getBlockCount();
////        //返回SD卡大小
////        //return allBlocks * blockSize; //单位Byte
////        //return (allBlocks * blockSize)/1024; //单位KB
////        return (allBlocks * blockSize)/1024/1024; //单位MB
////    }
//
//
//
//    /**
//     * Get the value of the data column for this Uri. This is useful for
//     * MediaStore Uris, and other file-based ContentProviders.
//     *
//     * @param context       The context.
//     * @param uri           The Uri to query.
//     * @param selection     (Optional) Filter used in the query.
//     * @param selectionArgs (Optional) Selection arguments used in the query.
//     * @return The value of the _data column, which is typically a file path.
//     */
//    public String getDataColumn(Context context, Uri uri, String selection,
//                                String[] selectionArgs) {
//
//        Cursor cursor = null;
//        final String column = "_data";
//        final String[] projection = {column};
//
//        try {
//            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
//                    null);
//            if (cursor != null && cursor.moveToFirst()) {
//                final int column_index = cursor.getColumnIndexOrThrow(column);
//                return cursor.getString(column_index);
//            }
//        } finally {
//            if (cursor != null)
//                cursor.close();
//        }
//        return null;
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is ExternalStorageProvider.
//     */
//    public boolean isExternalStorageDocument(Uri uri) {
//        return "com.android.externalstorage.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is DownloadsProvider.
//     */
//    public boolean isDownloadsDocument(Uri uri) {
//        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
//    }
//
//    /**
//     * @param uri The Uri to check.
//     * @return Whether the Uri authority is MediaProvider.
//     */
//    public boolean isMediaDocument(Uri uri) {
//        return "com.android.providers.media.documents".equals(uri.getAuthority());
//    }
//
//    private String getRealPathFromURI(Uri uri) {
//        String res = null ;
//        String[] proj = { MediaStore.Images.Media.DATA } ;
//        Cursor cursor = getContentResolver().query(uri, proj, null, null, null) ;
//        if (null != cursor && cursor.moveToFirst()){
//            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//            res = cursor.getString(column_index);
//            cursor.close();
//        }
//
//
//        return res;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        release();
//    }
//
//
//
//
//
//    private CameraDevice cameraDevice ;
//
//    @RequiresApi(api = Build.VERSION_CODES.M)
//    private void init() {
//        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    Activity#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for Activity#requestPermissions for more details.
//            return;
//        }
//        try {
//            assert cameraManager != null;
//            cameraManager.openCamera("0", stateCallback, handler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            cameraDevice.createCaptureSession(setOutputSize(cameraDevice.getId(), mTexture),
//                    sessionStateCb, handler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    CameraCaptureSession.StateCallback sessionStateCb = new CameraCaptureSession.StateCallback() {
//        @Override
//        public void onConfigured(CameraCaptureSession session) {
//            captureSession = session ;
//        }
//
//        @Override
//        public void onConfigureFailed(CameraCaptureSession session) {
//            Log.d(TAG, "create session fail id:" + session.getDevice().getId());
//        }
//    } ;
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//
//    Handler handler = new Handler(Looper.getMainLooper()) ;
//
//    CameraDevice.StateCallback stateCallback = new CameraDevice.StateCallback() {
//
//        @Override
//        public void onOpened(CameraDevice device) {
//            cameraDevice = device ;
//        }
//
//        @Override
//        public void onDisconnected(CameraDevice cameraDevice) {
//            cameraDevice.close();
//        }
//
//        @Override
//        public void onError(CameraDevice cameraDevice, int i) {
//            cameraDevice.close();
//        }
//    } ;
//
//
//    CameraCharacteristics characteristics ;
//    ImageReader imageReader ;
//    CameraCaptureSession captureSession ;
//    private List<Surface> setOutputSize(String id, SurfaceTexture texture){
//
//        StreamConfigurationMap map = characteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP) ;
//        String picKey = KEY_PICTURE_SIZE;
//        String preKey = KEY_PREVIEW_SIZE;
//        String formatKey = KEY_PICTURE_FORMAT;
//
////        get value from SharedPreferences
//
//        int format = getPicFormat(id, formatKey);
//        Size previewSize = getPreviewSize(id, preKey, map);
//        texture.setDefaultBufferSize(previewSize.getWidth(), previewSize.getHeight());
//        Size pictureSize = getPictureSize(id, picKey, map, format);
//
//        Surface surface = new Surface(texture) ;
//        if (imageReader != null){
//            imageReader.close();
//            imageReader = null ;
//        }
//        imageReader = ImageReader.newInstance(pictureSize.getWidth(), pictureSize.getHeight(),
//                format, 1) ;
//        imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader reader) {
//                reader.getWidth() ;
//                reader.getHeight();
//                Log.d("cjh", "imageReader.setOnImageAvailableListener---"+
//                    ",width:"+reader.getWidth() +
//                    ",height:"+reader.getHeight()
//                );
////                mCallback.onDataBack(getByteFromReader(reader),
////                        reader.getWidth(), reader.getHeight());
//            }
//        }, null);
//
//        Size uiSize = getPreviewUiSize(this, previewSize);
////        mCallback.onViewChange(uiSize.getHeight(), uiSize.getWidth());
//        return Arrays.asList(surface, imageReader.getSurface()) ;
//    }
//
//    private void release(){
//        releaseImageReader();
//        releaseCameraCaptureSession();
//    }
//
//    private void releaseImageReader(){
//        assert imageReader != null ;
//        imageReader.close();
//        imageReader = null ;
//    }
//    private void releaseCameraCaptureSession(){
//        assert captureSession != null ;
//        captureSession.close();
//        captureSession = null ;
//    }
//
//
//    public static Size getPreviewUiSize(Context context, Size previewSize) {
//        WindowManager windowManager = (WindowManager) context.getSystemService(Context
//                .WINDOW_SERVICE);
//        DisplayMetrics metrics = new DisplayMetrics();
//        windowManager.getDefaultDisplay().getMetrics(metrics);
//        double ratio = previewSize.getWidth() / (double) previewSize.getHeight();
//        int w = (int) Math.ceil(metrics.widthPixels * ratio);
//        int h = metrics.widthPixels;
//        return new Size(w, h);
//    }
//
//
//    public static final String IMAGE_FORMAT = String.valueOf(ImageFormat.JPEG);
//    public int getPicFormat(String id, String key) {
//        return Integer.parseInt(getValueFromPref(id, key, IMAGE_FORMAT));
//    }
//
//    private static final ArrayList<String> SPEC_KEY = new ArrayList<>(3);
//    private SharedPreferences mSharedPreference;
//    public String getValueFromPref(String cameraId, String key, String defaultValue) {
//        SharedPreferences preferences;
//        if (!SPEC_KEY.contains(key)) {
//            preferences = mSharedPreference;
//        } else {
//            preferences = getSharedPrefById(cameraId);
//        }
//        return preferences.getString(key, defaultValue);
//    }
//
//
//    public static final String NULL_VALUE = "SharedPreference No Value";
//
//    public static final String SPLIT_TAG = "x";
//    private Point mRealDisplaySize = new Point();
//    public Size getPreviewSize(String id, String key, StreamConfigurationMap map) {
//        String preStr = getValueFromPref(id, key, NULL_VALUE);
//        if (NULL_VALUE.equals(preStr)) {
//            // preference not set, use default value
//            return getDefaultPreviewSize(map, mRealDisplaySize);
//        } else {
//            String[] size = preStr.split(SPLIT_TAG);
//            return new Size(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
//        }
//    }
//
//    public Size getPictureSize(String id, String key, StreamConfigurationMap map, int format) {
//        String picStr = getValueFromPref(id, key, NULL_VALUE);
//        if (NULL_VALUE.equals(picStr)) {
//            // preference not set, use default value
//            return getDefaultPictureSize(map, format);
//        } else {
//            String[] size = picStr.split(SPLIT_TAG);
//            return new Size(Integer.parseInt(size[0]), Integer.parseInt(size[1]));
//        }
//    }
//
//
//    /**
//     * get related shared preference by camera id
//     * @param cameraId valid camera id
//     * @return related SharedPreference from the camera id
//     */
//    public SharedPreferences getSharedPrefById(String cameraId) {
//        return getSharedPreferences(getSharedPrefName(cameraId), Context.MODE_PRIVATE);
//    }
//
//    private String getSharedPrefName(String cameraId) {
//        return getPackageName() + "_camera_" + cameraId;
//    }
//
//
//    /**
//     * get default preview size from supported preview size list
//     * @param map specific stream configuration map used for get supported preview size
//     * @param displaySize devices screen size
//     * @return preview size equals or less than screen size
//     */
//    public static Size getDefaultPreviewSize(StreamConfigurationMap map, Point displaySize) {
//        Size[] supportSize = map.getOutputSizes(SurfaceTexture.class);
//        sortCamera2Size(supportSize);
//        for (Size size : supportSize) {
//            if (!ratioMatched(size)) {continue;}
//            if ((size.getHeight() == displaySize.x)
//                    || (size.getWidth() <= displaySize.y && size.getHeight() <= displaySize.x)) {
//                return size;
//            }
//        }
//        return supportSize[0];
//    }
//
//    public static boolean ratioMatched(Size size) {
//        return size.getWidth() * 3 == size.getHeight() * 4;
//    }
//    private static void sortCamera2Size(Size[] sizes) {
//        Comparator<Size> comparator = new Comparator<Size>() {
//            @Override
//            public int compare(Size o1, Size o2) {
//                return o2.getWidth() * o2.getHeight() - o1.getWidth() * o1.getHeight();
//            }
//        };
//        Arrays.sort(sizes, comparator);
//    }
//
//    /**
//     * get default picture size from support picture size list
//     * @param map specific stream configuration map used for get supported picture size
//     * @param format used for get supported picture size list from map
//     * @return largest 4:3 picture size or largest size in supported list
//     */
//    public static Size getDefaultPictureSize(StreamConfigurationMap map, int format) {
//        Size[] supportSize = map.getOutputSizes(format);
//        sortCamera2Size(supportSize);
//        for (Size size : supportSize) {
//            if (ratioMatched(size)) {
//                return size;
//            }
//        }
//        return supportSize[0];
//    }

}
