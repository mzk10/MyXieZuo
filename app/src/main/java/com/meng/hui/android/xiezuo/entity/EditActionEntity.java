package com.meng.hui.android.xiezuo.entity;

import java.io.Serializable;

/**
 * Created by mzk10 on 2017/7/27.
 */

public class EditActionEntity implements Serializable{

    private String content;
    private int line;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }
}
