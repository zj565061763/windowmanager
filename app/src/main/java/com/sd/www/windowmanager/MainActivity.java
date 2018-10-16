package com.sd.www.windowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sd.lib.windowmanager.FFloatView;


public class MainActivity extends AppCompatActivity
{
    private FFloatView mFloatView;
    private FFloatView mActivityFloatView;

    private View mFloatContentView;
    private View mActivityFloatContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloatContentView = findViewById(R.id.scrollView);
        mActivityFloatContentView = findViewById(R.id.btn_new_activity_float);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplication(), "click", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private FFloatView getFloatView()
    {
        if (mFloatView == null)
            mFloatView = new FFloatView(this);
        return mFloatView;
    }

    private FFloatView getActivityFloatView()
    {
        if (mActivityFloatView == null)
            mActivityFloatView = new FFloatView(this);
        return mActivityFloatView;
    }

    /**
     * 添加到Window
     */
    public void onClickAddToWindow(View view)
    {
        getFloatView().setContentView(mFloatContentView);
        getFloatView().addToWindow(true);
    }

    /**
     * 从Window移除
     */
    public void onClickRemoveFromWindow(View view)
    {
        getFloatView().addToWindow(false);
    }

    /**
     * 还原到原xml布局
     */
    public void onClickRestore(View view)
    {
        getFloatView().restoreContentView();
    }

    public void onClickNewActivity(View view)
    {
        startActivity(new Intent(this, NewActivity.class));
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        // 新界面回来的时候还原
        getActivityFloatView().restoreContentView();
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        // 打开新界面的时候悬浮
        getActivityFloatView().setContentView(mActivityFloatContentView);
        getActivityFloatView().addToWindow(true);
    }
}
