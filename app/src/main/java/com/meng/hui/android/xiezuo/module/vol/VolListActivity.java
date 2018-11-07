package com.meng.hui.android.xiezuo.module.vol;

import android.content.Intent;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.module.vol.entity.VolEntity;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mzk10 on 2017/7/24.
 */

public class VolListActivity extends MyActivity implements View.OnClickListener {

    private static final String TAG = "VolListActivity";
    private static final String BOOKLIST_DIR = "/booklist/";
    @BindView(R.id.action_bar_tv_title)
    TextView action_bar_tv_title;
    @BindView(R.id.action_bar_btn_menu)
    ImageView action_bar_btn_menu;
    @BindView(R.id.lv_vollist)
    ListView lv_vollist;
    @BindView(R.id.action_bar_tv_title_l)
    TextView action_bar_tv_title_l;
    @BindView(R.id.action_bar_tv_title_r)
    TextView action_bar_tv_title_r;

    private String bookName;
    private LayoutInflater inflater;
    private List<VolEntity> vollist;
    private SparseArray<View.OnClickListener> listenerMap;
    private SparseArray<View.OnLongClickListener> longlistenerMap;
    private MyAdapter vollist_adapter;
    private MyComparator vollist_comparator;

    @Override
    public int bindLayout() {
        return R.layout.activity_vollist;
    }

    @Override
    public void initView() {
    }

    @Override
    public void initData() {
        bookName = getIntent().getStringExtra("bookName");
        action_bar_tv_title.setText(bookName);
        action_bar_tv_title_l.setText(getResources().getText(R.string.shumingzuo));
        action_bar_tv_title_r.setText(getResources().getText(R.string.shumingyou));
        action_bar_btn_menu.setVisibility(View.INVISIBLE);

        inflater = LayoutInflater.from(this);
        vollist = new ArrayList<>();
        listenerMap = new SparseArray<>();
        longlistenerMap = new SparseArray<>();
        vollist_adapter = new MyAdapter();

        vollist_comparator = new MyComparator();
        lv_vollist.setAdapter(vollist_adapter);
        refrashVolListView();

        View add = LayoutInflater.from(getApplicationContext()).inflate(R.layout.layout_vollist_item_add, lv_vollist, false);
        add.setOnClickListener(this);
        lv_vollist.addFooterView(add);
        vollist_adapter.notifyDataSetChanged();
    }

    @Override
    public void setListener() {

    }

    private void refrashVolListView() {
        vollist.clear();
        listenerMap.clear();
        longlistenerMap.clear();
        loadVollist();
        vollist_adapter.notifyDataSetChanged();
    }


    /**
     * 读取总书目
     */
    private void loadVollist() {
        String dir = getExternalFilesDir(null) + BOOKLIST_DIR + bookName + "/";
        File file_dir = new File(dir);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                String fileExtensionName = FileUtil.getFileExtensionName(name);
                if (!file.isDirectory() && "txt".equals(fileExtensionName)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] volFiles = file_dir.listFiles(filter);
        if (volFiles != null) {
            for (File volfile : volFiles) {
                if (volfile.exists() && !volfile.isDirectory()) {
                    VolEntity entity = new VolEntity();
                    entity.setVolName(volfile.getName());
                    entity.setVolPath(volfile.getPath());
                    vollist.add(entity);
                }
            }
            Collections.sort(vollist, vollist_comparator);
        }
    }


    @Override
    public void startAction() {

    }

    @Override
    public void onBackClicked() {
        finish();
    }

    @OnClick(R.id.action_bar_btn_back)
    public void action_bar_btn_back() {
        finish();
    }

    @OnClick(R.id.action_bar_btn_menu)
    public void action_bar_btn_menu() {
    }

    private void createVol() {
        MakeDialogUtil.showInputDialog(this, "请输入章节标题", "", "", new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String param) {
                if (param != null && !"".equals(param.trim())) {
                    File volfile = new File(getExternalFilesDir(null) + BOOKLIST_DIR + bookName + "/" + param + Constants.VOL_EXT_NAME);
                    if (volfile.exists() && !volfile.isDirectory()) {
                        Toast.makeText(VolListActivity.this, "章节名重复", Toast.LENGTH_SHORT).show();
                    } else {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(volfile);
                            fos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        refrashVolListView();
                    }
                } else {
                    Toast.makeText(VolListActivity.this, "章节名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        createVol();
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return vollist.size();
        }

        @Override
        public VolEntity getItem(int i) {
            return vollist.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = inflater.inflate(R.layout.layout_vollist_item, null);
                Holder holder = new Holder();
                holder.volname = (TextView) view.findViewById(R.id.tv_vollist_item_volname);
                view.setTag(holder);
            }
            final VolEntity item = getItem(position);
            Holder holder = (Holder) view.getTag();
            holder.volname.setText(item.getVolName());

            View.OnClickListener listener = listenerMap.get(position);
            if (listener == null) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String vol_path = item.getVolPath();
                        Intent intent = new Intent(VolListActivity.this, VolEditActivity.class);
                        intent.putExtra("vol_path", vol_path);
                        intent.putExtra("name", item.getVolName());
                        startActivity(intent);
                    }
                };
                listenerMap.put(position, listener);
            }
            view.setOnClickListener(listener);

            View.OnLongClickListener longListener = longlistenerMap.get(position);
            if (longListener == null) {
                longListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        MakeDialogUtil.showMenuDialog(VolListActivity.this, new String[]{"删除章节", "修改章节名称", "取消"}, new MakeDialogUtil.OnMenuCallBack() {
                            @Override
                            public void onCallBack(int i) {
                                if (i == 0) {
                                    Utils.showToast(VolListActivity.this, "删除" + item.getVolName());
                                    File file = new File(item.getVolPath());
                                    if (file.exists())
                                        file.delete();
                                    File filebak = new File(item.getVolPath() + Constants.VOLACTION_EXT_NAME);
                                    if (filebak.exists())
                                        filebak.delete();
                                    refrashVolListView();
                                } else if (i == 1) {
                                    //TODO 修改章节名称
                                    final File file = new File(item.getVolPath());
                                    final File filebak = new File(item.getVolPath() + Constants.VOLACTION_EXT_NAME);
                                    MakeDialogUtil.showInputDialog(VolListActivity.this, "请输入新的章节名", FileUtil.cutExtensionName(file.getName()), "", new MakeDialogUtil.OnInputCallBack() {
                                        @Override
                                        public void onCallBack(String param) {
                                            FileUtil.changeFileName(file, param);
                                            FileUtil.changeFileName(filebak, param + ".txt");
                                            refrashVolListView();
                                        }
                                    });
                                }
                            }
                        });
                        return true;
                    }
                };
                longlistenerMap.put(position, longListener);
            }
            view.setOnLongClickListener(longListener);
            return view;
        }
    }

    private class Holder {
        private TextView volname;
    }

    private class MyComparator implements Comparator<VolEntity> {
        @Override
        public int compare(VolEntity volEntity, VolEntity t1) {
            return 0;
        }
    }

}
