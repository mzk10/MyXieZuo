package com.meng.hui.android.xiezuo.util.pop.holder;

import android.content.Context;
import android.view.View;

import com.meng.hui.android.xiezuo.R;

import butterknife.OnClick;

public class MainMenuHolder extends BaseHolder<Integer, Void, Void> {

    public MainMenuHolder(Context context, OnCloseListener<Integer, Void, Void> listener) {
        super(context, listener);
    }

    @Override
    public void startAction() {
    }

    @Override
    public int initView() {
        return R.layout.pop_main_menu;
    }

    @Override
    public void changeValue(String... value) {

    }

    @OnClick({R.id.btn_main_menu_changetextsize, R.id.btn_main_menu_changefont, R.id.btn_main_menu_changebg, R.id.btn_main_menu_cancel})
    public void onViewClicked(View view) {
        if (getListener() != null) {
            getListener().onClick(true, view.getId(), null, null);
        }
    }
}
