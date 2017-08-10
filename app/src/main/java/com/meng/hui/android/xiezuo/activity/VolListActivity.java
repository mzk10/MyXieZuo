package com.meng.hui.android.xiezuo.activity;

import android.content.Intent;
import android.util.Log;
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
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.VolEntity;
import com.meng.hui.android.xiezuo.util.MakeDialogUtil;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by mzk10 on 2017/7/24.
 */

public class VolListActivity extends MyActivity {

    private static final String TAG = "VolListActivity";
    private static final String BOOKLIST_DIR = "/booklist/";

    private Button action_bar_btn_back;
    private Button action_bar_btn_menu;
    private TextView action_bar_tv_title;
    private Button btn_add;

    private ListView lv_booklist;
    private String bookName;
    private LayoutInflater inflater;
    private List<VolEntity> vollist;
    private SparseArray<View.OnClickListener> listenerMap;
    private SparseArray<View.OnLongClickListener> longlistenerMap;
    private MyAdapter vollist_adapter;
    private MyComparator vollist_comparator;

    @Override
    public void initView() {

        setContentView(R.layout.activity_vollist);

        action_bar_btn_back = findViewById(R.id.action_bar_btn_back);
        action_bar_btn_menu = findViewById(R.id.action_bar_btn_menu);
        action_bar_tv_title = findViewById(R.id.action_bar_tv_title);
        lv_booklist = findViewById(R.id.lv_vollist);
        btn_add = findViewById(R.id.btn_add);
    }

    @Override
    public void initData() {
        bookName = getIntent().getStringExtra("bookName");
        action_bar_tv_title.setText("《"+bookName+"》");
        action_bar_btn_back.setOnClickListener(this);
        action_bar_btn_menu.setVisibility(View.INVISIBLE);
        btn_add.setOnClickListener(this);

        inflater = LayoutInflater.from(this);
        vollist = new ArrayList<>();
        listenerMap = new SparseArray<>();
        longlistenerMap = new SparseArray<>();
        vollist_adapter = new MyAdapter();

        vollist_comparator = new MyComparator();
        lv_booklist.setAdapter(vollist_adapter);
        refrashVolListView();
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
        String dir = getExternalFilesDir(null) + BOOKLIST_DIR + bookName +"/";
        File file_dir = new File(dir);
        FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File file) {
                String name = file.getName();
                String fileExtensionName = Utils.getFileExtensionName(name);
                if (!file.isDirectory() && "txt".equals(fileExtensionName)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
        File[] volFiles = file_dir.listFiles(filter);
        if (volFiles != null)
        {
            for (File volfile : volFiles) {
                if (volfile.exists() && !volfile.isDirectory())
                {
                    VolEntity entity = new VolEntity();
                    entity.setValName(volfile.getName());
                    entity.setValPath(volfile.getPath());
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

    @Override
    public void onClick(View view) {
        if (view == action_bar_btn_back) {
            finish();
        } else if (view == btn_add) {
            createVol();
        }
    }

    private void createVol() {
        MakeDialogUtil.showInputDialog(this, "请输入章节标题", "", "", new MakeDialogUtil.OnInputCallBack() {
            @Override
            public void onCallBack(String param) {
                if (param != null && !"".equals(param.trim())) {
                    File volfile = new File(getExternalFilesDir(null) + BOOKLIST_DIR + bookName +"/"+ param + Constants.VOL_EXT_NAME);
                    if (volfile.exists() && volfile.isDirectory())
                    {
                        Toast.makeText(VolListActivity.this, "章节名重复", Toast.LENGTH_SHORT).show();
                    }else
                    {
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(volfile);
                            fos.flush();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }finally
                        {
                            try {
                                fos.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        refrashVolListView();
                    }
                }else{
                    Toast.makeText(VolListActivity.this, "章节名不能为空", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                holder.volname = view.findViewById(R.id.tv_vollist_item_volname);
                view.setTag(holder);
            }
            final VolEntity item = getItem(position);
            Holder holder = (Holder) view.getTag();
            holder.volname.setText(item.getValName());

            View.OnClickListener listener = listenerMap.get(position);
            if (listener == null) {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String valPath = item.getValPath();
                        Intent intent = new Intent(VolListActivity.this, VolEditActivity.class);
                        intent.putExtra("valPath", valPath);
                        startActivity(intent);
                    }
                };
                listenerMap.put(position, listener);
            }
            view.setOnClickListener(listener);

            View.OnLongClickListener longListener = longlistenerMap.get(position);
            if (longListener == null)
            {
                longListener = new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        MakeDialogUtil.showMenuDialog(VolListActivity.this, new String[]{"删除章节", "取消"}, new MakeDialogUtil.OnMenuCallBack() {
                            @Override
                            public void onCallBack(int i) {
                                if (i == 0)
                                {
                                    Utils.showToast(VolListActivity.this, "删除"+item.getValName());
                                    File file = new File(item.getValPath());
                                    if (file.exists())
                                    file.delete();
                                    File filebak = new File(item.getValPath()+Constants.VOLACTION_EXT_NAME);
                                    if (filebak.exists())
                                    filebak.delete();
                                    refrashVolListView();
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

    private class MyComparator implements Comparator<VolEntity>
    {
        @Override
        public int compare(VolEntity volEntity, VolEntity t1) {
            return 0;
        }
    }

}
