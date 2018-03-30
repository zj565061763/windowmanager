/*
 * Copyright (C) 2017 zhengjun, fanwe (http://www.fanwe.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fanwe.lib.windowmanager;

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
    private Map<View, Integer> mMapView = new WeakHashMap<>();

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
                {
                    sInstance = new FWindowManager();
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

    private void initContextIfNeed(View view)
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
            throw new NullPointerException("mContext is null, you must call init(Context) before this");
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

        int sdkInt = Build.VERSION.SDK_INT;
        if (sdkInt >= Build.VERSION_CODES.KITKAT && sdkInt <= Build.VERSION_CODES.N)
        {
            params.type = WindowManager.LayoutParams.TYPE_TOAST;
        } else
        {
            params.type = WindowManager.LayoutParams.TYPE_PHONE;
        }

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
        initContextIfNeed(view);
        if (containsView(view))
        {
            return;
        }

        if (params == null)
        {
            params = newLayoutParams();
        }
        getWindowManager().addView(view, params);

        view.removeOnAttachStateChangeListener(mInternalOnAttachStateChangeListener);
        view.addOnAttachStateChangeListener(mInternalOnAttachStateChangeListener);
        mMapView.put(view, 0);
    }

    private View.OnAttachStateChangeListener mInternalOnAttachStateChangeListener = new View.OnAttachStateChangeListener()
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
        {
            return;
        }

        if (containsView(view))
        {
            getWindowManager().updateViewLayout(view, params);
        }
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
        {
            getWindowManager().removeView(view);
        }
    }

    /**
     * 立即移除view
     *
     * @param view
     */
    public void removeViewImmediate(View view)
    {
        initContextIfNeed(view);

        if (containsView(view))
        {
            getWindowManager().removeViewImmediate(view);
        }
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
    public void removeView(Class clazz)
    {
        final List<View> list = getView(clazz);
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
            {
                list.add((T) view);
            }
        }
        return list;
    }

    /**
     * 是否有指定class的view
     *
     * @param clazz
     * @return
     */
    public boolean containsView(Class clazz)
    {
        for (Map.Entry<View, Integer> item : mMapView.entrySet())
        {
            final View view = item.getKey();
            if (view.getClass() == clazz)
            {
                return true;
            }
        }
        return false;
    }
}
