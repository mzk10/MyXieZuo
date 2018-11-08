package com.meng.hui.android.xiezuo.util.pop.holder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import butterknife.ButterKnife;

public abstract class BaseHolder<T,Y,U> {

    private OnCloseListener<T,Y,U> listener;
    private final Context context;
    public View view;

    public BaseHolder(Context context, OnCloseListener<T,Y,U> listener){
        this.context = context;
        this.listener = listener;
        view = LayoutInflater.from(context).inflate(initView(), null, false);
        ButterKnife.bind(this, view);
        startAction();
    }

    public abstract int initView();

    public abstract void startAction();

    public Context getContext() {
        return context;
    }

    public OnCloseListener<T,Y,U> getListener() {
        return listener;
    }

    public abstract void changeValue(String... value);
    /*public void setListener(OnCloseListener<T,Y,U> listener) {
        this.listener = listener;
    }*/

    public interface OnCloseListener<E,F,G> {
        void onClick(boolean isClick, E param1, F param2, G param3);
    }

}
