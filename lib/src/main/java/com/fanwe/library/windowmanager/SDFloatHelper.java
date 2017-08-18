package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;
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
    private Context mContext;

    private WeakReference<ViewGroup> mOriginalParent;
    private ViewGroup.LayoutParams mOriginalParams;
    private int mOriginalIndex = -1;

    private WindowManager.LayoutParams mWindowParams;
    private boolean mIsAddedToWindow;

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

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
        if (mContentView != null && parent != null)
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
        if (mContentView != null)
        {
            mContentView.setOnTouchListener(null);
        }

        mContentView = view;
        if (view != null)
        {
            if (mContext == null)
            {
                mContext = view.getContext().getApplicationContext();
            }

            final ViewParent viewParent = view.getParent();
            if (viewParent instanceof ViewGroup)
            {
                final ViewGroup viewGroup = (ViewGroup) viewParent;

                setOriginalParent(viewGroup);
                mOriginalParams = view.getLayoutParams();
                mOriginalIndex = viewGroup.indexOfChild(view);
            }

            view.setOnTouchListener(mInternalOnTouchListener);
        }
    }

    private View.OnTouchListener mInternalOnTouchListener = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View v, MotionEvent event)
        {
            processTouchEvent(event);
            return false;
        }
    };

    /**
     * 处理触摸事件
     *
     * @param event
     */
    public void processTouchEvent(MotionEvent event)
    {
        final View view = getContentView();
        if (view == null)
        {
            return;
        }
        if (!mIsAddedToWindow)
        {
            return;
        }

        mTouchHelper.processTouchEvent(event);
        if (event.getAction() == MotionEvent.ACTION_MOVE)
        {
            int dx = (int) mTouchHelper.getDeltaXFrom(SDTouchHelper.EVENT_LAST);
            int dy = (int) mTouchHelper.getDeltaYFrom(SDTouchHelper.EVENT_LAST);

            final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
            final int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;

            dx = mTouchHelper.getLegalDeltaX(getWindowParams().x, 0, screenWidth - view.getWidth(), dx);
            dy = mTouchHelper.getLegalDeltaY(getWindowParams().y, 0, screenHeight - view.getHeight(), dy);

            boolean needUpdate = false;
            if (dx != 0)
            {
                getWindowParams().x += dx;
                needUpdate = true;
            }
            if (dy != 0)
            {
                getWindowParams().y += dy;
                needUpdate = true;
            }

            if (needUpdate)
            {
                SDWindowManager.getInstance().updateViewLayout(view, getWindowParams());
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
