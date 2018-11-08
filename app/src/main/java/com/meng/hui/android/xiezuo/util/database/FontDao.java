package com.meng.hui.android.xiezuo.util.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.meng.hui.android.xiezuo.entity.FontEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mzk10 on 2017/8/15.
 */

public class FontDao extends DataBaseDao<FontEntity>{

    public FontDao(Context context) {
        super(context);
    }

    @Override
    public void addData(FontEntity data) {
        ContentValues values = getContentValues(data);
        db.insert("font", null, values);
    }

    @Override
    public void deleteData(int id) {

    }

    @Override
    public void updateData(FontEntity data) {
        db.update("font", getContentValues(data), null, null);
    }

    @Override
    public FontEntity selectData(int id) {
        Cursor cursor = db.query("font", null, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        if (cursor.moveToNext())
        {
            FontEntity fontEntity = getFontEntity(cursor);
            return fontEntity;
        }
        return null;
    }

    @Override
    public List<FontEntity> listData(String column, int id) {
        return null;
    }

    public List<FontEntity> listData() {
        List<FontEntity> list = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from font;", null);
        while(cursor.moveToNext())
        {
            FontEntity entity = getFontEntity(cursor);
            list.add(entity);
        }
        return list;
    }

    @Override
    public int countData(String column, int id) {
        return 0;
    }

    private ContentValues getContentValues(FontEntity data) {
        ContentValues values = new ContentValues();
        values.put("id", data.getId());
        values.put("name", data.getName());
        values.put("path", data.getPath());
        values.put("isSelected", data.isSelected());
        values.put("isDownload", data.isDownload());
        return values;
    }

    private FontEntity getFontEntity(Cursor cursor) {
        FontEntity entity = new FontEntity();
        entity.setId(cursor.getInt(cursor.getColumnIndex("id")));
        entity.setName(cursor.getString(cursor.getColumnIndex("name")));
        entity.setPath(cursor.getString(cursor.getColumnIndex("path")));
        entity.setSelected(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isSelected"))));
        entity.setDownload(Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex("isDownload"))));
        return entity;
    }
}
