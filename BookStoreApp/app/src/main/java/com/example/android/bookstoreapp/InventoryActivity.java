package com.example.android.bookstoreapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class InventoryActivity extends AppCompatActivity {

    private BookDbHelper mDbHelper;
    private static final String LOG_TAG = InventoryActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        //instantiate subclass of SQLiteOpenHelper to access database, current context is this activity
        mDbHelper = new BookDbHelper(this);
    }

    @Override
    protected void onStart(){
        super.onStart();
        displayDatabaseInfo();
    }

    //helper method to display database information in textview
    private void displayDatabaseInfo(){

        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //define projection to display necessary columns from database
        String projection[] = {
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER,
                BookContract.BookEntry.COLUMN_BOOK_PHONE};

        //perform query on table
        Cursor cursor = db.query(
                BookContract.BookEntry.TABLE_NAME,  //table name
                projection,                         //columns to return
                null,                       //columns for WHERE clause
                null,                   //values for WHERE clause
                null,                       //don't group rows
                null,                       //don't filter by row groups
                null);                      //sort order

        Log.i(LOG_TAG, "query request: " + cursor);

        TextView displayView = (TextView) findViewById(R.id.tv_book);

        try{
            //create header in text view
            displayView.setText("This table contains " + cursor.getCount() + " books\n\n");
            displayView.append(BookContract.BookEntry.COLUMN_BOOK_NAME + " - "
                    + BookContract.BookEntry.COLUMN_BOOK_PRICE + " - "
                    + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " - "
                    + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER + " - "
                    + BookContract.BookEntry.COLUMN_BOOK_PHONE + "\n");

            //find index of each column
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);
            int phoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PHONE);

            //Iterate through all returned rows in cursor
            while (cursor.moveToNext()){
                //use index to extract values of word at current row
                String currentName = cursor.getString(nameColumnIndex);
                int currentPrice = cursor.getInt(priceColumnIndex);
                int currentQuantity = cursor.getInt(quantityColumnIndex);
                String currentSupplier = cursor.getString(supplierColumnIndex);
                String currentPhone = cursor.getString(phoneColumnIndex);

                //display values from each column and row in cursor textview
                displayView.append("\n" + currentName + " - "
                        + currentPrice + " - "
                        + currentQuantity + " - "
                        + currentSupplier + " - "
                        + currentPhone);
            }
        }finally {
            //close cursor to release resources
            cursor.close();
        }
    }

    //helper method to insert dummy data into database. For debugging purposes only
    private void insertBook(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //create contentValues object. Column names are keys, book properties are values
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, "White Wolf");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, 10);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, 5);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER, "Bantam Press");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PHONE, "07742186275");

        //insert new row in database, returning id for the row.
        //first argument is table name, second is null ( no column for framework to use,
        //third is ContentValues object contining information
        long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        //inflate options menu from xml file, adds menu options to app bar
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    public void dropTable(){
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        //delete existing table
        String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + BookContract.BookEntry.TABLE_NAME + ";";
        db.execSQL(SQL_DROP_TABLE);

        //re-create empty table
        String SQL_CREATE_BOOK_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PRICE + " INTEGER NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PHONE + " TEXT NOT NULL); ";

        //execute statement
        db.execSQL(SQL_CREATE_BOOK_TABLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_insert_dummy_data:
                insertBook();
                displayDatabaseInfo();
                return true;
            case R.id.action_delete_all_books:
                dropTable();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}