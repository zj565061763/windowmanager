package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;

import com.fanwe.library.touchhelper.SDTouchHelper;

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

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

    private boolean dontProcessTouchEvent()
    {
        return (!mIsDraggable || getContentView() == null || !isAddedToWindow());
    }

    private boolean canDrag()
    {
        boolean result = false;

        mTouchHelper.saveDirection();
        switch (mTouchHelper.getDirection())
        {
            case MoveLeft:
                if (SDTouchHelper.isScrollToRight(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveTop:
                if (SDTouchHelper.isScrollToBottom(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveRight:
                if (SDTouchHelper.isScrollToLeft(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveBottom:
                if (SDTouchHelper.isScrollToTop(getContentView()))
                {
                    result = true;
                }
                break;
        }
        return result;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        if (dontProcessTouchEvent())
        {
            return false;
        }

        if (mTouchHelper.isNeedIntercept())
        {
            return true;
        }
        mTouchHelper.processTouchEvent(ev);
        switch (ev.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (canDrag())
                {
                    mTouchHelper.setNeedIntercept(true);
                }
                break;
        }
        return mTouchHelper.isNeedIntercept();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (dontProcessTouchEvent())
        {
            return false;
        }

        mTouchHelper.processTouchEvent(event);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                if (mTouchHelper.isNeedCosume())
                {
                    int dx = (int) mTouchHelper.getDeltaXFrom(SDTouchHelper.EVENT_LAST);
                    int dy = (int) mTouchHelper.getDeltaYFrom(SDTouchHelper.EVENT_LAST);

                    final int screenWidth = getResources().getDisplayMetrics().widthPixels;
                    final int screenHeight = getResources().getDisplayMetrics().heightPixels;
                    final int maxX = screenWidth - getContentView().getWidth();
                    final int maxY = screenHeight - getContentView().getHeight();

                    dx = mTouchHelper.getLegalDeltaX(getWindowParams().x, 0, maxX, dx);
                    dy = mTouchHelper.getLegalDeltaY(getWindowParams().y, 0, maxY, dy);

                    if (dx != 0 || dy != 0)
                    {
                        getWindowParams().x += dx;
                        getWindowParams().y += dy;
                        updateViewLayout();
                    }
                } else
                {
                    if (canDrag() || mTouchHelper.isNeedIntercept())
                    {
                        mTouchHelper.setNeedCosume(true);
                    }
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                mTouchHelper.setNeedIntercept(false);
                mTouchHelper.setNeedCosume(false);
                break;
        }
        return mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }
}
