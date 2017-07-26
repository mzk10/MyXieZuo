package com.meng.hui.android.xiezuo.core;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.KeyEvent;
import android.view.View;

/**
 * Created by mzk10 on 2017/7/13.
 */

public abstract class MyActivity extends Activity implements View.OnClickListener{

    @Override
    protected final void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        initView();
        initData();
    }

    @Override
    protected final void onResume() {
        super.onResume();
        startAction();
    }

    public abstract void initView();
    public abstract void initData();
    public abstract void startAction();

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
