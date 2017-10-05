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

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.ContentValues.TAG;

public class InventoryCursorAdapter extends CursorAdapter {

    private Context context;

    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
        this.context = context;
    }

    @Override
    public View newView(final Context context, Cursor cursor, ViewGroup parent) {
        final View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

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

        if (TextUtils.isEmpty(productModel)) {
            viewHolder.modelTextView.setVisibility(View.GONE);
        }

        viewHolder.nameTextView.setText(productName);
        viewHolder.modelTextView.setText(productModel);
        viewHolder.priceTextView.setText(productPrice);
        viewHolder.quantityTextView.setText(String.valueOf(quantityProduct));
        viewHolder.photoImageView.setImageURI(productImageUri);

        viewHolder.buyImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri productUri = ContentUris.withAppendedId(inventoryEntry.CONTENT_URI, productIdColumnIndex);
                adjustProductQuantity(context, productUri, quantityProduct);
            }
        });
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
    }

    static class ViewHolder {
        @BindView(R.id.product_name)
        TextView nameTextView;
        @BindView(R.id.product_model)
        TextView modelTextView;
        @BindView(R.id.product_price)
        TextView priceTextView;
        @BindView(R.id.product_current_quantity)
        TextView quantityTextView;
        @BindView(R.id.product_image)
        ImageView photoImageView;
        @BindView(R.id.product_buy_button)
        ImageButton buyImageButton;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
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