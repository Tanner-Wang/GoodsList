package com.example.android.goodslist.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.goodslist.data.GoodsContract.GoodsEntry;

public class GoodsProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = GoodsProvider.class.getSimpleName();

    private GoodsDbHelper mDbHelper;

    private static final int GOODS = 1;

    private static final int GOODS_ID = 2;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(GoodsContract.CONTENT_AUTHORITY, GoodsContract.PATH_GOODS, GOODS);
        sUriMatcher.addURI(GoodsContract.CONTENT_AUTHORITY, GoodsContract.PATH_GOODS + "/#", GOODS_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new GoodsDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                cursor = database.query(GoodsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case GOODS_ID:
                selection = GoodsEntry._ID + "=?";
                //ContentUris.parseId(uri)会将uri最后一段转换成一个数字
                //String.valueOf()会将数字转换成一个字符串
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(GoodsEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return GoodsEntry.CONTENT_LIST_TYPE;
            case GOODS_ID:
                return GoodsEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return insertGoods(uri, values);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertGoods(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(GoodsEntry.COLUMN_GOODS_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Goods requires a name");
        }

        // Check that the amount is valid
        Integer amount = values.getAsInteger(GoodsEntry.COLUMN_GOODS_AMOUNT);
        if (amount != null && amount < 0) {
            throw new IllegalArgumentException("Goods requires valid amount");
        }

        // If the price is provided, check that it's greater than or equal to 0
        Float price = values.getAsFloat(GoodsEntry.COLUMN_GOODS_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Goods requires valid price");
        }

        // Check that the image is valid
        byte[] image = values.getAsByteArray(GoodsEntry.COLUMN_GOODS_IMAGE);
        if (image == null) {
            throw new IllegalArgumentException("Goods requires valid image");
        }

        // The sales volume is not need to check


        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Insert the new goods with the given values
        long id = database.insert(GoodsEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        // Track the number of rows that were deleted
        int rowsDeleted;
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(GoodsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case GOODS_ID:
                // Delete a single row given by the ID in the URI
                selection = GoodsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(GoodsEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GOODS:
                return updateGoods(uri, values, selection, selectionArgs);
            case GOODS_ID:
                // For the GOODS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = GoodsEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateGoods(uri, values, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update goods in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more goods).
     * Return the number of rows that were successfully updated.
     */
    private int updateGoods(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // check that the name value is not null.
        if (values.containsKey(GoodsEntry.COLUMN_GOODS_NAME)) {
            String name = values.getAsString(GoodsEntry.COLUMN_GOODS_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Goods requires a name");
            }
        }

        // check that the amount value is valid.
        if (values.containsKey(GoodsEntry.COLUMN_GOODS_AMOUNT)) {
            Integer amount = values.getAsInteger(GoodsEntry.COLUMN_GOODS_AMOUNT);
            if (amount != null && amount < 0) {
                throw new IllegalArgumentException("Goods requires valid amount");
            }
        }

        // check that the price value is valid.
        if (values.containsKey(GoodsEntry.COLUMN_GOODS_PRICE)) {
            // Check that the price is greater than or equal to 0 kg
            Float price = values.getAsFloat(GoodsEntry.COLUMN_GOODS_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // check that the image value is valid.
        if (values.containsKey(GoodsEntry.COLUMN_GOODS_IMAGE)) {
            byte[] image = values.getAsByteArray(GoodsEntry.COLUMN_GOODS_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Pet requires valid weight");
            }
        }

        // No need to check the sales volume

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(GoodsEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        // Returns the number of database rows affected by the update statement
        return rowsUpdated;
    }

}
