package com.meng.hui.android.xiezuo.core;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.meng.hui.android.xiezuo.util.Utils;

import butterknife.ButterKnife;

/**
 * Created by mzk10 on 2017/7/13.
 */

public abstract class MyActivity extends AppCompatActivity{

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = LayoutInflater.from(getApplicationContext()).inflate(bindLayout(), null, false);
        setContentView(contentView);

        int statusHeight = Utils.getStatusHeight(getApplicationContext());

        contentView.setPadding(0, statusHeight, 0, 0);
        ButterKnife.bind(this);
        initView();
        initData();
        setListener();
        startAction();
    }

    public abstract int bindLayout();
    public abstract void initView();
    public abstract void initData();
    public abstract void setListener();
    public abstract void startAction();

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN)
        {
            onBackClicked();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public abstract void onBackClicked();

}
