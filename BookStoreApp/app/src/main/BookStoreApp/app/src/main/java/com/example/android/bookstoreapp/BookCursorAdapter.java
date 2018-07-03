package com.example.android.bookstoreapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstoreapp.data.BookContract;

import javax.crypto.interfaces.PBEKey;

/**
 * Created by Aiden on 18/06/2018.
 */

public class BookCursorAdapter extends CursorAdapter {

    //construct new BookCursorAdapter
    public BookCursorAdapter(Context context, Cursor c){super(context, c, 0);}

    /**
     * makes new blank list item. No data is set (or bound) to views yet
     *
     * @param context, app context
     * @param cursor, cursor from which to get data. Cursor already moved to correct position
     * @param parent, parent to which the new view is attached
     * @return newly created list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent){
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    } /**
     * This method binds the book data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current book can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        //find views we want to modify
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView summaryTextView = (TextView) view.findViewById(R.id.summary);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        final int bookId = cursor.getInt(cursor.getColumnIndexOrThrow(BookContract.BookEntry._ID));

        //find columns for book attributes we want
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);

        //read book attributes from cursor for current book
        String bookName = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final String bookQuantity = cursor.getString(quantityColumnIndex);

        Button saleButton = view.findViewById(R.id.sale_btn);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity;
                quantity = Integer.parseInt(bookQuantity);
                if(quantity <= 0){
                    Toast.makeText(context, "Out of stock", Toast.LENGTH_SHORT).show();
                }else{
                    quantity -=1;
                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantity);
                    Uri bookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);
                    context.getContentResolver().update(bookUri, values, null, null);
                    quantityTextView.setText(bookQuantity);
                }
            }
        });

        //update textViews with book attributes
        nameTextView.setText(bookName);
        summaryTextView.setText("£" + bookPrice);
        quantityTextView.setText(bookQuantity);
    }
}
