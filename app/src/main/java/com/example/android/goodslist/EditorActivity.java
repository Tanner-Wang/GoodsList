package com.example.android.goodslist;

import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.goodslist.data.GoodsContract.GoodsEntry;

public class EditorActivity extends AppCompatActivity implements android.app.LoaderManager.LoaderCallbacks<Cursor> {

    ViewHolder holder;

    /**
     * Identifier for the goods data loader
     */
    private static final int EXISTING_GOODS_LOADER = 0;

    /**
     * Content URI for the existing goods (null if it's a new goods)
     */
    private Uri mCurrentGoodsUri;

    /**
     * Boolean flag that keeps track of whether the goods has been edited (true) or not (false)
     */
    private boolean mGoodsHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mGoodsHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        holder = new ViewHolder();

        Intent intent = getIntent();
        mCurrentGoodsUri = intent.getData();

        if (mCurrentGoodsUri == null) {
            setTitle(getString(R.string.editor_activity_title_new_goods));
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a goods that hasn't been created yet.)
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_activity_title_edit_goods));
            // Initialize a loader to read the goods data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_GOODS_LOADER, null, this);
        }

        // Find all relevant views that we will need to read user input from
        holder.mNameEditText = (EditText) findViewById(R.id.edit_goods_name);
        holder.mAmountEditText = (EditText) findViewById(R.id.edit_goods_amount);
        holder.mAmountEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        holder.mPriceEditText = (EditText) findViewById(R.id.edit_goods_price);
        holder.mPriceEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        holder.mAddAmountEditText = (EditText) findViewById(R.id.edit_add_goods_amount);
        holder.mAddAmountEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        holder.mCutBackAmountEditText = (EditText) findViewById(R.id.edit_cut_back_goods_amount);
        holder.mCutBackAmountEditText.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        holder.mSalesVolume = (TextView) findViewById(R.id.edit_sales_volume);
        holder.mOrderButton = (TextView) findViewById(R.id.order);
        holder.mOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:"));
                intent1.putExtra(Intent.EXTRA_EMAIL, "manager@gmail.com");
                intent1.putExtra(Intent.EXTRA_SUBJECT, "Order Summary");
                intent1.putExtra(Intent.EXTRA_TEXT, "Goods' name:" + holder.mNameEditText.getText()
                        + "\nOrderAmount:" + holder.mCutBackAmountEditText.getText());
                if (intent1.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent1);
                }
            }
        });

//         Setup OnTouchListeners on all the input fields, so we can determine if the user
//         has touched or modified them. This will let us know if there are unsaved changes
//         or not, if the user tries to leave the editor without saving.
        holder.mNameEditText.setOnTouchListener(mTouchListener);
        holder.mAmountEditText.setOnTouchListener(mTouchListener);
        holder.mPriceEditText.setOnTouchListener(mTouchListener);
        holder.mAddAmountEditText.setOnTouchListener(mTouchListener);
        holder.mCutBackAmountEditText.setOnTouchListener(mTouchListener);


    }

    private static class ViewHolder {
        EditText mNameEditText;
        EditText mAmountEditText;
        EditText mPriceEditText;
        EditText mAddAmountEditText;
        EditText mCutBackAmountEditText;
        TextView mSalesVolume;
        TextView mOrderButton;
    }

    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Since the editor shows all goods attributes, define a projection that contains
        // all columns from the goods table
        String[] projection = {
                GoodsEntry._ID,
                GoodsEntry.COLUMN_GOODS_NAME,
                GoodsEntry.COLUMN_GOODS_AMOUNT,
                GoodsEntry.COLUMN_GOODS_PRICE,
                GoodsEntry.COLUMN_SALES_VOLUME};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentGoodsUri,         // Query the content URI for the current goods
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of goods attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_NAME);
            int amountColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_AMOUNT);
            int priceColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_GOODS_PRICE);
            int salesVolumeColumnIndex = cursor.getColumnIndex(GoodsEntry.COLUMN_SALES_VOLUME);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int amount = cursor.getInt(amountColumnIndex);
            float price = cursor.getFloat(priceColumnIndex);
            int salesVolume = cursor.getInt(salesVolumeColumnIndex);

            // Update the views on the screen with the values from the database
            holder.mNameEditText.setText(name);
            holder.mAmountEditText.setText("" + amount);
            holder.mPriceEditText.setText("" + price);
            holder.mAddAmountEditText.setText("" + 0);
            holder.mCutBackAmountEditText.setText("" + 0);
            holder.mSalesVolume.setText("" + salesVolume);

        }
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        // If the loader is invalidated, clear out all the data from the input fields.
        holder.mNameEditText.setText("");
        holder.mAmountEditText.setText("");
        holder.mPriceEditText.setText("");
        holder.mSalesVolume.setText("");
        holder.mAddAmountEditText.setText(String.valueOf(0));
        holder.mCutBackAmountEditText.setText(String.valueOf(0));
    }


    private void saveGoods() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = holder.mNameEditText.getText().toString().trim();
        int amountInt = Integer.parseInt(holder.mAmountEditText.getText().toString().trim());
        String addString = holder.mAddAmountEditText.getText().toString().trim();
        String cutBackString = holder.mCutBackAmountEditText.getText().toString().trim();
        int addInt = Integer.parseInt(addString);
        int cutBackInt = Integer.parseInt(cutBackString);
        if (addInt > 0) {
            amountInt += addInt;
        }
        if (cutBackInt > 0) {
            amountInt -= cutBackInt;
        }
        String priceString = holder.mPriceEditText.getText().toString().trim();
        String salesVolumeString = holder.mSalesVolume.getText().toString().trim();

        // Check if this is supposed to be a new goods
        // and check if all the fields in the editor are blank
        if (mCurrentGoodsUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(String.valueOf(amountInt)) &&
                TextUtils.isEmpty(priceString)) {
            // Since no fields were modified, we can return early without creating a new goods.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }
        if (amountInt < 0) {
            Toast.makeText(this, "The amount is less than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and goods attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(GoodsEntry.COLUMN_GOODS_NAME, nameString);
        values.put(GoodsEntry.COLUMN_GOODS_AMOUNT, (String.valueOf(amountInt)));
        values.put(GoodsEntry.COLUMN_GOODS_PRICE, priceString);
        values.put(GoodsEntry.COLUMN_SALES_VOLUME, salesVolumeString);

        // Determine if this is a new or existing goods by checking if mCurrentGoodsUri is null or not
        if (mCurrentGoodsUri == null) {
            // This is a NEW goods, so insert a new goods into the provider,
            // returning the content URI for the new goods.
            Uri newUri = getContentResolver().insert(GoodsEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_goods_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_goods_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING goods, so update the goods with content URI: mCurrentGoodsUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentGoodsUri will already identify the correct row in the database that
            // we want to modify.
            int rowsAffected = getContentResolver().update(mCurrentGoodsUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.editor_update_goods_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_update_goods_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new goods, hide the "Delete" menu item.
        if (mCurrentGoodsUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //插入货物信息
                saveGoods();
                //退出EditorActivity，然后返回原Activity
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the goods hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mGoodsHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the goods hasn't changed, continue with handling back button press
        if (!mGoodsHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the goods.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this goods.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the goods.
                deleteGoods();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the goods.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the goods in the database.
     */
    private void deleteGoods() {
        // Only perform the delete if this is an existing goods.
        if (mCurrentGoodsUri != null) {
            // Call the ContentResolver to delete the goods at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentGoodsUri
            // content URI already identifies the goods that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentGoodsUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_goods_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_goods_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }


}
