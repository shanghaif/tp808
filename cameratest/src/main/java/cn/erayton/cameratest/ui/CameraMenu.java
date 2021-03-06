package cn.erayton.cameratest.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;

import cn.erayton.cameratest.Config;
import cn.erayton.cameratest.callback.MenuInfo;
import cn.erayton.cameratest.data.CamListPreference;
import cn.erayton.cameratest.data.PrefListAdapter;
import cn.erayton.cameratest.data.PreferenceGroup;
import cn.erayton.cameratest.data.SubPrefListAdapter;
import cn.erayton.cameratest.manager.CameraSettings;
import cn.erayton.cameratest.utils.XmlInflater;

public class CameraMenu extends CameraBaseMenu {
    public static final String TAG = Config.getTag(CameraMenu.class);
    private CameraSubMenu mSubMenu;
    private Context mContext;
    private OnMenuClickListener mMenuClickListener;
    private MenuInfo mMenuInfo;
    private PrefListAdapter mAdapter;

    public CameraMenu(Context context, int resId, MenuInfo info) {
        super(context);
        mContext = context ;
        mMenuInfo = info ;
        XmlInflater xmlInflater = new XmlInflater(context) ;
        mAdapter = new PrefListAdapter(context, xmlInflater.inflate(resId)) ;
        updateAllMenuIcon();
        mAdapter.setClickListener(mMenuListener) ;
        recycleView.setAdapter(mAdapter) ;
    }

    private void updateAllMenuIcon() {
        PreferenceGroup group = mAdapter.getPrefGroup();
        for (int i = 0; i < group.size(); i++) {
            updateMenuIcon(i);
        }
    }
    /**
     * Find icon preference and notify update item
     * @param position used for get CamListPreference
     */
    private void updateMenuIcon(int position) {
        if (position < 0) { return; }
        CamListPreference preference = mAdapter.getPrefGroup().get(position);
        switch (preference.getKey()) {
            case CameraSettings.KEY_SWITCH_CAMERA:
                updateIcon(preference, mMenuInfo.getCurrentCameraId());
                break;
            case CameraSettings.KEY_FLASH_MODE:
                updateIcon(preference, mMenuInfo.getCurrentValue(preference.getKey()));
                break;
            default:
                break;
        }
        mAdapter.notifyItemChanged(position);
    }

    /**
     * Find correct icon in icon list by currentValue
     * @param preference which icon need update
     * @param currentValue current value of this preference stored in shared pref
     */
    private void updateIcon(CamListPreference preference, String currentValue) {
        int index = getIndex(preference.getEntryValues(), currentValue);
        if (index < preference.getEntryIcons().length && index >= 0) {
            preference.setIcon(preference.getEntryIcons()[index]);
        }
    }

    public View getView() {
        return recycleView;
    }

    public void setOnMenuClickListener(OnMenuClickListener listener) {
        mMenuClickListener = listener;
    }

    /**
     * Camera menu click listener
     */
    private PrefListAdapter.PrefClickListener mMenuListener =
            new PrefListAdapter.PrefClickListener() {
                @Override
                public void onClick(View view, int position, CamListPreference preference) {
                    // if is switch menu click, no need show sub menu
                    if (preference.getKey().equals(CameraSettings.KEY_SWITCH_CAMERA)) {
                        if (mMenuClickListener != null) {
                            mMenuClickListener.onMenuClick(preference.getKey(), null);
                            updateMenuIcon(position);
                        }
                        return;
                    }
                    if (mSubMenu == null) {
                        mSubMenu = new CameraSubMenu(mContext, preference);
                        mSubMenu.setItemClickListener(mItemClickListener);
                    }
                    mSubMenu.notifyDataSetChange(preference, mMenuInfo);
                    mSubMenu.show(view, 0, view.getHeight());
                }
            };

    /**
     * Camera sub menu click listener
     */
    private SubPrefListAdapter.PrefItemClickListener mItemClickListener =
            new SubPrefListAdapter.PrefItemClickListener() {
                @Override
                public void onItemClick(String key, String value) {
                    Log.d(TAG, "sub menu click key:" + key + " value:" + value);
                    if (mMenuClickListener != null) {
                        mMenuClickListener.onMenuClick(key, value);
                    }
                    // after menu value change, update icon
                    if (key.equals(CameraSettings.KEY_FLASH_MODE)) {
                        mSubMenu.close();
                        int position = mAdapter.getPrefGroup().find(key);
                        updateMenuIcon(position);
                    }
                }
            };

    public void close() {
        if (mSubMenu != null) {
            mSubMenu.close();
        }
    }

}
