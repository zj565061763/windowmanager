package com.fanwe.www.windowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fanwe.library.windowmanager.SDFloatHelper;

public class MainActivity extends AppCompatActivity
{

    private SDFloatHelper mFloatHelper = new SDFloatHelper();
    private SDFloatHelper mNewActivityFloatHelper = new SDFloatHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatHelper.setContentView(findViewById(R.id.btn_float)); //设置要悬浮的view
        mNewActivityFloatHelper.setContentView(findViewById(R.id.btn_new_activity_float)); //设置要悬浮的view
    }

    public void onClickAddToWindow(View view)
    {
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

    public void onClickNewActivity(View view)
    {
        startActivity(new Intent(this, NewActivity.class));
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mNewActivityFloatHelper.restoreContentView();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mNewActivityFloatHelper.addToWindow(true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mFloatHelper.addToWindow(false);
        mNewActivityFloatHelper.addToWindow(false);
    }
}
