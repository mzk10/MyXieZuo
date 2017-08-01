package com.meng.hui.android.xiezuo.activity;

import android.content.Context;
import android.graphics.Typeface;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.EditActionEntity;
import com.meng.hui.android.xiezuo.util.AsyncTaskBeta;
import com.meng.hui.android.xiezuo.util.LinkList;
import com.meng.hui.android.xiezuo.util.Utils;
import com.meng.hui.android.xiezuo.util.XiezuoDebug;

import java.io.File;

/**
 * Created by mzk10 on 2017/7/26.
 */

public class VolEditActivity extends MyActivity implements TextWatcher, View.OnKeyListener {

    private static final String TAG = "VolEditActivity";

    private EditText et_voledit_content;
    private ImageButton btn_voledit_back;
    private ImageButton btn_voledit_copy;
    private ImageButton btn_voledit_revert;
    private ImageButton btn_voledit_unrevert;
    private TextView tv_voledit_count;

    private String valPath;
    private LinkList<EditActionEntity> revertActionPool;
    private LinkList<EditActionEntity> unRevertActionPool;


    @Override
    public void initView() {
        setContentView(R.layout.activity_voledit);

        btn_voledit_back=findViewById(R.id.btn_voledit_back);
        btn_voledit_copy = findViewById(R.id.btn_voledit_copy);
        btn_voledit_revert=findViewById(R.id.btn_voledit_revert);
        btn_voledit_unrevert =findViewById(R.id.btn_voledit_unrevert);
        et_voledit_content = findViewById(R.id.et_voledit_content);
        tv_voledit_count = findViewById(R.id.tv_voledit_count);
    }

    @Override
    public void initData() {
        valPath = getIntent().getStringExtra("valPath");
        Typeface tp = Typeface.createFromAsset(getAssets(), "yh.ttf");
        et_voledit_content.setTypeface(tp);

        btn_voledit_back.setOnClickListener(this);
        btn_voledit_copy.setOnClickListener(this);
        btn_voledit_revert.setOnClickListener(this);
        btn_voledit_unrevert.setOnClickListener(this);
        et_voledit_content.setOnKeyListener(this);

        loadRevertAction();

        flushRevertState();

        File file = new File(valPath);
        String valContent = Utils.loadFileString(file);
        et_voledit_content.setText(valContent);
        flushCount(valContent);
        et_voledit_content.addTextChangedListener(this);
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
        if (view == btn_voledit_back)
        {
            finish();
        }
        else if (view == btn_voledit_copy)
        {
            String s = et_voledit_content.getText().toString();
            copyText(s);
        }
        else if (view == btn_voledit_revert)
        {
            revertText();
        }
        else if (view == btn_voledit_unrevert)
        {
            unRevertText();
        }

    }

    /**
     * 保存文本
     */
    private void saveText(String text) {
        AsyncTaskBeta<String,Void,Void> async = new AsyncTaskBeta<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... param) {
                String string = param[0];
                Utils.saveStringToFile(string, new File(valPath));
                return null;
            }
        };
        async.execute(text);
        flushCount(text);
    }

    /**
     * 刷新字数
     * @param content
     */
    private void flushCount(String content)
    {
        AsyncTaskBeta<String, Void, Integer> async = new AsyncTaskBeta<String, Void, Integer>() {
            @Override
            protected Integer doInBackground(String... param) {
                int absLength = Utils.getStringAbsLength(param[0]);
                return absLength;
            }

            @Override
            protected void onPostExecute(Integer integer) {
                super.onPostExecute(integer);
                tv_voledit_count.setText("字数："+integer);
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
        cm.setText(text);
        Toast.makeText(this, "内容复制成功", Toast.LENGTH_SHORT).show();
    }

    /**
     * 撤销修改
     */
    private void revertText() {
        EditActionEntity entity = revertActionPool.removeLast();
        if (entity != null)
        {
            //保存当前状态到反撤销池
            Editable text = et_voledit_content.getText();
            String s = text.toString();
            int selectionEnd = Selection.getSelectionEnd(text);
            EditActionEntity current = new EditActionEntity();
            current.setContent(s);
            current.setLine(selectionEnd);
            unRevertActionPool.add(current);

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
        EditActionEntity entity = unRevertActionPool.removeLast();
        if (entity != null)
        {
            //保存当前状态到撤销池
            Editable text = et_voledit_content.getText();
            String s = text.toString();
            int selectionEnd = Selection.getSelectionEnd(text);
            EditActionEntity current = new EditActionEntity();
            current.setContent(s);
            current.setLine(selectionEnd);
            revertActionPool.add(current);

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

    private void flushRevertState()
    {
        if (revertActionPool!=null && revertActionPool.size()>0)
        {
            btn_voledit_revert.setEnabled(true);
        }else{
            btn_voledit_revert.setEnabled(false);
        }

        if (unRevertActionPool!=null && unRevertActionPool.size()>0)
        {
            btn_voledit_unrevert.setEnabled(true);
        }else{
            btn_voledit_unrevert.setEnabled(false);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        EditActionEntity entity = new EditActionEntity();
        entity.setContent(charSequence.toString());
        entity.setLine(i);
        revertActionPool.add(entity);
        unRevertActionPool.removeAll();
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
        if (keyCode == KeyEvent.KEYCODE_ENTER)
        {
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
        revertActionPool.add(entity);
        //移除监听
        et_voledit_content.removeTextChangedListener(this);
        //修改文本内容
        Editable insert = editable.insert(cursorLocation, "\n    ");
        et_voledit_content.setText(insert);
        String content = insert.toString();
        saveText(content);
        int line = cursorLocation + 5;
        Selection.setSelection(et_voledit_content.getText(), line);
        //恢复监听
        et_voledit_content.addTextChangedListener(this);
        unRevertActionPool.removeAll();
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
    private void saveRevertAction()
    {
        AsyncTaskBeta<Void, Void, Void> async = new AsyncTaskBeta<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... param) {
                Utils.saveSerializable(revertActionPool, valPath+".rev");
                Utils.saveSerializable(unRevertActionPool, valPath+".unr");
                return null;
            }
        };
        async.execute();
    }

    /**
     * 读取保存过的撤销事件
     */
    private void loadRevertAction()
    {
        Object rev = Utils.loadSerializable(valPath + ".rev");
        if (rev!=null && rev instanceof LinkList){
            revertActionPool = (LinkList) rev;
        }else{
            revertActionPool = new LinkList<>();
        }

        Object unr = Utils.loadSerializable(valPath + ".unr");
            if (unr!=null && unr instanceof LinkList){
            unRevertActionPool = (LinkList) unr;
        }else{
            unRevertActionPool = new LinkList<>();
        }
    }
}
