package cn.erayton.cameratest.ui;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import cn.erayton.cameratest.R;
import cn.erayton.cameratest.callback.MenuInfo;
import cn.erayton.cameratest.data.CamListPreference;
import cn.erayton.cameratest.data.SubPrefListAdapter;
import cn.erayton.cameratest.manager.CameraSettings;

public class CameraSubMenu  extends CameraBaseMenu{

    private SubPrefListAdapter mAdapter;
    private PopupWindow mPopWindow;

    public CameraSubMenu(Context context, CamListPreference preference) {
        super(context);
        mAdapter = new SubPrefListAdapter(context, preference) ;
        recycleView.setAdapter(mAdapter);
        initPopWindow(context) ;
    }

    private void initPopWindow(Context context) {
        mPopWindow = new PopupWindow(context) ;
        mPopWindow.setContentView(recycleView);
        int color = context.getResources().getColor(R.color.pop_window_bg) ;
        mPopWindow.setBackgroundDrawable(new ColorDrawable(color));
        mPopWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        mPopWindow.setAnimationStyle(-1);
        mPopWindow.setOutsideTouchable(false);
    }

    public void setItemClickListener(SubPrefListAdapter.PrefItemClickListener listener) {
        mAdapter.setClickListener(listener);
    }

    public void notifyDataSetChange(CamListPreference preference, MenuInfo info){
        updatePreValueByMenuInfo(preference, info) ;
        mAdapter.updateDataSet(preference);

    }

    private void updatePreValueByMenuInfo(CamListPreference preference, MenuInfo info) {
        assert info != null ;
        switch (preference.getKey()){
            case CameraSettings.KEY_SWITCH_CAMERA:
                preference.setEntries(info.getCameraIdList());
                preference.setEntryValues(info.getCameraIdList());
                mAdapter.updateHighlightIndex(getIndex(info.getCameraIdList(),
                        info.getCurrentCameraId()), false);
                break;
            default:
                mAdapter.updateHighlightIndex(-1, false);
                break;
        }
    }


    public void show(View view, int xOffset, int yOffset) {
        if (!mPopWindow.isShowing()) {
            mPopWindow.showAtLocation(view, Gravity.TOP | Gravity.CENTER, xOffset, yOffset);
        } else  {
            mPopWindow.dismiss();
        }
    }

    public void close() {
        if (mPopWindow != null && mPopWindow.isShowing()) {
            mPopWindow.dismiss();
        }
    }
}
