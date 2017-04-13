package com.example.android.goodslist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.goodslist.data.GoodsContract.GoodsEntry;


public final class GoodsDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "goods.db";
    private static final int DATABASE_VERSION = 1;

    public GoodsDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_GOODS_TABLE = "CREATE TABLE " + GoodsEntry.TABLE_NAME + " ("
                + GoodsEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + GoodsEntry.COLUMN_GOODS_NAME + " TEXT NOT NULL, "
                + GoodsEntry.COLUMN_GOODS_AMOUNT + " INTEGER DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_PRICE + " REAL NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_SALES_VOLUME + " INTEGER NOT NULL DEFAULT 0, "
                + GoodsEntry.COLUMN_GOODS_IMAGE + " BLOB)";
        db.execSQL(SQL_CREATE_GOODS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //The database is still at version 1, so there's nothing to do be done here.
    }
}
