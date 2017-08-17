package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;

/**
 * 可拖拽的悬浮view
 */
public class SDDraggableFloatView extends SDFloatView
{
    public SDDraggableFloatView(Context context)
    {
        super(context);
    }

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev)
    {
        mTouchHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
        mTouchHelper.processTouchEvent(event);

        switch (event.getAction())
        {
            case MotionEvent.ACTION_MOVE:
                getWindowParams().x += mTouchHelper.getDeltaXFrom(SDTouchHelper.EVENT_LAST);
                getWindowParams().y += mTouchHelper.getDeltaYFrom(SDTouchHelper.EVENT_LAST);
                SDWindowManager.getInstance().updateViewLayout(this, getWindowParams());
                break;
        }

        return super.onTouchEvent(event);
    }
}
