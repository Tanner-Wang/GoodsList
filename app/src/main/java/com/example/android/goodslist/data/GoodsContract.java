package com.example.android.goodslist.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Administrator on 2017/3/29.
 */

public final class GoodsContract {

    private GoodsContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.goodslist";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+CONTENT_AUTHORITY);

    public static final String PATH_GOODS = "goods";

    public static final class GoodsEntry implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_GOODS);
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOODS;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_GOODS;
        public static final String TABLE_NAME = "goods";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_GOODS_NAME = "name";
        public static final String COLUMN_GOODS_AMOUNT = "amount";
        public static final String COLUMN_GOODS_PRICE = "price";
        public static final String COLUMN_SALES_VOLUME = "sell";


    }

}
