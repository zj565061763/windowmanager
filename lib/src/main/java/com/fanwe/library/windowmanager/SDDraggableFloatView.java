package com.fanwe.library.windowmanager;

import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;

/**
 * 可拖动的悬浮view
 */
public class SDDraggableFloatView extends SDFloatView
{
    public SDDraggableFloatView(Context context)
    {
        super(context);
        init();
    }

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

    /**
     * 是否可以拖动
     */
    private boolean mIsDraggable = true;

    private void init()
    {
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

    private boolean canDrag()
    {
        boolean result = false;

        switch (mTouchHelper.getFirstMoveDirection())
        {
            case MoveLeft:
                if (!getContentView().canScrollHorizontally(1))
                {
                    result = true;
                }
                break;
            case MoveTop:
                if (!getContentView().canScrollVertically(1))
                {
                    result = true;
                }
                break;
            case MoveRight:
                if (!getContentView().canScrollHorizontally(-1))
                {
                    result = true;
                }
                break;
            case MoveBottom:
                if (!getContentView().canScrollVertically(-1))
                {
                    result = true;
                }
                break;
        }

        Log.i("SDDraggableFloatView", "SDDraggableFloatView:" + mTouchHelper.getFirstMoveDirection());

//        if (mTouchHelper.isMoveLeftFrom(SDTouchHelper.EVENT_DOWN))
//        {
//            if (!getContentView().canScrollHorizontally(1))
//            {
//                result = true;
//            }
//        } else if (mTouchHelper.isMoveRightFrom(SDTouchHelper.EVENT_DOWN))
//        {
//            if (!getContentView().canScrollHorizontally(-1))
//            {
//                result = true;
//            }
//        } else if (mTouchHelper.isMoveUpFrom(SDTouchHelper.EVENT_DOWN))
//        {
//            if (!getContentView().canScrollVertically(1))
//            {
//                result = true;
//            }
//        } else if (mTouchHelper.isMoveDownFrom(SDTouchHelper.EVENT_DOWN))
//        {
//            if (!getContentView().canScrollVertically(-1))
//            {
//                result = true;
//            }
//        }
        return result;
    }

    private boolean dontProcessTouchEvent()
    {
        return (!mIsDraggable || getContentView() == null || !isAddedToWindow());
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
                mTouchHelper.resetFirstMoveDirection();
                break;
        }
        return mTouchHelper.isNeedCosume() || event.getAction() == MotionEvent.ACTION_DOWN;
    }
}
