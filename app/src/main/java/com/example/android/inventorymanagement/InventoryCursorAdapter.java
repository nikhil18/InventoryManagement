package com.example.android.inventorymanagement;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventorymanagement.data.InventoryContract.inventoryEntry;

import static android.content.ContentValues.TAG;

public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView nameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView modelTextView = (TextView) view.findViewById(R.id.product_model);
        TextView priceTextView = (TextView) view.findViewById(R.id.product_price);
        TextView quantityTextView = (TextView) view.findViewById(R.id.product_current_quantity);
        ImageView photoImageView = (ImageView) view.findViewById(R.id.product_image);
        ImageButton buyImageButton = (ImageButton) view.findViewById(R.id.product_buy_button);

        final int productIdColumnIndex = cursor.getInt(cursor.getColumnIndex(inventoryEntry._ID));
        int nameColumnIndex = cursor.getColumnIndex(inventoryEntry.COLUMN_PRODUCT_NAME);
        int modelColumnIndex = cursor.getColumnIndex(inventoryEntry.COLUMN_PRODUCT_MODEL);
        int priceColumnIndex = cursor.getColumnIndex(inventoryEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(inventoryEntry.COLUMN_PRODUCT_QUANTITY);
        int photoColumnIndex = cursor.getColumnIndex(inventoryEntry.COLUMN_PRODUCT_PICTURE);

        String productName = cursor.getString(nameColumnIndex);
        String productModel = cursor.getString(modelColumnIndex);
        String productPrice = cursor.getString(priceColumnIndex);
        final int quantityProduct = cursor.getInt(quantityColumnIndex);
        String imageUriString = cursor.getString(photoColumnIndex);
        Uri productImageUri = Uri.parse(imageUriString);
        photoImageView.setImageURI(productImageUri);

        if (TextUtils.isEmpty(productModel)) {
            modelTextView.setVisibility(View.GONE);
        }

        nameTextView.setText(productName);
        modelTextView.setText(productModel);
        priceTextView.setText(productPrice);
        quantityTextView.setText(String.valueOf(quantityProduct));

        buyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(inventoryEntry.CONTENT_URI, productIdColumnIndex);
                adjustProductQuantity(context, productUri, quantityProduct);
            }
        });
    }

    private void adjustProductQuantity(Context context, Uri productUri, int currentQuantityInStock) {

        int newQuantityValue = (currentQuantityInStock >= 1) ? currentQuantityInStock - 1 : 0;

        if (currentQuantityInStock == 0) {
            Toast.makeText(context.getApplicationContext(), R.string.toast_out_of_stock_msg, Toast.LENGTH_SHORT).show();
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(inventoryEntry.COLUMN_PRODUCT_QUANTITY, newQuantityValue);
        int numRowsUpdated = context.getContentResolver().update(productUri, contentValues, null, null);
        if (numRowsUpdated > 0) {
            Log.i(TAG, context.getString(R.string.buy_msg_confirm));
        } else {
            Toast.makeText(context.getApplicationContext(), R.string.no_product_in_stock, Toast.LENGTH_SHORT).show();
            Log.e(TAG, context.getString(R.string.error_msg_stock_update));
        }


    }
}