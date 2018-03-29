package com.fanwe.www.windowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fanwe.lib.windowmanager.FFloatView;


public class MainActivity extends AppCompatActivity
{
    private FFloatView mFloatView;
    private FFloatView mNewActivityFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplication(), "click", Toast.LENGTH_SHORT).show();
            }
        });

        mFloatView = new FFloatView(this);
        mFloatView.setContentView(findViewById(R.id.scrollView)); //设置要悬浮的view

        mNewActivityFloatView = new FFloatView(this);
        mNewActivityFloatView.setContentView(findViewById(R.id.btn_new_activity_float)); //设置要悬浮的view
    }

    public void onClickAddToWindow(View view)
    {
        mFloatView.addToWindow(true); //true-添加到Window
    }

    public void onClickRemoveFromWindow(View view)
    {
        mFloatView.addToWindow(false); //false-从Window移除
    }

    public void onClickRestore(View view)
    {
        mFloatView.restoreContentView(); //还原到原xml布局
    }

    public void onClickNewActivity(View view)
    {
        startActivity(new Intent(this, NewActivity.class));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mNewActivityFloatView.restoreContentView(); //新界面回来的时候还原
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mNewActivityFloatView.addToWindow(true); //打开新界面的或时候悬浮
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mFloatView.setContentView(null);
        mNewActivityFloatView.setContentView(null);
    }
}
