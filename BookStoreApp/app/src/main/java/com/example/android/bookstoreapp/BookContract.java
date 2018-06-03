package com.example.android.bookstoreapp;

import android.provider.BaseColumns;

/**
 * Created by Aiden on 01/06/2018.
 */

public final class BookContract {

    private BookContract(){
        //empty constructor to avoid accidental instantiation
    }

    public static final class BookEntry implements BaseColumns{
        //name of database
        public final static String TABLE_NAME = "books";

        //product name (type: STRING)
        public final static String COLUMN_BOOK_NAME ="name";

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