package com.fanwe.www.windowmanager;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.fanwe.library.SDLibrary;
import com.fanwe.library.adapter.SDSimpleAdapter;
import com.fanwe.library.windowmanager.SDDraggableFloatView;

import java.util.Arrays;
import java.util.List;


public class MainActivity extends AppCompatActivity
{
    private SDDraggableFloatView mFloatView;
    private SDDraggableFloatView mNewActivityFloatView;

    private ListView lv_content;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        SDLibrary.getInstance().init(getApplication());
        setContentView(R.layout.activity_main);
        bindListView();
        findViewById(R.id.btn_float).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(getApplication(), "click", Toast.LENGTH_SHORT).show();
            }
        });

        mFloatView = new SDDraggableFloatView(this);
        mFloatView.setContentView(findViewById(R.id.lv_content)); //设置要悬浮的view

        mNewActivityFloatView = new SDDraggableFloatView(this);
        mNewActivityFloatView.setContentView(findViewById(R.id.btn_new_activity_float)); //设置要悬浮的view
    }

    /**
     * listview测试
     */
    private void bindListView()
    {
        lv_content = (ListView) findViewById(R.id.lv_content);

        String[] arrModel = new String[100];
        List<String> listModel = Arrays.asList(arrModel);
        final SDSimpleAdapter<String> adapter = new SDSimpleAdapter<String>(listModel, this)
        {
            @Override
            public int getLayoutId(int position, View convertView, ViewGroup parent)
            {
                return R.layout.item_listview;
            }

            @Override
            public void bindData(int position, View convertView, ViewGroup parent, String model)
            {
            }
        };
        lv_content.setAdapter(adapter);
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
        mFloatView.addToWindow(false);
        mNewActivityFloatView.addToWindow(false);
    }
}
