package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

import java.lang.ref.WeakReference;

/**
 * 悬浮view帮助类
 */
public class SDFloatHelper
{
    private View mContentView;
    private Context mContext;

    private WeakReference<ViewGroup> mOriginalParent;
    private ViewGroup.LayoutParams mOriginalParams;
    private int mOriginalIndex = -1;

    private WindowManager.LayoutParams mWindowParams;

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

    private void keepOriginalSize()
    {
        if (mOriginalParams == null)
        {
            return;
        }
        getWindowParams().width = mOriginalParams.width;
        getWindowParams().height = mOriginalParams.height;
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
            if (!isAddedToWindow())
            {
                removeViewFromParent(view);
                SDWindowManager.getInstance().addView(view, getWindowParams());
            }
        } else
        {
            if (isAddedToWindow())
            {
                SDWindowManager.getInstance().removeViewImmediate(view);
            }
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
        return SDWindowManager.getInstance().containsView(mContentView);
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

            keepOriginalSize();
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

    private int mLastX;
    private int mLastY;

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
        if (!isAddedToWindow())
        {
            return;
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                mLastX = (int) event.getRawX();
                mLastY = (int) event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                final int screenWidth = mContext.getResources().getDisplayMetrics().widthPixels;
                final int screenHeight = mContext.getResources().getDisplayMetrics().heightPixels;
                final int maxX = screenWidth - view.getWidth();
                final int maxY = screenHeight - view.getHeight();

                final int moveX = (int) event.getRawX();
                final int moveY = (int) event.getRawY();

                int dx = moveX - mLastX;
                int dy = moveY - mLastY;

                mLastX = moveX;
                mLastY = moveY;

                if (dx > 0)
                {
                    if (view.canScrollHorizontally(-1))
                    {
                        dx = 0;
                    }
                } else if (dx < 0)
                {
                    if (view.canScrollHorizontally(1))
                    {
                        dx = 0;
                    }
                }

                if (dy > 0)
                {
                    if (view.canScrollVertically(-1))
                    {
                        dy = 0;
                    }
                } else if (dy < 0)
                {
                    if (view.canScrollVertically(1))
                    {
                        dy = 0;
                    }
                }

                int x = getWindowParams().x + dx;
                int y = getWindowParams().y + dy;

                if (x < 0)
                {
                    x = 0;
                } else if (x > maxX)
                {
                    x = maxX;
                }

                if (y < 0)
                {
                    y = 0;
                } else if (y > maxY)
                {
                    y = maxY;
                }

                if (getWindowParams().x != x || getWindowParams().y != y)
                {
                    getWindowParams().x = x;
                    getWindowParams().y = y;
                    SDWindowManager.getInstance().updateViewLayout(view, getWindowParams());
                }
                break;
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
