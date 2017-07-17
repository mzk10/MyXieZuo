package com.meng.hui.android.xiezuo.core;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by mzk10 on 2017/7/13.
 */

public abstract class MyActivity extends Activity implements View.OnClickListener{

    @Override
    protected final void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
        {
            onBackClicked();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public abstract void onBackClicked();


}
