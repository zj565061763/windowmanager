package com.sd.lib.windowmanager;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.lang.ref.WeakReference;

class ViewStoreHelper
{
    private WeakReference<View> mView;
    private WeakReference<ViewGroup> mParent;
    private ViewGroup.LayoutParams mParams;
    private int mIndex = -1;

    /**
     * 保存view的信息
     *
     * @param view
     */
    public void save(View view)
    {
        reset();

        if (view != null)
        {
            mView = new WeakReference<>(view);

            mParams = view.getLayoutParams();
            final ViewParent parent = view.getParent();
            if (parent != null && parent instanceof ViewGroup)
            {
                final ViewGroup viewGroup = (ViewGroup) parent;

                mParent = new WeakReference<>(viewGroup);
                mIndex = viewGroup.indexOfChild(view);
            }
        }
    }

    /**
     * 把View还原到原先的父容器
     */
    public void restore()
    {
        restoreTo(getParent());
    }

    /**
     * 把View还原到某个容器
     *
     * @param viewGroup
     */
    public void restoreTo(ViewGroup viewGroup)
    {
        if (viewGroup == null)
        {
            return;
        }
        final View view = getView();
        if (view == null)
        {
            return;
        }
        if (view.getParent() == viewGroup)
        {
            return;
        }

        removeViewFromParent(view);
        viewGroup.addView(view, mIndex, mParams);
    }

    private void reset()
    {
        mView = null;
        mParent = null;
        mParams = null;
        mIndex = -1;
    }

    public View getView()
    {
        return mView == null ? null : mView.get();
    }

    public ViewGroup getParent()
    {
        return mParent == null ? null : mParent.get();
    }

    public ViewGroup.LayoutParams getParams()
    {
        return mParams;
    }

    public int getIndex()
    {
        return mIndex;
    }

    /**
     * 把View从它的Parent上移除
     *
     * @param view
     */
    public static void removeViewFromParent(View view)
    {
        if (view == null)
        {
            return;
        }

        final ViewParent parent = view.getParent();
        if (parent != null && parent instanceof ViewGroup)
        {
            ViewGroup viewGroup = (ViewGroup) parent;
            viewGroup.removeView(view);
        }
    }
}
