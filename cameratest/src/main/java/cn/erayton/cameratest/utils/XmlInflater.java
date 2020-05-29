package cn.erayton.cameratest.utils;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import cn.erayton.cameratest.Config;
import cn.erayton.cameratest.data.CamListPreference;
import cn.erayton.cameratest.data.PreferenceGroup;

public class XmlInflater {
    private static final String TAG = Config.getTag(XmlInflater.class);
    private static final Class<?>[] CTOR_SIGNATURE =
            new Class[] {Context.class, AttributeSet.class};
    private static final ArrayMap<String, Constructor<?>> sConstructorMap = new ArrayMap<>();
    private Context mContext;

    public XmlInflater(Context contextontext) {
        mContext = contextontext;
    }

    private CamListPreference getInstance(String tagName, Object[] args){
        Constructor<?> constructor = sConstructorMap.get(tagName) ;
        if (constructor == null){
            try {
                Class<?> clazz = mContext.getClassLoader().loadClass(tagName) ;
                constructor = clazz.getConstructor(CTOR_SIGNATURE) ;
                sConstructorMap.put(tagName, constructor) ;
            } catch (ClassNotFoundException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        try {
            assert constructor != null;
            return (CamListPreference) constructor.newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "init preference error");
        return null;
    }

    public PreferenceGroup inflate(int resId){

    }
}
