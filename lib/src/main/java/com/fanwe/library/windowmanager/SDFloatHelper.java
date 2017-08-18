package com.fanwe.library.windowmanager;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.WindowManager;

/**
 * 悬浮view帮助类
 */
public class SDFloatHelper
{
    private View mContentView;
    private SDViewHelper mViewHelper = new SDViewHelper();

    private WindowManager.LayoutParams mWindowParams;

    /**
     * 是否可以拖动
     */
    private boolean mIsDraggable = true;

    /**
     * 设置要悬浮的view
     *
     * @param view
     */
    public void setContentView(View view)
    {
        if (mContentView != view)
        {
            if (mContentView != null)
            {
                mContentView.setOnTouchListener(null);
            }
            mContentView = view;

            mViewHelper.save(view);
            if (mViewHelper.getParams() != null)
            {
                getWindowParams().width = mViewHelper.getParams().width;
                getWindowParams().height = mViewHelper.getParams().height;
            }

            if (view != null)
            {
                view.setOnTouchListener(mInternalOnTouchListener);
            }
        }
    }

    /**
     * 还原悬浮view到原parent
     */
    public void restoreContentView()
    {
        if (!mViewHelper.canRestore(mContentView))
        {
            return;
        }

        addToWindow(false);
        mViewHelper.restore(mContentView);
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
     * 设置是否可以拖动，默认可以拖动
     *
     * @param draggable
     */
    public void setDraggable(boolean draggable)
    {
        mIsDraggable = draggable;
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
        updateViewLayout();
    }

    /**
     * 根据LayoutParams更新布局
     */
    public void updateViewLayout()
    {
        if (isAddedToWindow())
        {
            SDWindowManager.getInstance().updateViewLayout(getContentView(), getWindowParams());
        }
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
        if (!(viewParent instanceof ViewGroup))
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
        if (!mIsDraggable)
        {
            return;
        }
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
                final int screenWidth = mViewHelper.getContext().getResources().getDisplayMetrics().widthPixels;
                final int screenHeight = mViewHelper.getContext().getResources().getDisplayMetrics().heightPixels;
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
}
