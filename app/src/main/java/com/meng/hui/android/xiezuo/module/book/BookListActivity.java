package com.meng.hui.android.xiezuo.module.book;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.FontEntity;
import com.meng.hui.android.xiezuo.module.book.adapter.BookListAdapter;
import com.meng.hui.android.xiezuo.module.book.entity.BookEntity;
import com.meng.hui.android.xiezuo.module.vol.VolListActivity;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;
import com.meng.hui.android.xiezuo.util.database.FontDao;
import com.meng.hui.android.xiezuo.util.pop.PopWindowUtil;
import com.meng.hui.android.xiezuo.util.pop.holder.BaseHolder;
import com.meng.hui.android.xiezuo.util.pop.holder.MainMenuHolder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


public class BookListActivity extends MyActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, View.OnClickListener {

    private static final String TAG = "BookListActivity";
    @BindView(R.id.action_bar_btn_back)
    ImageView action_bar_btn_back;
    @BindView(R.id.action_bar_tv_title)
    TextView action_bar_tv_title;
    @BindView(R.id.lv_booklist)
    ListView lv_booklist;

    private List<BookEntity> booklist;
    private BookListAdapter booklist_adapter;
    private MyComparator booklist_comparator;

    private SharedPreferences config;
    //private String[] menu;
    private List<FontEntity> fontlist;
    private FontDao dao;

    @Override
    public int bindLayout() {
        return R.layout.activity_booklist;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        //menu = new String[]{"更改字体大小", "更改字体", "编辑页面背景色", "取消"};
        config = getSharedPreferences("config", MODE_PRIVATE);
        fontlist = new ArrayList<>();
        dao = new FontDao(this);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void startAction() {
        action_bar_tv_title.setText(R.string.book_list);
        action_bar_btn_back.setVisibility(View.INVISIBLE);

        booklist = new ArrayList<>();
        booklist_adapter = new BookListAdapter(booklist, getApplicationContext());

        booklist_comparator = new MyComparator();
        lv_booklist.setAdapter(booklist_adapter);
        lv_booklist.setOnItemClickListener(this);
        lv_booklist.setOnItemLongClickListener(this);
        refrashBookListView();

        View add = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_booklist_item_add, lv_booklist, false);
        add.setOnClickListener(this);
        lv_booklist.addFooterView(add);
        booklist_adapter.notifyDataSetChanged();
    }

    private void refrashBookListView() {
        booklist.clear();
        loadBooklist();
        booklist_adapter.notifyDataSetChanged();
    }

    /**
     * 读取总书目
     */
    private void loadBooklist() {
        String dir = getExternalFilesDir(null) + Constants.path.BOOKLIST_DIR;
        File file_dir = new File(dir);
        if ((file_dir.exists() && file_dir.isDirectory()) || file_dir.mkdirs()) {
            File[] files = file_dir.listFiles();
            if (files != null) {
                for (File bookfile : files) {
                    if (bookfile.isDirectory()) {
                        BookEntity entity = FileUtil.getBookDirInfo(bookfile);
                        booklist.add(entity);
                    }
                }
                Collections.sort(booklist, booklist_comparator);
            }
        }
    }

    @Override
    public void onClick(View v) {
        btn_add();
    }

