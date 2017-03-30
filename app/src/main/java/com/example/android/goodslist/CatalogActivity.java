package com.example.android.goodslist;

import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.goodslist.data.GoodsContract.GoodsEntry;

public class CatalogActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    static ViewHolder holder;

    private static final int GOODS_LOADER = 0;

    GoodsCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        holder = new ViewHolder();

        // Setup FAB to open EditorActivity
        holder.fab = (TextView) findViewById(R.id.fab);
        holder.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        holder.goodsListView = (ListView) findViewById(R.id.list_view_goods);
        holder.emptyView = findViewById(R.id.empty_view);
        holder.goodsListView.setEmptyView(holder.emptyView);

        mCursorAdapter = new GoodsCursorAdapter(this, null);
        holder.goodsListView.setAdapter(mCursorAdapter);

        holder.goodsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                //形成被点击的特定goods的Content Uri并将id包含在其后面
                Uri currentGoodsUri = ContentUris.withAppendedId(GoodsEntry.CONTENT_URI, id);
                intent.setData(currentGoodsUri);
                startActivity(intent);
            }
        });


        getLoaderManager().initLoader(GOODS_LOADER, null, this);
    }

    static class ViewHolder {
        TextView fab;
        ListView goodsListView;
        View emptyView;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                GoodsEntry._ID,
                GoodsEntry.COLUMN_GOODS_NAME,
                GoodsEntry.COLUMN_GOODS_AMOUNT,
                GoodsEntry.COLUMN_GOODS_PRICE,
                GoodsEntry.COLUMN_SALES_VOLUME,
        };

        return new CursorLoader(this,
                GoodsEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert new goods" menu option
            case R.id.action_insert_new_goods:
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllGoods();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteAllGoods() {
        int rowsDeleted = getContentResolver().delete(GoodsEntry.CONTENT_URI, null, null);
    }


}
