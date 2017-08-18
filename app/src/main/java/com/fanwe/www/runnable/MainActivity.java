package com.fanwe.www.runnable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.fanwe.library.windowmanager.SDFloatHelper;

public class MainActivity extends AppCompatActivity
{

    private SDFloatHelper mFloatHelper = new SDFloatHelper();
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
    }

    public void onClickAddToWindow(View view)
    {
        mFloatHelper.setContentView(btn); //设置要悬浮的view
        mFloatHelper.addToWindow(true); //true-添加到Window
    }

    public void onClickRemoveFromWindow(View view)
    {
        mFloatHelper.addToWindow(false); //false-从Window移除
    }

    public void onClickRestore(View view)
    {
        mFloatHelper.restoreContentView(); //还原到原xml布局
    }
}
