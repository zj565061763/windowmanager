package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * 悬浮view
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
            removeAllViews();
            saveViewInfo(view);
            if (view != null)
            {
                addView(view);
            }
        }
    }

    /**
     * 设置要悬浮的view
     *
     * @param view
     * @param params
     */
    public void setContentView(View view, ViewGroup.LayoutParams params)
    {
        if (getContentView() != view)
        {
            removeAllViews();
            saveViewInfo(view);
            if (view != null)
            {
                addView(view, params);
            }
        }
    }

    /**
     * 设置要悬浮的view
     *
     * @param layoutId
     */
    public void setContentView(int layoutId)
    {
        View view = null;
        if (layoutId != 0)
        {
            view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
        }
        setContentView(view);
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
            removeView(mContentView);
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
     * @param attach
     */
    public void addToWindow(boolean attach)
    {
        if (attach)
        {
            if (getParent() == null)
            {
                SDWindowManager.getInstance().addView(this, getWindowParams());
                mIsAddedToWindow = true;
            }
        } else
        {
            if (getParent() != null)
            {
                SDWindowManager.getInstance().removeView(this);
            }
        }
    }

    public boolean isAddedToWindow()
    {
        return mIsAddedToWindow;
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        mIsAddedToWindow = false;
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
