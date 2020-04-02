package com.sd.www.windowmanager;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.sd.lib.windowmanager.FFloatView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;


public class MainActivity extends AppCompatActivity
{
    private FFloatView mFloatView;
    private View mFloatContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFloatContentView = findViewById(R.id.scrollView);

        findViewById(R.id.btn_click).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplication(), "click", Toast.LENGTH_SHORT).show();
            }
        });

        AndPermission.with(this)
                .overlay()
                .onGranted(new Action<Void>()
                {
                    @Override
                    public void onAction(Void data)
                    {
                        Toast.makeText(MainActivity.this, "onGranted", Toast.LENGTH_SHORT).show();
                    }
                })
                .onDenied(new Action<Void>()
                {
                    @Override
                    public void onAction(Void data)
                    {
                        Toast.makeText(MainActivity.this, "onDenied", Toast.LENGTH_SHORT).show();
                    }
                }).start();

    }

    private FFloatView getFloatView()
    {
        if (mFloatView == null)
            mFloatView = new FFloatView(this);
        return mFloatView;
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
}
