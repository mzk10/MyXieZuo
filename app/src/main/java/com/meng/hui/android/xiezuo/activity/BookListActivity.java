package com.meng.hui.android.xiezuo.activity;

import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.BookEntity;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;

import java.util.ArrayList;
import java.util.List;


public class BookListActivity extends MyActivity {

    private Button action_bar_btn_back;
    private Button action_bar_btn_menu;
    private TextView action_bar_tv_title;
    private Button btn_add;
    private ListView lv_booklist;

    private LayoutInflater inflater;
    private List<BookEntity> booklist;

    @Override
    public void initView(Bundle savedInstanceState) {
        setContentView(R.layout.activity_booklist);

        action_bar_btn_back = findViewById(R.id.action_bar_btn_back);
        action_bar_btn_menu = findViewById(R.id.action_bar_btn_menu);
        action_bar_tv_title = findViewById(R.id.action_bar_tv_title);
        lv_booklist = findViewById(R.id.lv_booklist);
        btn_add = findViewById(R.id.btn_add);

        action_bar_btn_back.setOnClickListener(this);
    }

    @Override
    public void initData(Bundle savedInstanceState) {
        action_bar_tv_title.setText("小说列表");
        action_bar_btn_back.setOnClickListener(this);
        action_bar_btn_menu.setVisibility(View.GONE);
        btn_add.setOnClickListener(this);

        inflater = LayoutInflater.from(this);
        booklist = initBookData();
        MyAdapter adapter = new MyAdapter();
        lv_booklist.setAdapter(adapter);
    }

    private List<BookEntity> initBookData() {
        return new ArrayList<>();
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
        }else if (view == btn_add)
        {
            createBook();
        }
    }

    private void createBook() {
        MakeDialogUtil.showInputDialog(this, "你他妈倒是写个书名啊！", new String[]{""}, new String[]{""}, new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String[] params) {
                String name = params[0];
                if (name == null || "".equals(name))
                {
                    Toast.makeText(BookListActivity.this, "你不写有个鸡吧用啊！！", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(BookListActivity.this, "写了也没鸡吧用23333", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private class MyAdapter extends BaseAdapter
    {

        private SparseArray<View.OnClickListener> listenerMap;

        private MyAdapter() {
            this.listenerMap = new SparseArray<>();
        }

        @Override
        public int getCount() {
            return booklist.size();
        }

        @Override
        public BookEntity getItem(int i) {
            return booklist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null)
            {
                view = inflater.inflate(R.layout.layout_booklist_item, null);
                Holder holder = new Holder();
                holder.bookname = view.findViewById(R.id.tv_booklist_item_bookname);
                holder.bookcount = view.findViewById(R.id.tv_booklist_item_bookcount);
                holder.booklength = view.findViewById(R.id.tv_booklist_item_booklength);
                view.setTag(holder);
            }
            BookEntity item = getItem(i);
            Holder holder = (Holder) view.getTag();
            holder.bookname.setText(item.getBookName());
            holder.bookcount.setText(String.valueOf(item.getBookCount()));
            holder.booklength.setText(String.valueOf(item.getBookLength()));

            View.OnClickListener listener = listenerMap.get(i);
            if (listener == null)
            {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //TODO
                    }
                };
                listenerMap.put(i, listener);
            }
            view.setOnClickListener(listenerMap.get(i));
            return view;
        }
    }

    private class Holder
    {
        private TextView bookname;
        private TextView bookcount;
        private TextView booklength;
    }

}
