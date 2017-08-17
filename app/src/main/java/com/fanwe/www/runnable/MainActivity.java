package com.fanwe.www.runnable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.fanwe.library.windowmanager.SDDraggableFloatView;

public class MainActivity extends AppCompatActivity
{

    private SDDraggableFloatView mFloatView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFloatView = new SDDraggableFloatView(this);
    }

    public void onClickAddToWindow(View view)
    {
        mFloatView.setContentView(findViewById(R.id.btn));
        mFloatView.addToWindow(true);
    }
}
