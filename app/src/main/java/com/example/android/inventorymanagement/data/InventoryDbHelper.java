package com.example.android.inventorymanagement.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.android.inventorymanagement.data.InventoryContract.inventoryEntry;

public class InventoryDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = InventoryDbHelper.class.getSimpleName();

    public static final String COMMA_SEP = ", ";

    public static final int DATABASE_VERSION = 1;

    public static final String DATABASE_NAME = "inventory.db";

    public static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + inventoryEntry.TABLE_NAME + " ("
            + inventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + inventoryEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL" + COMMA_SEP
            + inventoryEntry.COLUMN_PRODUCT_MODEL + " TEXT" + COMMA_SEP
            + inventoryEntry.COLUMN_PRODUCT_PRICE + " REAL NOT NULL" + COMMA_SEP
            + inventoryEntry.COLUMN_SUPPLIER_NAME + " TEXT" + COMMA_SEP
            + inventoryEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL" + COMMA_SEP
            + inventoryEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0" + COMMA_SEP
            + inventoryEntry.COLUMN_PRODUCT_PICTURE + " TEXT" +")";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + inventoryEntry.TABLE_NAME;

    public InventoryDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
        Log.v(LOG_TAG, SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    }
}
