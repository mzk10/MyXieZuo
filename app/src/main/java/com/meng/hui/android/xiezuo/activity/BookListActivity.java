package com.meng.hui.android.xiezuo.activity;

import android.content.Intent;
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
import com.meng.hui.android.xiezuo.util.Utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;


public class BookListActivity extends MyActivity {

    private static final String BOOKLIST_DIR = "/booklist/";

    private Button action_bar_btn_back;
    private Button action_bar_btn_menu;
    private TextView action_bar_tv_title;
    private Button btn_add;
    private ListView lv_booklist;

    private LayoutInflater inflater;
    private List<BookEntity> booklist;
    private SparseArray<View.OnClickListener> listenerMap;
    private MyAdapter booklist_adapter;
    private MyComparator booklist_comparator;
    private SimpleDateFormat format;

    @Override
    public void initView() {
        setContentView(R.layout.activity_booklist);

        action_bar_btn_back = findViewById(R.id.action_bar_btn_back);
        action_bar_btn_menu = findViewById(R.id.action_bar_btn_menu);
        action_bar_tv_title = findViewById(R.id.action_bar_tv_title);
        lv_booklist = findViewById(R.id.lv_booklist);
        btn_add = findViewById(R.id.btn_add);

    }

    @Override
    public void initData() {
        action_bar_tv_title.setText("小说列表");
        action_bar_btn_back.setVisibility(View.INVISIBLE);
        action_bar_btn_menu.setVisibility(View.INVISIBLE);
        btn_add.setOnClickListener(this);

        inflater = LayoutInflater.from(this);
        booklist = new ArrayList<>();
        listenerMap = new SparseArray<>();
        booklist_adapter = new MyAdapter();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

        booklist_comparator = new MyComparator();
        lv_booklist.setAdapter(booklist_adapter);
        refrashBookListView();
    }

    private void refrashBookListView() {
        booklist.clear();
        listenerMap.clear();
        loadBooklist();
        booklist_adapter.notifyDataSetChanged();
    }

    /**
     * 读取总书目
     */
    private void loadBooklist() {
        String dir = getExternalFilesDir(null) + BOOKLIST_DIR;
        File file_dir = new File(dir);
        if (!file_dir.exists() || !file_dir.isDirectory()) {
            file_dir.mkdirs();
        }
        File[] files = file_dir.listFiles();
        if (files != null)
        {
            for (File bookfile : files) {
                if (bookfile.isDirectory())
                {
                    BookEntity entity = Utils.getBookDirInfo(bookfile);
                    booklist.add(entity);
                }
            }
            Collections.sort(booklist, booklist_comparator);
        }
    }

    @Override
    public void startAction() {

    }

    @Override
    public void onBackClicked() {
        finish();
    }

    @Override
    public void onClick(View view) {
        if (view == btn_add) {
            createBook();
        }
    }

    /**
     * 创建新书
     */
    private void createBook() {
        MakeDialogUtil.showInputDialog(this, "请输入书名", null, null, new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String param) {
                if (param != null && !"".equals(param.trim())) {
                    File bookfile = new File(getExternalFilesDir(null) + BOOKLIST_DIR + param);
                    if (bookfile.exists() && bookfile.isDirectory())
                    {
                        Toast.makeText(BookListActivity.this, "书名重复", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        bookfile.mkdirs();
                        refrashBookListView();
                    }
                }else{
                    Toast.makeText(BookListActivity.this, "书名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private class MyAdapter extends BaseAdapter {

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
            if (view == null) {
                view = inflater.inflate(R.layout.layout_booklist_item, null);
                Holder holder = new Holder();
                holder.bookname = view.findViewById(R.id.tv_booklist_item_bookname);
                holder.bookcount = view.findViewById(R.id.tv_booklist_item_bookcount);
                holder.booklasttime = view.findViewById(R.id.tv_booklist_item_booklasttime);
                view.setTag(holder);
            }
            final BookEntity item = getItem(i);
            Holder holder = (Holder) view.getTag();
            holder.bookname.setText("《" + item.getBookName() + "》");
            holder.bookcount.setText("共" + item.getBookCount() + "章");
            holder.booklasttime.setText("修改时间：" + format.format(item.getLastDate()));

            View.OnClickListener listener = listenerMap.get(i);
            if (listener == null) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String bookName = item.getBookName();
                        Intent intent = new Intent(BookListActivity.this, VolListActivity.class);
                        intent.putExtra("bookName", bookName);
                        startActivity(intent);
                    }
                };
                listenerMap.put(i, listener);
            }
            view.setOnClickListener(listenerMap.get(i));
            return view;
        }
    }

    private class Holder {
        private TextView bookname;
        private TextView bookcount;
        private TextView booklasttime;
    }

    private class MyComparator implements Comparator<BookEntity>
    {
        @Override
        public int compare(BookEntity bookEntity, BookEntity t1) {
            int result = 0;
            long lastDate = bookEntity.getLastDate();
            long lastDate1 = t1.getLastDate();
            if (lastDate > lastDate1)
            {
                result = -1;
            }else
            {
                result = 0;
            }
            return result;
        }
    }

}
