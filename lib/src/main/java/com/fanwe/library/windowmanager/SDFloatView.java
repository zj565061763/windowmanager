package com.fanwe.library.windowmanager;

import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;

/**
 * 悬浮view
 */
public class SDFloatView extends FrameLayout
{
    public SDFloatView(Context context)
    {
        super(context.getApplicationContext());
    }

    private View mContentView;
    private SDViewHelper mViewHelper = new SDViewHelper();

    private WindowManager.LayoutParams mWindowParams;

    /**
     * 设置要悬浮的view
     *
     * @param view
     */
    public void setContentView(View view)
    {
        if (mContentView == view)
        {
            return;
        }
        mContentView = view;

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
    public void restoreContentView()
    {
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

    public void updateViewLayout()
    {
        SDWindowManager.getInstance().updateViewLayout(this, getWindowParams());
    }

    /**
     * 把contentView添加到当前view
     */
    private void addContentViewInternal()
    {
        final View view = getContentView();
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
        addView(view, params);
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
            addContentViewInternal();
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
}
