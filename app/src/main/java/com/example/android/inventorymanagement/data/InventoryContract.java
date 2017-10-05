package com.example.android.inventorymanagement.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.inventory";

    public static final String PATH_INVENTORY = "inventory";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class inventoryEntry implements BaseColumns {

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_INVENTORY;

        public static final String TABLE_NAME = "inventory";

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_INVENTORY);

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_MODEL = "model";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_PICTURE = "picture";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_SUPPLIER_NAME = "supplierName";
        public final static String COLUMN_SUPPLIER_EMAIL = "supplierEmail";
    }

}
