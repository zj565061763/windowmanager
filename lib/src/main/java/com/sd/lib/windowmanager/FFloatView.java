package com.sd.lib.windowmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 悬浮view
 */
public class FFloatView extends FrameLayout
{
    public FFloatView(Context context)
    {
        super(context.getApplicationContext());
    }

    private View mContentView;
    private final ViewStoreHelper mViewStoreHelper = new ViewStoreHelper();

    private WindowManager.LayoutParams mWindowParams;

    /**
     * 返回设置的内容view
     *
     * @return
     */
    public final View getContentView()
    {
        return mContentView;
    }

    /**
     * 设置内容view给当前悬浮view
     *
     * @param layoutId 内容view布局id
     */
    public final void setContentView(int layoutId)
    {
        final View view = LayoutInflater.from(getContext()).inflate(layoutId, this, false);
        setContentView(view);
    }

    /**
     * 设置内容view给当前悬浮view
     *
     * @param view
     */
    public final void setContentView(View view)
    {
        final View old = getContentView();

        if (old == view)
            return;

        if (old != null)
        {
            if (old.getParent() == this)
                removeView(old);
        }

        mContentView = view;
        onContentViewChanged(view);

        mViewStoreHelper.save(view);
        synchronizeContentViewSizeToFloatView();
    }

    /**
     * {@link #setContentView(View)}和{@link #setContentView(int)}之后会触发此方法，可用来初始化内容view
     *
     * @param contentView 可能为null
     */
    protected void onContentViewChanged(View contentView)
    {

    }

    /**
     * 还原内容view到原父容器
     */
    public final void restoreContentView()
    {
        mViewStoreHelper.restore();
    }

    /**
     * 还原内容View到某个容器
     *
     * @param viewGroup
     */
    public final void restoreContentViewTo(ViewGroup viewGroup)
    {
        mViewStoreHelper.restoreTo(viewGroup);
    }

    /**
     * 返回WindowManager的LayoutParams
     *
     * @return
     */
    public final WindowManager.LayoutParams getWindowParams()
    {
        if (mWindowParams == null)
            mWindowParams = FWindowManager.newLayoutParams();
        return mWindowParams;
    }

    /**
     * 把内容view的大小同步到悬浮view
     */
    public final void synchronizeContentViewSizeToFloatView()
    {
        final View view = getContentView();
        if (view == null)
            return;

        final ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params == null)
            return;

        final WindowManager.LayoutParams windowParams = getWindowParams();
        windowParams.width = params.width;
        windowParams.height = params.height;
        updateFloatViewLayout();
    }

    /**
     * 更新悬浮view布局
     */
    public final void updateFloatViewLayout()
    {
        FWindowManager.getInstance().updateViewLayout(this, getWindowParams());
    }

    /**
     * 把悬浮view添加到window或者移除
     *
     * @param add
     */
    public final void addToWindow(boolean add)
    {
        if (add)
        {
            addContentViewToFloatViewIfNeed();
            FWindowManager.getInstance().addView(this, getWindowParams());
        } else
        {
            FWindowManager.getInstance().removeView(this);
        }
    }

    /**
     * 把内容view添加到当前悬浮view
     */
    private void addContentViewToFloatViewIfNeed()
    {
        final View view = getContentView();
        if (view == null)
            throw new NullPointerException("contentView is null");

        if (view.getParent() == this)
            return;

        ViewStoreHelper.removeViewFromParent(view);
        removeAllViews();
        addView(view);
    }

    @Override
    public void onViewRemoved(View child)
    {
        super.onViewRemoved(child);

        if (mContentView == child)
            setContentView(null);

        if (getChildCount() <= 0)
            addToWindow(false);
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
    public final void setDraggable(boolean draggable)
    {
        mIsDraggable = draggable;
    }

    /**
     * 是否可以拖动
     *
     * @return
     */
    public final boolean isDraggable()
    {
        return mIsDraggable;
    }

    private final FTouchHelper mTouchHelper = new FTouchHelper();

    private boolean dontProcessTouchEvent()
    {
        return (!mIsDraggable || getContentView() == null || getContentView().getParent() != this);
    }

    private boolean canDrag()
    {
        boolean result = false;

        mTouchHelper.saveDirection();
        switch (mTouchHelper.getDirection())
        {
            case MoveLeft:
                if (FTouchHelper.isScrollToRight(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveTop:
                if (FTouchHelper.isScrollToBottom(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveRight:
                if (FTouchHelper.isScrollToLeft(getContentView()))
                {
                    result = true;
                }
                break;
            case MoveBottom:
                if (FTouchHelper.isScrollToTop(getContentView()))
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
                    int dx = (int) mTouchHelper.getDeltaXFrom(FTouchHelper.EVENT_LAST);
                    int dy = (int) mTouchHelper.getDeltaYFrom(FTouchHelper.EVENT_LAST);

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
                        updateFloatViewLayout();
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
