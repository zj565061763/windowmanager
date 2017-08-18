package com.fanwe.www.windowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fanwe.library.windowmanager.SDFloatHelper;

import ezy.assist.compat.SettingsCompat;

public class MainActivity extends AppCompatActivity
{

    private SDFloatHelper mFloatHelper = new SDFloatHelper();
    private SDFloatHelper mOnStopFloatHelper = new SDFloatHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatHelper.setContentView(findViewById(R.id.btn)); //设置要悬浮的view

        mOnStopFloatHelper.setContentView(findViewById(R.id.btn_onstop)); //设置要悬浮的view
    }

    public void onClickAddToWindow(View view)
    {
        if (SettingsCompat.canDrawOverlays(this))
        {
            mFloatHelper.addToWindow(true); //true-添加到Window
        } else
        {
            SettingsCompat.manageDrawOverlays(this);
            Toast.makeText(this, "请打开悬浮窗权限", Toast.LENGTH_SHORT).show();
        }
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
        mOnStopFloatHelper.restoreContentView();
    }

    @Override
    protected void onStop()
    {
        super.onStop();
        mOnStopFloatHelper.addToWindow(true);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mFloatHelper.addToWindow(false);
        mFloatHelper.setContentView(null);

        mOnStopFloatHelper.addToWindow(false);
        mOnStopFloatHelper.setContentView(null);
    }
}
