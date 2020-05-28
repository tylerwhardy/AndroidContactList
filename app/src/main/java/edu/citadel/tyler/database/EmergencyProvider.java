package edu.citadel.tyler.database;

import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.content.*;

import java.sql.SQLException;


public class EmergencyProvider extends ContentProvider
{
    private static final String LOG_TAG = "EmergencyProvider)";

    private static final String AUTHORITY   = EmergencyContract.AUTHORITY;
    private static final String TABLE_NAME  = EmergencyContract.TABLE_NAME;
    private static final Uri    CONTENT_URI = EmergencyContract.CONTENT_URI;

    public static final int CONTACTS    = 1;   // for emergency contacts table
    public static final int CONTACTS_ID = 2;   // for a single emergency contact

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static
    {
        uriMatcher.addURI(AUTHORITY, "/" + TABLE_NAME, CONTACTS);
        uriMatcher.addURI(AUTHORITY, "/" + TABLE_NAME + "/#", CONTACTS_ID);
    }

    private EmergencyDbOpenHelper dbHandler;


    public EmergencyProvider()
    {
        super();
    }


    @Override
    public boolean onCreate()
    {
        dbHandler = new EmergencyDbOpenHelper(getContext());
        return false;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values)
    {
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        long id;

        switch (uriMatcher.match(uri))
        {
            case CONTACTS:
                id = db.insert(TABLE_NAME, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: \"" + uri + "\"");
        }

        if (id > 0)
        {
            Uri newContactUri = ContentUris.withAppendedId(CONTENT_URI, id);
            getContext().getContentResolver().notifyChange(newContactUri, null);
            return newContactUri;
        }
        else
        {
            Log.e(LOG_TAG, "Failed to add a record into " + uri);
            return uri;
        }

    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder)
    {
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        queryBuilder.setTables(TABLE_NAME);

        int uriType = uriMatcher.match(uri);

        switch (uriType)
        {
            case CONTACTS_ID:
                queryBuilder.appendWhere("_id = " + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: \"" + uri + "\"");
        }

        Cursor cursor = queryBuilder.query(dbHandler.getReadableDatabase(),
                projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs)
    {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int rowsUpdated;

        switch (uriType)
        {
            case CONTACTS:
                rowsUpdated = db.update(TABLE_NAME, values, selection, selectionArgs);
                break;
            case CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (selection == null || selection.length() == 0)
                    rowsUpdated = db.update(TABLE_NAME, values, "_id = " + id, null);
                else
                    rowsUpdated = db.update(TABLE_NAME, values, "_id = " + id
                            + " and " + selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: \"" + uri + "\"");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs)
    {
        int uriType = uriMatcher.match(uri);
        SQLiteDatabase db = dbHandler.getWritableDatabase();
        int rowsDeleted;

        switch (uriType)
        {
            case CONTACTS:
                rowsDeleted = db.delete(TABLE_NAME, selection, selectionArgs);
                break;
            case CONTACTS_ID:
                String id = uri.getLastPathSegment();
                if (selection == null || selection.length() == 0)
                    rowsDeleted = db.delete(TABLE_NAME, "_id = " + id, null);
                else
                    rowsDeleted = db.delete(TABLE_NAME, "_id = " + id
                            + " and " + selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: \"" + uri + "\"");
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri)
    {
        int uriType = uriMatcher.match(uri);

        switch (uriType)
        {
            case CONTACTS:
                return "vnd.android.cursor.dir/vnd.edu.citadel.android." + TABLE_NAME;
            case CONTACTS_ID:
                return "vnd.android.cursor.item/vnd.edu.citadel.android." + TABLE_NAME;
        }

        return null;
    }
}