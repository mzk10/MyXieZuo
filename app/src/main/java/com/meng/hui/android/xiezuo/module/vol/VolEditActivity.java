package com.meng.hui.android.xiezuo.module.vol;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.Constants;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.FontEntity;
import com.meng.hui.android.xiezuo.module.vol.entity.EditActionEntity;
import com.meng.hui.android.xiezuo.module.vol.entity.VolActionEntity;
import com.meng.hui.android.xiezuo.util.AsyncTaskBeta;
import com.meng.hui.android.xiezuo.util.FileUtil;
import com.meng.hui.android.xiezuo.util.LinkList;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;
import com.meng.hui.android.xiezuo.util.database.FontDao;
import com.meng.hui.android.xiezuo.util.pop.PopWindowUtil;
import com.meng.hui.android.xiezuo.util.pop.holder.BaseHolder;
import com.meng.hui.android.xiezuo.util.pop.holder.EditerSettingHolder;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by mzk10 on 2017/7/26.
 */

public class VolEditActivity extends MyActivity implements TextWatcher, View.OnKeyListener {

    private static final String TAG = "VolEditActivity";
    @BindView(R.id.btn_voledit_back)
    ImageView btn_voledit_back;
    @BindView(R.id.btn_voledit_copy)
    ImageView btn_voledit_copy;
    @BindView(R.id.btn_voledit_revert)
    ImageView btn_voledit_revert;
    @BindView(R.id.btn_voledit_unrevert)
    ImageView btn_voledit_unrevert;
    @BindView(R.id.et_voledit_content)
    EditText et_voledit_content;
    @BindView(R.id.tv_voledit_count)
    TextView tv_voledit_count;
    @BindView(R.id.background)
    LinearLayout background;
    @BindView(R.id.tv_vol_name)
    TextView tv_vol_name;
    @BindView(R.id.tv_vol_name_maohao)
    TextView tv_vol_name_maohao;

    private String vol_path;
    private VolActionEntity action;

    @Override
    public int bindLayout() {
        return R.layout.activity_voledit;
    }

    @Override
    public void initView() {
        refreshTheme();
        String vol_name = getIntent().getStringExtra("name");
        tv_vol_name.setText(vol_name);
    }

    @Override
    public void initData() {
        vol_path = getIntent().getStringExtra("vol_path");

        et_voledit_content.setOnKeyListener(this);

        loadRevertAction();
        flushRevertState();

        File file = new File(vol_path);
        String valContent = FileUtil.loadFileString(file);
        et_voledit_content.setText(valContent);
        try {
            Selection.setSelection(et_voledit_content.getText(), action.getLine());
        } catch (Exception e) {
            XiezuoDebug.e(TAG, e);
        }
        flushCount(valContent);
        et_voledit_content.addTextChangedListener(this);
    }

    /**
     * 刷新主题
     */
    private void refreshTheme() {
        refreshColor();
        refreshFont();
        refreshTextSize();
    }

    /**
     * 刷新颜色
     */
    private void refreshColor() {
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        int editbg = config.getInt("editbg", 1);
        if (editbg == 1) {
            background.setBackgroundColor(getResources().getColor(R.color.colorEditbg_black));
            et_voledit_content.setTextColor(getResources().getColor(R.color.colorGray));
            tv_vol_name.setTextColor(getResources().getColor(R.color.colorGray));
            tv_vol_name_maohao.setTextColor(getResources().getColor(R.color.colorGray));
        } else if (editbg == 2) {
            background.setBackgroundColor(getResources().getColor(R.color.colorEditbg_white));
            et_voledit_content.setTextColor(getResources().getColor(R.color.colorBlack));
            tv_vol_name.setTextColor(getResources().getColor(R.color.colorBlack));
            tv_vol_name_maohao.setTextColor(getResources().getColor(R.color.colorBlack));
        }
    }

