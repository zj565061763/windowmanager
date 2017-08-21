package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.fanwe.library.touchhelper.SDTouchHelper;

/**
 * 悬浮view
 */
public class SDFloatView extends FrameLayout
{
    public SDFloatView(Context context)
    {
        super(context.getApplicationContext());
    }

    private View mFloatView;
    private SDViewHelper mViewHelper = new SDViewHelper();

    private WindowManager.LayoutParams mWindowParams;

    /**
     * 设置要悬浮的view
     *
     * @param view
     */
    public void setFloatView(View view)
    {
        if (mFloatView == view)
        {
            return;
        }
        addToWindow(false);

        if (mFloatView != null)
        {
            if (mFloatView.getParent() == this)
            {
                removeView(mFloatView);
                mFloatView.setLayoutParams(mViewHelper.getParams());
            }
        }

        mFloatView = view;

        mViewHelper.save(view);
        if (mViewHelper.getParams() != null)
        {
            getWindowParams().width = mViewHelper.getParams().width;
            getWindowParams().height = mViewHelper.getParams().height;
        }
    }

    /**
     * 还原悬浮view到原parent
     */
    public void restoreFloatView()
    {
        addToWindow(false);
        mViewHelper.restore(mFloatView);
    }

    /**
     * 返回悬浮的内容view
     *
     * @return
     */
    public View getFloatView()
    {
        return mFloatView;
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
     * 更新View布局
     */
    public void updateViewLayout()
    {
        SDWindowManager.getInstance().updateViewLayout(this, getWindowParams());
    }

    /**
     * 把需要悬浮的view添加到当前view
     */
    private void addFloatViewInternal()
    {
        final View view = getFloatView();
        if (view == null)
        {
            return;
        }
        if (view.getParent() == this)
        {
            return;
        }
        SDViewHelper.removeViewFromParent(view);

        final FrameLayout.LayoutParams params = generateDefaultLayoutParams();
        view.setLayoutParams(params);
        onFloatViewAdd(view);
    }

    /**
     * 需要悬浮的view被添加到当前view
     *
     * @param view
     */
    protected void onFloatViewAdd(View view)
    {
        addView(view, 0);
    }

    /**
     * 是否添加到Window
     *
     * @param add
     */
    public void addToWindow(boolean add)
    {
        if (add)
        {
            addFloatViewInternal();
            SDWindowManager.getInstance().addView(this, getWindowParams());
        } else
        {
            SDWindowManager.getInstance().removeViewImmediate(this);
        }
    }

    /**
     * 是否已经被添加到Window
     *
     * @return
     */
    public boolean isAddedToWindow()
    {
        return SDWindowManager.getInstance().containsView(this);
    }

    //----------drag logic start----------

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

    /**
     * 是否可以拖动
     *
     * @return
     */
    public boolean isDraggable()
    {
        return mIsDraggable;
    }

    private SDTouchHelper mTouchHelper = new SDTouchHelper();

    private boolean dontProcessTouchEvent()
    {
        return (!mIsDraggable || getFloatView() == null || getFloatView().getParent() != this);
    }

    private boolean canDrag()
    {
        boolean result = false;

        mTouchHelper.saveDirection();
        switch (mTouchHelper.getDirection())
        {
            case MoveLeft:
                if (SDTouchHelper.isScrollToRight(getFloatView()))
                {
                    result = true;
                }
                break;
            case MoveTop:
                if (SDTouchHelper.isScrollToBottom(getFloatView()))
                {
                    result = true;
                }
                break;
            case MoveRight:
                if (SDTouchHelper.isScrollToLeft(getFloatView()))
                {
                    result = true;
                }
                break;
            case MoveBottom:
                if (SDTouchHelper.isScrollToTop(getFloatView()))
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
                    final int maxX = screenWidth - getFloatView().getWidth();
                    final int maxY = screenHeight - getFloatView().getHeight();

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

    //----------drag logic end----------
}
