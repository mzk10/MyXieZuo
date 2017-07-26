package com.meng.hui.android.xiezuo.activity;

import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meng.hui.android.xiezuo.R;
import com.meng.hui.android.xiezuo.core.MyActivity;
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

    private String valPath;
    private LinkList<String> editActionPool;
    private boolean isRevert;
    private boolean isEnter;


    @Override
    public void initView() {
        setContentView(R.layout.activity_voledit);

        btn_voledit_back=findViewById(R.id.btn_voledit_back);
        btn_voledit_save=findViewById(R.id.btn_voledit_save);
        btn_voledit_revert=findViewById(R.id.btn_voledit_revert);
        et_voledit_content = findViewById(R.id.et_voledit_content);
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
        et_voledit_content.addTextChangedListener(this);
        et_voledit_content.setOnKeyListener(this);

        File file = new File(valPath);
        String valContent = Utils.loadFileString(file);
        et_voledit_content.setText(valContent);
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
     * 保存
     */
    private void saveText() {
        String string = et_voledit_content.getText().toString();
        Utils.saveStringToFile(string, new File(valPath));
    }

    private void revertText() {
        String string = editActionPool.removeLast();
        if (string != null)
        {
            isRevert = true;
            et_voledit_content.setText(string);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (isRevert)
        {
            isRevert = false;
        }else
        {
            editActionPool.add(charSequence.toString());
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (isEnter)
        {
            isEnter = false;
            XiezuoDebug.i(TAG, "是回车");
            editable.append("    ");
            et_voledit_content.setText(editable);
        }
    }

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_ENTER && keyEvent.getAction() == KeyEvent.ACTION_UP) {
            isEnter = true;
            return true;
        }
        return false;
    }

}
