package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2017/8/17.
 */

public class SDFloatView extends FrameLayout
{
    public SDFloatView(Context context)
    {
        super(context);
    }

    private View mContentView;

    private WeakReference<ViewGroup> mOriginalParent;
    private ViewGroup.LayoutParams mOriginalParams;
    private int mOriginalIndex = -1;

    /**
     * 设置要悬浮的view
     *
     * @param view
     */
    public void setContentView(View view)
    {
        removeAllViews();
        saveViewInfo(view);
        if (view == null)
        {
            return;
        }
        addView(view);
    }

    /**
     * 设置要悬浮的view
     *
     * @param view
     * @param params
     */
    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        removeAllViews();
        saveViewInfo(view);
        if (view == null)
        {
            return;
        }
        addView(view, params);
    }

    /**
     * 设置要悬浮的view
     *
     * @param layoutId
     */
    public void setContentView(int layoutId)
    {
        removeAllViews();
        saveViewInfo(null);
        LayoutInflater.from(getContext()).inflate(layoutId, this, true);
    }

    /**
     * 还原view到原parent
     */
    public void restoreContentView()
    {
        ViewGroup parent = getOriginalParent();
        if (parent != null && mContentView != null)
        {
            removeView(mContentView);
            parent.addView(mContentView, mOriginalIndex, mOriginalParams);
            saveViewInfo(null);
        }
    }

    private void saveViewInfo(View view)
    {
        mOriginalParent = null;
        mOriginalParams = null;
        mOriginalIndex = -1;

        mContentView = view;
        if (view != null)
        {
            ViewParent viewParent = view.getParent();
            if (viewParent instanceof ViewGroup)
            {
                ViewGroup viewGroup = (ViewGroup) viewParent;

                setOriginalParent(viewGroup);
                mOriginalParams = view.getLayoutParams();
                mOriginalIndex = viewGroup.indexOfChild(view);

                viewGroup.removeView(view);
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
