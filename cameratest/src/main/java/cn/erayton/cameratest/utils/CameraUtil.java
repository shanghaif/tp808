package cn.erayton.cameratest.utils;


import android.util.Size;

import java.util.Comparator;

public class CameraUtil {

    public static double RATIO_4X3 = 1.333333333;
    public static double RATIO_16X9 = 1.777777778;
    public static double ASPECT_TOLERANCE = 0.00001;
    public static final String SPLIT_TAG = "x";

    private static void sortCamera2Size(Size[] sizes){
        Comparator<Size> comparable = new Comparator<Size>() {
            @Override
            public int compare(Size o1, Size o2) {
                return 0;
            }
        }
    }
}
