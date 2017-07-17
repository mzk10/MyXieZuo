package com.meng.hui.android.xiezuo.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Created by mzk10 on 2017/7/13.
 */

public abstract class MyActivity extends Activity {

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView(savedInstanceState);
        initData(savedInstanceState);
        startAction(savedInstanceState);
    }

    public abstract void initView(Bundle savedInstanceState);
    public abstract void initData(Bundle savedInstanceState);
    public abstract void startAction(Bundle savedInstanceState);

    @Override
    public void finish() {
        super.finish();
    }
}
