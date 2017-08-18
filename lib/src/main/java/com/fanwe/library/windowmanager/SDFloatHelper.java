package com.fanwe.library.windowmanager;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * 悬浮view
 */
public class SDFloatHelper
{
    private View mContentView;

    private WeakReference<ViewGroup> mOriginalParent;
    private ViewGroup.LayoutParams mOriginalParams;
    private int mOriginalIndex = -1;

    private WindowManager.LayoutParams mWindowParams;
    private boolean mIsAddedToWindow;

    /**
     * 设置要悬浮的view
     *
     * @param view
     */
    public void setContentView(View view)
    {
        if (getContentView() != view)
        {
            saveViewInfo(view);
        }
    }

    /**
     * 还原悬浮view到原parent
     */
    public void restoreContentView()
    {
        ViewGroup parent = getOriginalParent();
        if (parent != null && mContentView != null)
        {
            addToWindow(false);
            parent.addView(mContentView, mOriginalIndex, mOriginalParams);
            saveViewInfo(null);
        }
    }

    /**
     * 返回悬浮的内容view
     *
     * @return
     */
    public View getContentView()
    {
        return mContentView;
    }

    /**
     * 返回WindowManager的LayoutParams
     *
     * @return
     */
    public WindowManager.LayoutParams getWindowParams()
    {
        if (mWindowParams == null)
        {
            mWindowParams = SDWindowManager.newLayoutParams();
        }
        return mWindowParams;
    }

    /**
     * 设置WindowManager的LayoutParams
     *
     * @param windowParams
     */
    public void setWindowParams(WindowManager.LayoutParams windowParams)
    {
        if (windowParams == null)
        {
            return;
        }
        mWindowParams = windowParams;
    }

    /**
     * 是否添加到Window
     *
     * @param add
     */
    public void addToWindow(boolean add)
    {
        final View view = getContentView();
        if (view == null)
        {
            return;
        }

        if (add)
        {
            if (!mIsAddedToWindow)
            {
                removeViewFromParent(view);
                SDWindowManager.getInstance().addView(view, getWindowParams());
                mIsAddedToWindow = true;
            }
        } else
        {
            SDWindowManager.getInstance().removeViewImmediate(view);
            mIsAddedToWindow = false;
        }
    }

    private static void removeViewFromParent(View view)
    {
        if (view == null)
        {
            return;
        }
        final ViewParent viewParent = view.getParent();
        if (viewParent == null || !(viewParent instanceof ViewGroup))
        {
            return;
        }
        ViewGroup viewGroup = (ViewGroup) viewParent;
        viewGroup.removeView(view);
    }

    /**
     * 是否已经被添加到Window
     *
     * @return
     */
    public boolean isAddedToWindow()
    {
        return mIsAddedToWindow;
    }

    private void saveViewInfo(View view)
    {
        mOriginalParent = null;
        mOriginalParams = null;
        mOriginalIndex = -1;

        mContentView = view;
        if (view != null)
        {
            final ViewParent viewParent = view.getParent();
            if (viewParent instanceof ViewGroup)
            {
                final ViewGroup viewGroup = (ViewGroup) viewParent;

                setOriginalParent(viewGroup);
                mOriginalParams = view.getLayoutParams();
                mOriginalIndex = viewGroup.indexOfChild(view);
            }
        }
    }

    private void setOriginalParent(ViewGroup viewGroup)
    {
        if (viewGroup != null)
        {
            mOriginalParent = new WeakReference<>(viewGroup);
        } else
        {
            mOriginalParent = null;
        }
    }

    private ViewGroup getOriginalParent()
    {
        if (mOriginalParent != null)
        {
            return mOriginalParent.get();
        }
        return null;
    }
}
