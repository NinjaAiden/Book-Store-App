package com.example.android.bookstoreapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import static java.security.AccessController.getContext;

/**
 * Created by Aiden on 18/06/2018.
 */

public class BookProvider extends ContentProvider {

    //tag for log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    //Uri matcher code for content uri for pets table
    private static final int BOOKS = 100;

    //Uri matcher code for content uri for single item in table
    private static final int BOOKS_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed on to constructor represents code to return for root uri.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer. Run the first time anything is called from this class
    static {
        /** calls to add uri go here, for all content URI patterns that the provider should recognise.
         * All paths added to the UriMatcher have a corresponding code to return when a match is found.
         *
         *content uri of the form "content://com.example.android.bookstoreapp.books" will map to the
         * integer code {@link #BOOKS}. This URI is used to provide access to multiple rows of the
         * books table
         */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);

        /**The content URI of the form "content://com.example.android.bookstoreapp/books/#" will map to the
         *integer code {@link #BOOKS_ID}. This URI is used to provide access to ONE single row
         * of the books table.
         *
         *In this case, the "#" wildcard is used where "#" can be substituted for an integer.
         * For example, "content://com.example.android.bookstoreapp/books/3" matches, but
         * "content://com.example.android.bookstoreapp/books" (without a number at the end) doesn't match.
         */
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOKS_ID);
    }

    //database helper object
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        //get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //this cursor will hold the result of the query
        Cursor cursor;

        //figure out if uri matcher can match uri to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the books table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOKS_ID:
                // For the BOOKS_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.bookstoreapp/books/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the books table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
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
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        //check name is not null
        String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Book requires a name");
        }

        //check the price is valid
        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        if (price == null){
            throw new IllegalArgumentException("Book requires a price");
        }

        //check there is a valid quantity
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null){
            throw new IllegalArgumentException("Book requires a quantity");
        }

        String supplier = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);
        if (supplier == null){
            throw new IllegalArgumentException("Book requires supplier name");
        }

        String phone = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_PHONE);
        if (phone == null){
            throw new IllegalArgumentException("Book supplier requires a phone number");
        }

        //get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        //insert new book with given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if(id == -1){
            Log.e(LOG_TAG, "failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        //return new uri with id of newly inserted row appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs){
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOKS_ID:
                // For the BOOKS_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update books in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    private int updateBook(Uri uri, ContentValues values, String selection,
                           String[] selectionArgs){

        // If the {@link BookEntry#COLUMN_BOOK_NAME} key is present,
        // check that the name value is not null.
        if(values.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME)){
            String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
            if (name == null){
                throw new IllegalArgumentException("Book requires a name");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_PRICE} key is present,
        // check that the name value is not null.
        if(values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PRICE)){
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            if (price != null && price < 0){
                throw new IllegalArgumentException("Book requires a valid price");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_QUANTITY} key is present,
        // check that the name value is not null.
        if(values.containsKey(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)){
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity != null && quantity < 0){
                throw new IllegalArgumentException("Book requires a valid quantity");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_SUPPLIER} key is present,
        // check that the name value is not null.
        if(values.containsKey(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER)){
            String supplier = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER);
            if (supplier == null){
                throw new IllegalArgumentException("Book requires a supplier");
            }
        }

        // If the {@link BookEntry#COLUMN_BOOK_PHONE} key is present,
        // check that the name value is not null.
        if(values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PHONE)){
            String phone = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_PHONE);
            if (phone == null){
                throw new IllegalArgumentException("Book supplier requires a phone number");
            }
        }

        //if there are no values to update, don't try to update database
        if (values.size() == 0){
            return 0;
        }

        //otherwise, get writable database to upload data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);

        if (rowsUpdated != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int delete(Uri uri, String selection, String [] selectionArgs){
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOKS_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if(rowsDeleted != 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match){
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOKS_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default: throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }

}
