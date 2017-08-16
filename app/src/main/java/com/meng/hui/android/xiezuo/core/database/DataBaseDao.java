package com.meng.hui.android.xiezuo.core.database;

import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;


public abstract class DataBaseDao<T>
{
    
    public SQLiteDatabase db;
    public Context context;
    
    public DataBaseDao(Context context){
        this.db = DataBaseOpenHelper.getInstance(context).getWritableDatabase();
        this.context=context;
    }
    
    public abstract void addData(T data);
    public abstract void deleteData(int id);
    public abstract void updateData(T data);
    public abstract T selectData(int id);
    public abstract List<T> listData(String column,int id);
    public abstract int countData(String column,int id);
    public abstract void closeDB();

}
