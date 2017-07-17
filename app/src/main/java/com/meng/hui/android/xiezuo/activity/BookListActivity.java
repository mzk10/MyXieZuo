package com.meng.hui.android.xiezuo.activity;

import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;

/**
 * Created by mzk10 on 2017/7/17.
 */

public class BookListActivity extends MyActivity {

    private Button action_bar_btn_back;
    private Button action_bar_btn_menu;
    private TextView action_bar_tv_title;
    private ListView lv_booklist;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_booklist);

        action_bar_btn_back = findViewById(R.id.action_bar_btn_back);
        action_bar_btn_menu = findViewById(R.id.action_bar_btn_menu);
        action_bar_tv_title = findViewById(R.id.action_bar_tv_title);
        lv_booklist = findViewById(R.id.lv_booklist);

        action_bar_tv_title.setText("小说列表");

        action_bar_btn_back.setOnClickListener(this);
        action_bar_btn_menu.setOnClickListener(this);

    }

    @Override
    public void initData(Bundle savedInstanceState) {
    }

    @Override
    public void startAction(Bundle savedInstanceState) {

    }

    @Override
    public void onBackClicked() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == action_bar_btn_back)
        {
            finish();
        }else if (view == action_bar_btn_menu)
        {

        }
    }


    public class MyAdapter extends BaseAdapter
    {

        @Override
        public int getCount() {
            return 0;
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            return null;
        }
    }




}