    @Override
    public void onBackClicked() {
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        BookListAdapter.BookListItemHolder holder = (BookListAdapter.BookListItemHolder) view.getTag();
        BookEntity item = holder.getItem();
        String bookName = item.getBookName();
        Intent intent = new Intent(BookListActivity.this, VolListActivity.class);
        intent.putExtra("bookName", bookName);
        startActivity(intent);
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, final View view, int position, long id) {
        MakeDialogUtil.showMenuDialog(BookListActivity.this, new String[]{"删除书籍", "取消"}, new MakeDialogUtil.OnMenuCallBack() {
            @Override
            public void onCallBack(int i) {
                if (i == 0) {
                    BookListAdapter.BookListItemHolder holder = (BookListAdapter.BookListItemHolder) view.getTag();
                    BookEntity item = holder.getItem();
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


    private FontEntity selectEntity;

    public void showChangeFontDialog(RadioGroup.OnCheckedChangeListener listener, final View.OnClickListener onClickListener) {
        if (fontlist == null || fontlist.size() == 0)
            return;
        final Dialog dialog = MakeDialogUtil.buildFullDialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View menu = inflater.inflate(R.layout.layout_dialog_checkbox, null);
        final RadioGroup check = (RadioGroup) menu.findViewById(R.id.rg_dialog_check);
        Button btn_confirm = (Button) menu.findViewById(R.id.btn_dialog_confirm);
        check.setOnCheckedChangeListener(listener);
        for (int i = 0; i < fontlist.size(); i++) {
            FontEntity entity = fontlist.get(i);
            RadioButton btn = (RadioButton) inflater.inflate(R.layout.layout_checkbox_item, null);
            btn.setText(entity.getName());
            btn.setId(entity.getId());
            btn.setTag(entity);
            check.addView(btn);
            if (entity.isSelected()) {
                check.check(entity.getId());
            }
            if (!entity.isDownload()) {
                btn.setTextColor(getResources().getColor(R.color.colorGray));
            }
        }
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                onClickListener.onClick(view);
            }
        });
        dialog.setContentView(menu);
        dialog.show();
    }

    @OnClick(R.id.action_bar_btn_menu)
    public void action_bar_btn_menu() {
        final PopWindowUtil.Builder builder = new PopWindowUtil.Builder(BookListActivity.this);
        PopWindowUtil.PopWindow popWindow = builder.setHasOpenAnim(true)
                .setCover(true)
                .setCancelable(true)
                .create(new MainMenuHolder(getApplicationContext(), new BaseHolder.OnCloseListener<Integer, Void, Void>() {
                    @Override
                    public void onClick(boolean isClick, Integer param1, Void param2, Void param3) {
                        switch (param1) {
                            case R.id.btn_main_menu_changetextsize:
                                changeTextSize();
                                break;
                            case R.id.btn_main_menu_changefont:
                                changeFont();
                                break;
                            case R.id.btn_main_menu_changebg:
                                changeBg();
                                break;
                            case R.id.btn_main_menu_cancel:
                                break;
                        }
                        builder.closeWindow();
                    }
                }));
        PopWindowUtil.getInstance().insertPop(popWindow);
    }

    /**
     * 字体大小
     */
    private void changeTextSize() {
        int fontSize = config.getInt("fontSize", -1);
        MakeDialogUtil.showInputDialog(this, "编辑页面字体大小", String.valueOf(fontSize), "", InputType.TYPE_CLASS_NUMBER, new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String param) {
                try {
                    int i = Integer.parseInt(param);
                    config.edit().putInt("fontSize", i).commit();
                } catch (Exception e) {
                    XiezuoDebug.e(TAG, e);
                }
            }
        });
    }

