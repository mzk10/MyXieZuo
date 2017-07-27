package com.meng.hui.android.xiezuo.activity;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;
import com.meng.hui.android.xiezuo.entity.EditActionEntity;
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
    private ImageButton btn_voledit_save;
    private ImageButton btn_voledit_revert;
    private TextView tv_voledit_count;

    private String valPath;
    private LinkList<EditActionEntity> editActionPool;

    @Override
    public void initView() {
        setContentView(R.layout.activity_voledit);

        btn_voledit_back=findViewById(R.id.btn_voledit_back);
        btn_voledit_save=findViewById(R.id.btn_voledit_save);
        btn_voledit_revert=findViewById(R.id.btn_voledit_revert);
        et_voledit_content = findViewById(R.id.et_voledit_content);
        tv_voledit_count = findViewById(R.id.tv_voledit_count);
    }

    @Override
    public void initData() {
        valPath = getIntent().getStringExtra("valPath");
        Typeface tp = Typeface.createFromAsset(getAssets(), "yh.ttf");
        et_voledit_content.setTypeface(tp);
        editActionPool = new LinkList<>();

        btn_voledit_back.setOnClickListener(this);
        btn_voledit_save.setOnClickListener(this);
        btn_voledit_revert.setOnClickListener(this);
        et_voledit_content.setOnKeyListener(this);

        File file = new File(valPath);
        String valContent = Utils.loadFileString(file);
        et_voledit_content.setText(valContent);
        int absLength = Utils.getStringAbsLength(valContent);
        tv_voledit_count.setText("字数："+absLength);
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
        else if (view == btn_voledit_save)
        {
            saveText();
        }
        else if (view == btn_voledit_revert)
        {
            revertText();
        }
    }

    /**
     * 保存文本
     */
    private void saveText() {
        String string = et_voledit_content.getText().toString();
        Utils.saveStringToFile(string, new File(valPath));
    }

    private void revertText() {
        EditActionEntity entity = editActionPool.removeLast();
        if (entity != null)
        {
            et_voledit_content.removeTextChangedListener(this);
            et_voledit_content.setText(entity.getContent());
            Selection.setSelection(et_voledit_content.getText(), entity.getLine());
            et_voledit_content.addTextChangedListener(this);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        XiezuoDebug.i(TAG, "检测到修改");
        EditActionEntity entity = new EditActionEntity();
        entity.setContent(charSequence.toString());
        entity.setLine(i);
        editActionPool.add(entity);
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
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
        editActionPool.add(entity);
        //移除监听
        et_voledit_content.removeTextChangedListener(this);
        //修改文本内容
        Editable insert = editable.insert(cursorLocation, "\n    ");
        et_voledit_content.setText(insert);
        int line = cursorLocation + 5;
        Selection.setSelection(et_voledit_content.getText(), line);
        //恢复监听
        et_voledit_content.addTextChangedListener(this);
    }

}
