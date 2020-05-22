package cn.erayton.cameratest.ui;

import android.content.Context;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

public class IndicatorView extends View {

    private Point mDisplayPoint;

    interface IndicatorListener{
        void onPositionChanged(int index) ;
    }

    public IndicatorView(Context context) {
        this(context, null);
    }

    public IndicatorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public IndicatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttributeAndPaint(context, attrs, defStyleAttr) ;
        mDisplayPoint = CameraU

    }


}
