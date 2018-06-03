package com.example.android.bookstoreapp;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Objects;

/**
 * Created by Aiden on 01/06/2018.
 */

public class EditorActivity extends AppCompatActivity {

    private EditText mNameEditText, mPriceEditText, mQuantityEditText, mSupplierEditText, mPhoneEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //find relevant views for input
        mNameEditText = findViewById(R.id.edit_book_name);
        mPriceEditText = findViewById(R.id.edit_book_price);
        mQuantityEditText = findViewById(R.id.edit_book_quantity);
        mSupplierEditText = findViewById(R.id.edit_supplier_name);
        mPhoneEditText = findViewById(R.id.edit_supplier_number);
    }

    private void insertBook(){
        // read from fields, use trim() to eliminate white space
        String nameString = mNameEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String supplierString = mSupplierEditText.getText().toString().trim();
        String supplierPhone = mPhoneEditText.getText().toString().trim();

        //check to make sure fields have been filled correctly
        if(nameString.matches("")){
            Toast.makeText(this, R.string.enter_book_name, Toast.LENGTH_SHORT).show();
        }else if(priceString.matches("")){
            Toast.makeText(this, R.string.enter_book_price, Toast.LENGTH_SHORT).show();
        }else if(quantityString.matches("")){
            Toast.makeText(this, R.string.enter_book_quantity, Toast.LENGTH_SHORT).show();
        }else if(supplierString.matches("")){
            Toast.makeText(this, R.string.enter_supplier_name, Toast.LENGTH_SHORT).show();
        }else if(supplierPhone.matches("")){
            Toast.makeText(this, R.string.enter_supplier_phone, Toast.LENGTH_SHORT).show();
        }else {

            int price = Integer.parseInt(priceString);
            int quantity = Integer.parseInt(quantityString);

            BookDbHelper mDbHelper = new BookDbHelper(this);

            SQLiteDatabase db = mDbHelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, price);
            values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
            values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER, supplierString);
            values.put(BookContract.BookEntry.COLUMN_BOOK_PHONE, supplierPhone);

            long newRowId = db.insert(BookContract.BookEntry.TABLE_NAME, null, values);
            String rowId = String.valueOf(newRowId);

            if (newRowId == -1) {
                Toast.makeText(this, R.string.entry_error, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.book_saved + rowId, Toast.LENGTH_SHORT).show();
            }
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            case R.id.action_delete:
                //do nothing for now
                return true;
            case android.R.id.home:
                //navigate back to parent activity
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}