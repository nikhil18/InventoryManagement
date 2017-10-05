package com.example.android.inventorymanagement.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.inventorymanagement.data.InventoryContract.inventoryEntry;

import static com.example.android.inventorymanagement.data.InventoryContract.inventoryEntry.COLUMN_PRODUCT_QUANTITY;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();


    private InventoryDbHelper mDbHelper;

    public static final int INVENTORY = 100;

    public static final int INVENTORY_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY, INVENTORY);

        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY, InventoryContract.PATH_INVENTORY + "/#", INVENTORY_ID);
    }

    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                cursor = database.query(inventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case INVENTORY_ID:
                selection = inventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(inventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        getContext().getContentResolver().notifyChange(uri, null);
        switch (match) {
            case INVENTORY:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        String name = values.getAsString(inventoryEntry.COLUMN_PRODUCT_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(inventoryEntry.COLUMN_PRODUCT_PRICE);
        if (price != null && price < 0) {
            throw new IllegalArgumentException("product requires a valid price");
        }

        Integer quantity = values.getAsInteger(COLUMN_PRODUCT_QUANTITY);
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("product requires a valid quantity");
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(inventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return updateInventory(uri, contentValues, selection, selectionArgs);
            case INVENTORY_ID:
                selection = inventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateInventory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateInventory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(inventoryEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(inventoryEntry.COLUMN_PRODUCT_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(inventoryEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity != null && quantity < 0) {
                throw new IllegalArgumentException("Product requires valid quantity");
            }
        }

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(inventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case INVENTORY:
                getContext().getContentResolver().notifyChange(uri, null);
                return database.delete(inventoryEntry.TABLE_NAME, selection, selectionArgs);
            case INVENTORY_ID:
                getContext().getContentResolver().notifyChange(uri, null);
                selection = inventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(inventoryEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case INVENTORY:
                return inventoryEntry.CONTENT_LIST_TYPE;
            case INVENTORY_ID:
                return inventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
