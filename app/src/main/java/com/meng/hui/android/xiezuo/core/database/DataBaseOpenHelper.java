package com.meng.hui.android.xiezuo.core.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseOpenHelper extends SQLiteOpenHelper
{
    
    public static DataBaseOpenHelper getInstance(Context context){
        return new DataBaseOpenHelper(context, "xiezuo", null, 1);
    }

    private static final String SQL_CREATE_FONT = "CREATE  TABLE \"main\".\"font\" (\"id\" INTEGER PRIMARY KEY  NOT NULL , \"name\" VARCHAR NOT NULL , \"path\" VARCHAR NOT NULL , \"isSelected\" VARCHAR NOT NULL , \"isDownload\" VARCHAR NOT NULL )";
    
    private DataBaseOpenHelper(Context context, String name, CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        //TODO 建表
        db.execSQL(SQL_CREATE_FONT);
    }

    @Override
    public void onOpen(SQLiteDatabase db)
    {
        super.onOpen(db);
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

    }

}
