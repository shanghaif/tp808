package cn.erayton.cameratest.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import cn.erayton.cameratest.R;
import cn.erayton.cameratest.utils.CameraUtil;
import cn.erayton.cameratest.utils.MediaFunc;

public class AppBaseUI implements View.OnClickListener {
    private CoverView mCoverView;
    private RelativeLayout mPreviewRootView;
    private ShutterButton mShutter;
    private ImageButton mSetting;
    private CircleImageView mThumbnail;
    private LinearLayout mBottomContainer;
    private FocusView mFocusView;
    private LinearLayout mMenuContainer;
    private CameraUiEvent mEvent;
    private IndicatorView mIndicatorView;

    private Point mDisplaySize;
    private int mVirtualKeyHeight;
    private int mTopBarHeight;

    public AppBaseUI(Context context, View rootView) {
        mCoverView = rootView.findViewById(R.id.cover_view);

        mPreviewRootView = rootView.findViewById(R.id.preview_root_view);
        mShutter = rootView.findViewById(R.id.btn_shutter);
        mShutter.setOnClickListener(this);
        mSetting = rootView.findViewById(R.id.btn_setting);
        mSetting.setOnClickListener(this);
        mBottomContainer = rootView.findViewById(R.id.bottom_container);
        mThumbnail = rootView.findViewById(R.id.thumbnail);
        mThumbnail.setOnClickListener(this);
        mMenuContainer = rootView.findViewById(R.id.menu_container);
        mIndicatorView = rootView.findViewById(R.id.indicator_view);

        mDisplaySize = CameraUtil.getDisplaySize(context);
        mVirtualKeyHeight = CameraUtil.getVirtualKeyHeight(context);
        mTopBarHeight = context.getResources().getDimensionPixelSize(R.dimen.menu_item_height);
        mFocusView = new FocusView(context);
        mFocusView.setVisibility(View.GONE);
        mPreviewRootView.addView(mFocusView);
    }

    public void setCameraUiEvent(CameraUiEvent mEvent) {
        this.mEvent = mEvent;
    }

    public RelativeLayout getRootView() {
        return mPreviewRootView;
    }

    public CoverView getCoverView() {
        return mCoverView;
    }

    public FocusView getFocusView() {
        return mFocusView;
    }

    public LinearLayout getmBottomView() {
        return mBottomContainer;
    }

    public IndicatorView getIndicatorView() {
        return mIndicatorView;
    }

    public void setMenuView(View view) {
        mMenuContainer.removeAllViews();
        mMenuContainer.addView(view);
    }

    public void setShutterMode(String mode) {
        mShutter.setMode(mode);
    }

    public void removeMenuView() {
        mMenuContainer.removeAllViews();
    }

    /**
     * Adjust layout when based on preview width
     *
     * @param width  preview screen width
     * @param height preview screen height
     */
    public void updateUiSize(int width, int height) {
        mFocusView.initFocusArea(width, height);
        int realHeight = mDisplaySize.y + mVirtualKeyHeight;
        int bottomHeight = CameraUtil.getBottomBarHeight(mDisplaySize.x);
        RelativeLayout.LayoutParams previewParams = new RelativeLayout.LayoutParams(width, height);
        RelativeLayout.LayoutParams bottomBarParams = (RelativeLayout.LayoutParams) mBottomContainer.getLayoutParams();
        int topMargin = 0;
        boolean needTopMargin = (height + 2 * mTopBarHeight) < realHeight;
        boolean needAlignCenter = width == height;
        if (needAlignCenter) {
            topMargin = (realHeight - mTopBarHeight - mVirtualKeyHeight - height) / 2;
        } else if (needTopMargin) {
            topMargin = mTopBarHeight;
        }
        int reserveHeight = realHeight - topMargin - height;
        boolean needAdjustBottomBar = reserveHeight > bottomHeight;
        if (needAdjustBottomBar) {
            bottomHeight = reserveHeight;
        }
        //  preview
        previewParams.setMargins(0, topMargin, 0, 0);
        mPreviewRootView.setLayoutParams(previewParams);
        //  bottom bar
        bottomBarParams.height = bottomHeight;
        mBottomContainer.setPadding(0, 0, 0, mVirtualKeyHeight);
        mBottomContainer.setLayoutParams(bottomBarParams);
    }

    /* should not call in main thread */
    public void updateThumbnail(Context context, Handler handler) {
        final Bitmap bitmap = MediaFunc.getThumb(context);
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (bitmap == null) {
                    mThumbnail.setClickable(false);
                    return;
                }
                mThumbnail.setImageBitmap(bitmap);
                mThumbnail.setClickable(true);
            }
        });
    }

    public void setThumbnail(Bitmap bitmap) {
        if (mThumbnail != null && bitmap != null) {
            mThumbnail.setImageBitmap(bitmap);
        }
    }

    public void setUIClickable(boolean clickable){
        mShutter.setClickable(clickable);
        mThumbnail.setClickable(clickable);
        mSetting.setClickable(clickable);
        if (mMenuContainer.getChildCount() > 0){
            mMenuContainer.getChildAt(0).setClickable(clickable);
        }
        mIndicatorView.setClickable(clickable);
    }

    @Override
    public void onClick(View v) {
        if (mEvent!=null){
            mEvent.onAction(CameraUiEvent.ACTION_CLICK, v);
        }
    }


    public void clickButtonShutter(){
        mShutter.performClick() ;
    }
}
