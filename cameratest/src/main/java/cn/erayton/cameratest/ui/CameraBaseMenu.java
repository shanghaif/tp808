package cn.erayton.cameratest.ui;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public abstract class CameraBaseMenu {
    protected RecyclerView recycleView;

    public interface OnMenuClickListener {
        void onMenuClick(String key, String value);
    }

    protected CameraBaseMenu(Context context) {
        recycleView = new RecyclerView(context) ;
        RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(
                RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT) ;
        recycleView.setLayoutParams(params);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(1,
                StaggeredGridLayoutManager.HORIZONTAL) ;
        recycleView.setLayoutManager(manager);
        recycleView.setHasFixedSize(true);

    }

    <T> int getIndex(T[] lists, T value) {
        for (int i = 0; i < lists.length; i++) {
            if (lists[i].equals(value)) {
                return i;
            }
        }
        return -1;
    }

}