    /**
     * 刷新字体
     */
    private void refreshFont() {
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        File externalFilesDir = getExternalFilesDir(null);
        int selectFont = config.getInt("selectFont", 0);
        if (selectFont != 0) {
            FontDao dao = new FontDao(VolEditActivity.this);
            FontEntity entity = dao.selectData(selectFont);
            String path = entity.getPath();
            String fname = Utils.getFilenameFromUrl(path);
            File ttf = new File(externalFilesDir + Constants.path.FONT_DIR, fname);
            if (ttf.exists()) {
                XiezuoDebug.i(TAG, "使用字体：" + entity.getName());
                Typeface fromFile = Typeface.createFromFile(ttf);
                et_voledit_content.setTypeface(fromFile);
                tv_vol_name.setTypeface(fromFile);
                tv_vol_name_maohao.setTypeface(fromFile);

            }
        } else {
            XiezuoDebug.i(TAG, "使用默认字体");
        }
    }

    /**
     * 刷新字号
     */
    private void refreshTextSize() {
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        int fontSize = config.getInt("fontSize", 18);
        et_voledit_content.setTextSize(fontSize);
        tv_vol_name.setTextSize(fontSize - 2);
        tv_vol_name_maohao.setTextSize(fontSize - 2);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void startAction() {

    }

    @Override
    public void onBackClicked() {
        finish();
    }

    @OnClick(R.id.btn_voledit_back)
    public void btn_voledit_back() {
        finish();
    }

    @OnClick(R.id.btn_voledit_copy)
    public void btn_voledit_copy() {
        String s = et_voledit_content.getText().toString();
        copyText(s);
    }

    @OnClick(R.id.btn_voledit_revert)
    public void btn_voledit_revert() {
        revertText();
    }

    @OnClick(R.id.btn_voledit_unrevert)
    public void btn_voledit_unrevert() {
        unRevertText();
    }

    @OnClick(R.id.btn_voledit_setting)
    public void btn_voledit_setting() {
        final PopWindowUtil.Builder builder = new PopWindowUtil.Builder(VolEditActivity.this);
        SharedPreferences config = getSharedPreferences("config", MODE_PRIVATE);
        int fontSize = config.getInt("fontSize", 18);
        PopWindowUtil.PopWindow settingWindow = builder.setCover(true)
                .setHasOpenAnim(true)
                .setHasCloseAnim(true)
                .setCancelable(false)
                .create(new EditerSettingHolder(getApplicationContext(), new BaseHolder.OnCloseListener<Integer, Integer, String>() {
                    @Override
                    public void onClick(boolean isClick, Integer param1, Integer param2, String param3) {
                        if (isClick){
                            if (param1 == 0){
                                refreshTextSize();
                            }else if (param1 == 1){
                                refreshColor();
                            }
                        }else {
                            builder.closeWindow();
                        }

                    }
                }));
        PopWindowUtil.getInstance().insertPop(settingWindow);
    }

    /**
     * 保存文本
     */
    private void saveText(String text) {
        AsyncTaskBeta<String, Void, Void> async = new AsyncTaskBeta<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... param) {
                String string = param[0];
                FileUtil.saveStringToFile(string, new File(vol_path));
                return null;
            }
        };
        async.execute(text);
        flushCount(text);
    }

    /**
     * 刷新字数
     *
     * @param content
     */
    private void flushCount(String content) {
        AsyncTaskBeta<String, Void, Integer> async = new AsyncTaskBeta<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... param) {
                int absLength = Utils.getStringAbsLength(param[0]);
                return absLength;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                tv_voledit_count.setText("字数：" + integer);
            }
        };
        async.execute(content);
    }

    /**
     * 将文字保存到剪贴板
     */
    private void copyText(String text) {
        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        // 将文本内容放到系统剪贴板里。
        if (cm != null) {
            cm.setPrimaryClip(ClipData.newPlainText(null, text));
            Toast.makeText(this, "内容复制成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "复制失败，请自行操作", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 撤销修改
     */
    private void revertText() {
        EditActionEntity entity = action.getRevertActionPool().removeLast();
        if (entity != null) {
            //保存当前状态到反撤销池
            Editable text = et_voledit_content.getText();
            String s = text.toString();
            int selectionEnd = Selection.getSelectionEnd(text);
            EditActionEntity current = new EditActionEntity();
            current.setContent(s);
            current.setLine(selectionEnd);
            action.getUnRevertActionPool().add(current);

            //撤销操作
            String content = entity.getContent();
            et_voledit_content.removeTextChangedListener(this);
            et_voledit_content.setText(content);
            saveText(content);
            Selection.setSelection(et_voledit_content.getText(), entity.getLine());
            et_voledit_content.addTextChangedListener(this);
            flushRevertState();
        }
    }

    /**
     * 反撤销
     */
    private void unRevertText() {
        EditActionEntity entity = action.getUnRevertActionPool().removeLast();
        if (entity != null) {
            //保存当前状态到撤销池
            Editable text = et_voledit_content.getText();
            String s = text.toString();
            int selectionEnd = Selection.getSelectionEnd(text);
            EditActionEntity current = new EditActionEntity();
            current.setContent(s);
            current.setLine(selectionEnd);
            action.getRevertActionPool().add(current);

            //反撤销操作
            String content = entity.getContent();
            et_voledit_content.removeTextChangedListener(this);
            et_voledit_content.setText(content);
            saveText(content);
            Selection.setSelection(et_voledit_content.getText(), entity.getLine());
            et_voledit_content.addTextChangedListener(this);
            flushRevertState();
        }
    }

    private void flushRevertState() {
        if (action.getRevertActionPool() != null && action.getRevertActionPool().size() > 0) {
            btn_voledit_revert.setEnabled(true);
        } else {
            btn_voledit_revert.setEnabled(false);
        }

        if (action.getUnRevertActionPool() != null && action.getUnRevertActionPool().size() > 0) {
            btn_voledit_unrevert.setEnabled(true);
        } else {
            btn_voledit_unrevert.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditActionEntity entity = new EditActionEntity();
        entity.setContent(charSequence.toString());
        entity.setLine(i);
        action.getRevertActionPool().add(entity);
        action.getUnRevertActionPool().removeAll();
        flushRevertState();
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
        saveText(editable.toString());
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
                handleEnter();
            }
            return true;
        }
        return false;
    }

    private void handleEnter() {
        //状态保存
        EditActionEntity entity = new EditActionEntity();
        Editable editable = et_voledit_content.getText();
        entity.setContent(editable.toString());
        int cursorLocation = Selection.getSelectionEnd(et_voledit_content.getText());
        entity.setLine(cursorLocation);
        action.getRevertActionPool().add(entity);
        //移除监听
        et_voledit_content.removeTextChangedListener(this);
        //修改文本内容
        Editable insert = editable.insert(cursorLocation, "\n     ");
        et_voledit_content.setText(insert);
        String content = insert.toString();
        saveText(content);
        int line = cursorLocation + 5;
        Selection.setSelection(et_voledit_content.getText(), line);
        //恢复监听
        et_voledit_content.addTextChangedListener(this);
        action.getUnRevertActionPool().removeAll();
        flushRevertState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveRevertAction();
    }

    /**
     * 保存撤销事件
     */
    private void saveRevertAction() {
        AsyncTaskBeta<Void, Void, Void> async = new AsyncTaskBeta<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... param) {
                int selectionStart = Selection.getSelectionStart(et_voledit_content.getText());
                action.setLine(selectionStart);
                FileUtil.saveSerializable(action, vol_path + Constants.VOLACTION_EXT_NAME);
                return null;
            }
        };
        async.execute();
    }

    /**
     * 读取保存过的撤销事件
     */
    private void loadRevertAction() {
        Object act = FileUtil.loadSerializable(vol_path + Constants.VOLACTION_EXT_NAME);
        if (act != null && act instanceof VolActionEntity) {
            action = (VolActionEntity) act;
        } else {
            action = new VolActionEntity();
        }

        {
            String pathrev = vol_path + ".rev";
            File file = new File(pathrev);
            if (file.exists()) {
                Object rev = FileUtil.loadSerializable(pathrev);
                if (rev != null && rev instanceof LinkList) {
                    LinkList<EditActionEntity> revertActionPool = (LinkList) rev;
                    action.setRevertActionPool(revertActionPool);
                }
                file.delete();
            }
        }
        {
            String pathunr = vol_path + ".unr";
            File file = new File(pathunr);
            if (file.exists()) {
                Object unr = FileUtil.loadSerializable(pathunr);
                if (unr != null && unr instanceof LinkList) {
                    LinkList<EditActionEntity> unRevertActionPool = (LinkList) unr;
                    action.setUnRevertActionPool(unRevertActionPool);
                }
                file.delete();
            }
        }
    }
}
