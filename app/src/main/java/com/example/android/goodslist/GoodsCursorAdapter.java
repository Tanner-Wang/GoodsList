package com.example.android.goodslist;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.goodslist.data.GoodsContract.GoodsEntry;

public class GoodsCursorAdapter extends CursorAdapter {


    public GoodsCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView amountTextView = (TextView) view.findViewById(R.id.amount);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        TextView salesVolumeTextView = (TextView) view.findViewById(R.id.sell);
        TextView toSellTextView = (TextView) view.findViewById(R.id.to_sell);
        ImageView pictureImageView = (ImageView) view.findViewById(R.id.list_goods_picture);

        int idColumnIndex = cursor.getColumnIndex(GoodsEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_NAME);
        int amountColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_AMOUNT);
        int priceColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PRICE);
        int salesVolumeColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_SALES_VOLUME);
        int pictureColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_IMAGE);

        String goodsItemId = cursor.getString(idColumnIndex);
        String goodsName = cursor.getString(nameColumnIndex);
        String goodsAmount = cursor.getString(amountColumnIndex);
        String goodsPrice = cursor.getString(priceColumnIndex);
        String goodsSalesVolume = cursor.getString(salesVolumeColumnIndex);
        byte[] in = cursor.getBlob(pictureColumnIndex);
        Bitmap bmpout = BitmapFactory.decodeByteArray(in, 0, in.length);

        final int mId = Integer.parseInt(goodsItemId);
        final int mAmount = Integer.parseInt(goodsAmount);
        final int mSalesVolume = Integer.parseInt(goodsSalesVolume);

        toSellTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mAmount > 0) {

                    Uri uri = ContentUris.withAppendedId(GoodsEntry.CONTENT_URI, (mId));
                    ContentValues values = new ContentValues();
                    values.put(GoodsEntry.COLUMN_GOODS_AMOUNT, mAmount - 1);
                    values.put(GoodsEntry.COLUMN_SALES_VOLUME, mSalesVolume + 1);
                    int rowsAffected = context.getContentResolver().update(uri, values, null, null);
                }
            }
        });

        // Update the TextViews with the attributes for the current goods
        nameTextView.setText(goodsName);
        amountTextView.setText(goodsAmount);
        priceTextView.setText(goodsPrice);
        salesVolumeTextView.setText(goodsSalesVolume);
        pictureImageView.setImageBitmap(bmpout);

    }


}
