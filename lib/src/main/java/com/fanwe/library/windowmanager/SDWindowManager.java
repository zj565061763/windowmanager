package com.fanwe.library.windowmanager;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

/**
 * Created by Administrator on 2016/9/19.
 */
public class SDWindowManager
{
    private static SDWindowManager sInstance;

    private Context mContext;
    private WeakHashMap<View, Integer> mMapView = new WeakHashMap<>();

    private SDWindowManager()
    {
    }

    public static SDWindowManager getInstance()
    {
        if (sInstance == null)
        {
            synchronized (SDWindowManager.class)
            {
                if (sInstance == null)
                {
                    sInstance = new SDWindowManager();
                }
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
        {
            mContext = context.getApplicationContext();
        }
    }

    private void init(View view)
    {
        if (view == null)
        {
            return;
        }
        Context context = view.getContext();
        if (context == null)
        {
            return;
        }
        init(context);
    }

    private void checkContext()
    {
        if (mContext == null)
        {
            throw new NullPointerException("mContext is null, you must call init(context) before this");
        }
    }

    private WindowManager getWindowManager()
    {
        checkContext();
        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        return windowManager;
    }

    public static WindowManager.LayoutParams newLayoutParams()
    {
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;

        params.type = WindowManager.LayoutParams.TYPE_TOAST;

        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.format = PixelFormat.TRANSLUCENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;

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
        if (view == null)
        {
            return;
        }
        init(view);
        if (params == null)
        {
            params = newLayoutParams();
        }

        getWindowManager().addView(view, params);
        mMapView.put(view, 0);
    }

    /**
     * 更新view的参数
     *
     * @param view
     * @param params
     */
    public void updateViewLayout(View view, ViewGroup.LayoutParams params)
    {
        if (view == null || params == null)
        {
            return;
        }
        init(view);

        getWindowManager().updateViewLayout(view, params);
    }

    /**
     * 移除view
     *
     * @param view
     */
    public void removeView(View view)
    {
        if (view == null)
        {
            return;
        }
        init(view);

        getWindowManager().removeView(view);
        mMapView.remove(view);
    }

    /**
     * 立即移除view
     *
     * @param view
     */
    public void removeViewImmediate(View view)
    {
        if (view == null)
        {
            return;
        }
        init(view);

        getWindowManager().removeViewImmediate(view);
        mMapView.remove(view);
    }

    /**
     * 移除指定class的所有view
     *
     * @param clazz
     */
    public void removeView(Class clazz)
    {
        List<View> list = getView(clazz);
        if (list != null && !list.isEmpty())
        {
            for (View item : list)
            {
                removeView(item);
            }
        }
    }

    /**
     * 返回等于指定class的所有view
     *
     * @param clazz
     * @return
     */
    public List<View> getView(Class clazz)
    {
        if (clazz == null || mMapView.isEmpty())
        {
            return null;
        }

        List<View> list = new ArrayList<>();
        for (Map.Entry<View, Integer> item : mMapView.entrySet())
        {
            View view = item.getKey();
            if (view != null && view.getClass() == clazz)
            {
                list.add(view);
            }
        }
        return list;
    }

    /**
     * 返回等于指定class的第一个view
     *
     * @param clazz
     * @return
     */
    public View getFirstView(Class clazz)
    {
        if (clazz == null || mMapView.isEmpty())
        {
            return null;
        }

        for (Map.Entry<View, Integer> item : mMapView.entrySet())
        {
            View view = item.getKey();
            if (view != null && view.getClass() == clazz)
            {
                return view;
            }
        }
        return null;
    }

    /**
     * 是否有指定class的view
     *
     * @param clazz
     * @return
     */
    public boolean hasView(Class clazz)
    {
        return getFirstView(clazz) != null;
    }

}