    /**
     * 更改字体
     */
    private void changeFont() {
        fontlist.clear();
        FontEntity def = new FontEntity();
        def.setId(0);
        def.setName("系统默认");
        def.setPath("");
        def.setDownload(true);
        fontlist.add(def);
        List<FontEntity> fontEntities = dao.listData();
        fontlist.addAll(fontEntities);
        final int selectFont = config.getInt("selectFont", 0);
        File dir = new File(getExternalFilesDir(null) + Constants.path.FONT_DIR);
        if (dir.exists() || dir.mkdirs()) {
        }

        for (int i = 0; i < fontlist.size(); i++) {
            //是否默认字体
            FontEntity entity = fontlist.get(i);
            int id = entity.getId();
            if (id == selectFont) {
                entity.setSelected(true);
            }
            //是否已下载
            String filename = Utils.getFilenameFromUrl(entity.getPath());
            XiezuoDebug.i(TAG, "filename=" + filename);
            File file = new File(dir, filename);
            if (file.exists()) {
                entity.setDownload(true);
            }
        }

        showChangeFontDialog(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton btn = (RadioButton) radioGroup.getChildAt(i);
                selectEntity = (FontEntity) btn.getTag();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectEntity != null) {
                    if (selectEntity.isDownload()) {
                        config.edit().putInt("selectFont", selectEntity.getId()).commit();
                        XiezuoDebug.i(TAG, "选中了：" + selectEntity.getName() + "字体");
                    } else {
                        XiezuoDebug.i(TAG, selectEntity.getName() + "字体" + " 未下载");
                        MakeDialogUtil.showConfirmDialog(BookListActivity.this, selectEntity.getName() + "字体" + " 未下载\n确认要下载吗？", null, null, new MakeDialogUtil.OnDialogConfirmLinsener() {
                            @Override
                            public void onConfirm(boolean isConfirm) {
                                if (isConfirm) {
                                    String url = Constants.url.BASE_URL + selectEntity.getPath();
                                    final String dir = new File(getExternalFilesDir(null), Constants.path.FONT_DIR).getPath();
                                    final MakeDialogUtil.DownPreContrl ctrl = MakeDialogUtil.showDownPre(BookListActivity.this);
                                    FileUtil.downloadFile(url, dir, new FileUtil.OnDownloadListener() {
                                        @Override
                                        public void onDownloadComplate(String path) {
                                            ctrl.close();
                                            if (path == null) {
                                                Utils.showToast(BookListActivity.this, "下载失败");
                                                String filename = FileUtil.getFilenameFromUrl(selectEntity.getPath());
                                                File file = new File(dir, filename);
                                                if (file.exists()) {
                                                    file.delete();
                                                }
                                            } else {
                                                Utils.showToast(BookListActivity.this, selectEntity.getName() + "字体下载成功");
                                                config.edit().putInt("selectFont", selectEntity.getId()).commit();
                                                XiezuoDebug.i(TAG, "选中了：" + selectEntity.getName() + "字体， 刚下载的");
                                            }
                                        }

                                        @Override
                                        public void onDownloadPro(int pro) {
                                            ctrl.setPro(pro);
                                        }
                                    });
                                }
                            }
                        }, false, null);
                    }
                }
            }
        });
    }

    /**
     * 更改背景色
     */
    private void changeBg() {
        //TODO
        showChangeBGDialog(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                bg_check_id = checkedId;
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (bg_check_id) {
                    case R.id.btn_checkbg_black:
                        config.edit().putInt("editbg", 1).commit();
                        break;
                    case R.id.btn_checkbg_white:
                        config.edit().putInt("editbg", 2).commit();
                        break;
                }
            }
        });
    }

    //@OnClick(R.id.btn_add)
    public void btn_add() {
        MakeDialogUtil.showInputDialog(this, "请输入书名", null, null, new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String param) {
                if (param != null && !"".equals(param.trim())) {
                    File bookfile = new File(getExternalFilesDir(null) + Constants.path.BOOKLIST_DIR + param);
                    if (bookfile.exists() && bookfile.isDirectory()) {
                        Toast.makeText(BookListActivity.this, "书名重复", Toast.LENGTH_SHORT).show();
                    } else {
                        bookfile.mkdirs();
                        refrashBookListView();
                    }
                } else {
                    Toast.makeText(BookListActivity.this, "书名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private int bg_check_id;

    public void showChangeBGDialog(RadioGroup.OnCheckedChangeListener listener, final View.OnClickListener onClickListener) {
        final Dialog dialog = MakeDialogUtil.buildFullDialog(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View menu = inflater.inflate(R.layout.layout_dialog_checkbox, null);
        final RadioGroup check = (RadioGroup) menu.findViewById(R.id.rg_dialog_check);
        Button btn_confirm = (Button) menu.findViewById(R.id.btn_dialog_confirm);
        int editbg = config.getInt("editbg", 1);
        check.setOnCheckedChangeListener(listener);
        {
            RadioButton btn = (RadioButton) inflater.inflate(R.layout.layout_checkbox_item, null);
            btn.setText("黑底白字");
            btn.setId(R.id.btn_checkbg_black);
            check.addView(btn);
            if (editbg == 1) {
                check.check(R.id.btn_checkbg_black);
            }
        }
        {
            RadioButton btn = (RadioButton) inflater.inflate(R.layout.layout_checkbox_item, null);
            btn.setText("白底黑字");
            btn.setId(R.id.btn_checkbg_white);
            check.addView(btn);
            if (editbg == 2) {
                check.check(R.id.btn_checkbg_white);
            }
        }

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
                onClickListener.onClick(view);
            }
        });
        dialog.setContentView(menu);
        dialog.show();
    }


    private class Holder {
        private TextView bookname;
        private TextView bookcount;
        private TextView booklasttime;
    }

    private class MyComparator implements Comparator<BookEntity> {
        @Override
        public int compare(BookEntity bookEntity, BookEntity t1) {
            int result;
            long lastDate = bookEntity.getLastDate();
            long lastDate1 = t1.getLastDate();
            if (lastDate > lastDate1) {
                result = -1;
            } else {
                result = 0;
            }
            return result;
        }
    }

}
