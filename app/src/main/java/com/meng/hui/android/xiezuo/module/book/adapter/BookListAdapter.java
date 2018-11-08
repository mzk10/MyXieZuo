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


}
