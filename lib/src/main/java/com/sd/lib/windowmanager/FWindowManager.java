package com.sd.lib.windowmanager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class FWindowManager
{
    private static FWindowManager sInstance;

    private Context mContext;
    private final Map<View, Integer> mMapView = new WeakHashMap<>();

    private FWindowManager()
    {
    }

    public static FWindowManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (FWindowManager.class)
            {
                if (sInstance == null)
                    sInstance = new FWindowManager();
            }
        }
        return sInstance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context)
    {
        if (mContext == null)
            mContext = context.getApplicationContext();
    }

    private void initContextIfNeed(View view)
    {
        if (view == null)
            return;

        final Context context = view.getContext();
        if (context == null)
            return;

        init(context);
    }

    private void checkContext()
    {
        if (mContext == null)
            throw new NullPointerException("mContext is null, you must call init(Context) before this");
    }

    private WindowManager getWindowManager()
    {
        checkContext();
        return (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    public static WindowManager.LayoutParams newLayoutParams()
    {
        final WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        if (Build.VERSION.SDK_INT >= 26)
        {
            params.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else
        {
            params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        }

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.format = PixelFormat.RGBA_8888;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;

        return params;
    }

    /**
     * 添加view到Window
     *
     * @param view
     * @param params
     */
    public void addView(View view, ViewGroup.LayoutParams params)
    {
        initContextIfNeed(view);

        if (containsView(view))
            return;

        if (params == null)
            params = newLayoutParams();

        getWindowManager().addView(view, params);

        view.removeOnAttachStateChangeListener(mInternalOnAttachStateChangeListener);
        view.addOnAttachStateChangeListener(mInternalOnAttachStateChangeListener);
        mMapView.put(view, 0);
    }

    private final View.OnAttachStateChangeListener mInternalOnAttachStateChangeListener = new View.OnAttachStateChangeListener()
    {
        @Override
        public void onViewAttachedToWindow(View v)
        {
        }

        @Override
        public void onViewDetachedFromWindow(View v)
        {
            v.removeOnAttachStateChangeListener(this);
            mMapView.remove(v);
        }
    };

    /**
     * 更新view的参数
     *
     * @param view
     * @param params
     */
    public void updateViewLayout(View view, ViewGroup.LayoutParams params)
    {
        initContextIfNeed(view);

        if (params == null)
            return;

        if (containsView(view))
            getWindowManager().updateViewLayout(view, params);
    }

    /**
     * 移除view
     *
     * @param view
     */
    public void removeView(View view)
    {
        initContextIfNeed(view);

        if (containsView(view))
            getWindowManager().removeView(view);
    }

    /**
     * 是否有指定的view
     *
     * @param view
     * @return
     */
    public boolean containsView(View view)
    {
        return mMapView.containsKey(view);
    }

    /**
     * 移除指定class的所有view
     *
     * @param clazz
     */
    public <T extends View> void removeView(Class<T> clazz)
    {
        final List<T> list = getView(clazz);
        for (View item : list)
        {
            getWindowManager().removeView(item);
        }
    }

    /**
     * 返回等于指定class的所有view
     *
     * @param clazz
     * @return
     */
    public <T extends View> List<T> getView(Class<T> clazz)
    {
        final List<T> list = new ArrayList<>();
        for (Map.Entry<View, Integer> item : mMapView.entrySet())
        {
            final View view = item.getKey();
            if (view.getClass() == clazz)
                list.add((T) view);
        }
        return list;
    }

    /**
     * 是否有指定class的view
     *
     * @param clazz
     * @return
     */
    public <T extends View> boolean containsView(Class<T> clazz)
    {
        for (Map.Entry<View, Integer> item : mMapView.entrySet())
        {
            final View view = item.getKey();
            if (view.getClass() == clazz)
                return true;
        }
        return false;
    }
}
