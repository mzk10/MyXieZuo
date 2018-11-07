package com.meng.hui.android.xiezuo.module.book.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.module.book.entity.BookEntity;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookListAdapter extends BaseAdapter {

    private List<BookEntity> booklist;
    private SimpleDateFormat format;
    private Context context;

    public BookListAdapter(List<BookEntity> booklist, Context context) {
        this.booklist = booklist;
        this.context = context;
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
        if (view == null) {
            BookListItemHolder holder = new BookListItemHolder(context);
            view = holder.view;
            view.setTag(holder);
        }

        if (format==null){
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        }

        final BookEntity item = getItem(i);
        BookListItemHolder holder = (BookListItemHolder) view.getTag();
        holder.setItem(item);
        holder.tv_booklist_item_bookname.setText(item.getBookName());
        holder.tv_booklist_item_bookcount.setText(String.valueOf(item.getBookCount()));
        holder.tv_booklist_item_booklasttime.setText(format.format(item.getLastDate()));

        return view;
    }

    public class BookListItemHolder {

        View view;
        @BindView(R.id.tv_booklist_item_bookname)
        TextView tv_booklist_item_bookname;
        @BindView(R.id.tv_booklist_item_bookcount)
        TextView tv_booklist_item_bookcount;
        @BindView(R.id.tv_booklist_item_booklasttime)
        TextView tv_booklist_item_booklasttime;

        BookEntity item;

        BookListItemHolder(Context context) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_booklist_item, null, false);
            ButterKnife.bind(this, view);
        }

        public BookEntity getItem() {
            return item;
        }

        public void setItem(BookEntity item) {
            this.item = item;
        }
    }


    /*

        {
            View.OnLongClickListener longlistener = longlistenerMap.get(i);
            if (longlistener == null) {
                longlistener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        MakeDialogUtil.showMenuDialog(BookListActivity.this, new String[]{"删除书籍", "取消"}, new MakeDialogUtil.OnMenuCallBack() {
                            @Override
                            public void onCallBack(int i) {
                                if (i == 0) {
                                    String bookName = item.getBookName();
                                    File bookfile = new File(getExternalFilesDir(null) + Constants.path.BOOKLIST_DIR + bookName);
                                    if (bookfile.exists() && bookfile.isDirectory()) {
                                        File[] files = bookfile.listFiles();
                                        if (files != null) {
                                            for (File file : files) {
                                                file.delete();
                                            }
                                        }
                                        boolean delete = bookfile.delete();
                                        if (delete) {
                                            Toast.makeText(BookListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                            refrashBookListView();
                                        } else {
                                            Toast.makeText(BookListActivity.this, "删除失败，请等待下一版本解决", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            }
                        });
                        return true;
                    }
                };
                longlistenerMap.put(i, longlistener);
            }
            view.setOnLongClickListener(longlistener);
        }
    */



}
