package com.example.android.bookstoreapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Aiden on 01/06/2018.
 */

public final class BookContract {

    //content authority string for app
    public static final String CONTENT_AUTHORITY = "com.example.android.bookstoreapp";

    public static final Uri BASE_CONTENT = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_BOOKS = "books";

    private BookContract() {
        //empty constructor to avoid accidental instantiation
    }

    //inner class, defining constant values for books database table. Each entry represents a single book
    public static final class BookEntry implements BaseColumns {

        //set uri for content path
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT, PATH_BOOKS);

        //MIME type of content for list of items
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //MIME type of content for single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //name of database
        public final static String TABLE_NAME = "books";

        //product name (type: STRING)
        public final static String COLUMN_BOOK_NAME = "name";

        //product price (type: INTEGER)
        public final static String COLUMN_BOOK_PRICE = "price";

        //product quantity (type: INTEGER)
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        //product supplier name (type: STRING)
        public final static String COLUMN_BOOK_SUPPLIER = "supplier";

        //product supplier phone number (type: STRING)
        public final static String COLUMN_BOOK_PHONE = "phone";
    }
}