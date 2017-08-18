package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

/**
 * 可拖动的悬浮view
 */
public class SDDraggableFloatView extends SDFloatView
{
    public SDDraggableFloatView(Context context)
    {
        super(context);
    }

    /**
     * 是否可以拖动
     */
    private boolean mIsDraggable = true;

    /**
     * 设置是否可以拖动，默认可以拖动
     *
     * @param draggable
     */
    public void setDraggable(boolean draggable)
    {
        mIsDraggable = draggable;
    }

    @Override
    public void setContentView(View view)
    {
        View oldView = getContentView();
        if (oldView != null)
        {
            oldView.setOnTouchListener(null);
        }

        super.setContentView(view);

        if (view != null)
        {
            view.setOnTouchListener(mInternalOnTouchListener);
        }
    }

    private OnTouchListener mInternalOnTouchListener = new OnTouchListener()
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

    private void processTouchEvent(MotionEvent event)
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
                final int screenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
                final int screenHeight = getContext().getResources().getDisplayMetrics().heightPixels;
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
                    SDWindowManager.getInstance().updateViewLayout(this, getWindowParams());
                }
                break;
        }
    }
}
