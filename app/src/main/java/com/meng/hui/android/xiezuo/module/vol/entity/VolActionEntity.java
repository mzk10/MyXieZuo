package com.meng.hui.android.xiezuo.module.vol.entity;

import com.meng.hui.android.xiezuo.util.LinkList;

import java.io.Serializable;

/**
 * Created by mzk10 on 2017/8/8.
 */

public class VolActionEntity implements Serializable{

    public VolActionEntity()
    {
        revertActionPool = new LinkList<>();
        unRevertActionPool = new LinkList<>();
    }

    private int line;
    private LinkList<EditActionEntity> revertActionPool;
    private LinkList<EditActionEntity> unRevertActionPool;

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public LinkList<EditActionEntity> getRevertActionPool() {
        return revertActionPool;
    }

    public void setRevertActionPool(LinkList<EditActionEntity> revertActionPool) {
        this.revertActionPool = revertActionPool;
    }

    public LinkList<EditActionEntity> getUnRevertActionPool() {
        return unRevertActionPool;
    }

    public void setUnRevertActionPool(LinkList<EditActionEntity> unRevertActionPool) {
        this.unRevertActionPool = unRevertActionPool;
    }
}
