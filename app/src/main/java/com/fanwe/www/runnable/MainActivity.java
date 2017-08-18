package com.fanwe.www.runnable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.fanwe.library.windowmanager.SDFloatHelper;

public class MainActivity extends AppCompatActivity
{

    private SDFloatHelper mFloatHelper = new SDFloatHelper();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplication(), "click", 0).show();
            }
        });
    }

    public void onClickAddToWindow(View view)
    {
        mFloatHelper.setContentView(findViewById(R.id.btn));
        mFloatHelper.addToWindow(true);
    }

    public void onClickRestore(View view)
    {
        mFloatHelper.restoreContentView();
    }
}
