package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Aiden on 01/06/2018.
 */

public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getName();

    //name of database file
    private static final String DATABASE_NAME = "inventory.db";

    //Database version
    private static final int DATABASE_VERSION = 1;

    //create new instance of {@link bookDbHelper}
    //@param context of app
    public BookDbHelper(Context context)
    {super(context, DATABASE_NAME, null, DATABASE_VERSION);}

    //called when database is created for first time

    @Override
    public void onCreate(SQLiteDatabase db){
        //create string that contains SQL statement to create table
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PHONE + " TEXT NOT NULL); ";

        //execute statement
        db.execSQL(SQL_CREATE_BOOK_TABLE);
        Log.i(LOG_TAG, "table created: " + SQL_CREATE_BOOK_TABLE);
    }

    //called on database upgrade
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        //version 1, upgrade unnecessary
    }
}