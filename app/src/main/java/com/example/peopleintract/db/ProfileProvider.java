package com.example.peopleintract.db;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import androidx.annotation.NonNull;


public class ProfileProvider extends ContentProvider {

    public static final int CODE_PROFILE = 100;
    public static final int CODE_PROFILE_WITH_PROFILE_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ProfileDbHelper mOpenHelper;

    public static UriMatcher buildUriMatcher(){

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ProfileContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, ProfileContract.PATH_PROFILE, CODE_PROFILE);
        matcher.addURI(authority, ProfileContract.PATH_PROFILE + "/#", CODE_PROFILE_WITH_PROFILE_ID);

        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ProfileDbHelper(getContext());
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values){
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)){

            case CODE_PROFILE:
                db.beginTransaction();
                int rowsInsereted = 0;
                try{
                    for (ContentValues value : values){
                        long _id = db.insert(ProfileContract.ProfileEntry.TABLE_NAME,null, value);
                        if (_id != -1) {
                            rowsInsereted++;
                        }
                    }
                    db.setTransactionSuccessful();
                }finally {
                    db.endTransaction();
                }

                if (rowsInsereted > 0) {
                    getContext().getContentResolver().notifyChange(uri,null);
                }

                return rowsInsereted;

            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

        switch (sUriMatcher.match(uri)){

            case CODE_PROFILE: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        ProfileContract.ProfileEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(),uri);
        return cursor;
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType now");
    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues contentValues) {
        throw new RuntimeException("We are not implementing insert now");
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        int numRowsDeleted;

        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)){

            case CODE_PROFILE:

                numRowsDeleted = mOpenHelper.getWritableDatabase().delete(
                        ProfileContract.ProfileEntry.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int numRowsUpdated = 0;

        if (selection != null) {
            switch (sUriMatcher.match(uri)) {

                case CODE_PROFILE:

                    numRowsUpdated = mOpenHelper.getWritableDatabase().update(
                            ProfileContract.ProfileEntry.TABLE_NAME,
                            values,
                            selection,
                            selectionArgs);

                    break;

                default:
                    throw new UnsupportedOperationException("Unknown uri:" + uri);
            }
        }

        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return numRowsUpdated;
    }


    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}
