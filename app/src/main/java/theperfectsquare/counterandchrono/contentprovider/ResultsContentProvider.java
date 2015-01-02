package theperfectsquare.counterandchrono.contentprovider;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import java.util.Arrays;
import java.util.HashSet;

import theperfectsquare.counterandchrono.database.ResultsDataBaseHelper;
import theperfectsquare.counterandchrono.database.ResultsTable;

/**
 * Created by tai on 29/12/14.
 */
public class ResultsContentProvider extends ContentProvider {
    // database
    private ResultsDataBaseHelper resultsdatabase;
    
    // used for the UriMacher
    private static final int RESULTS = 10;
    private static final int RESULTS_ID = 20;
    private static final int RESULTS_CAT_ID = 30;

    private static final String AUTHORITY = "theperfectsquare.counterandchrono.contentprovider.results";

    private static final String BASE_PATH = "results";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
            + "/counterandchrono";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/results";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, RESULTS);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", RESULTS_ID);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/category/#", RESULTS_CAT_ID);
    }

    @Override
    public boolean onCreate() {
        resultsdatabase = new ResultsDataBaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(ResultsTable.TABLE_RESULTS);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case RESULTS:
                break;
            case RESULTS_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ResultsTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            case RESULTS_CAT_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(ResultsTable.COLUMN_CATEGORY_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = resultsdatabase.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection,
                selectionArgs, null, null, sortOrder);
        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = resultsdatabase.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case RESULTS:
                id = sqlDB.insert(ResultsTable.TABLE_RESULTS, null, values);
                break;
            case RESULTS_CAT_ID:
                id = sqlDB.insert(ResultsTable.TABLE_RESULTS, null, values);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = resultsdatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case RESULTS:
                rowsDeleted = sqlDB.delete(ResultsTable.TABLE_RESULTS, selection,
                        selectionArgs);
                break;
            case RESULTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ResultsTable.TABLE_RESULTS,
                            ResultsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ResultsTable.TABLE_RESULTS,
                            ResultsTable.COLUMN_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            case RESULTS_CAT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(ResultsTable.TABLE_RESULTS,
                            ResultsTable.COLUMN_CATEGORY_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(ResultsTable.TABLE_RESULTS,
                            ResultsTable.COLUMN_CATEGORY_ID + "=" + id
                                    + " and " + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = resultsdatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case RESULTS:
                rowsUpdated = sqlDB.update(ResultsTable.TABLE_RESULTS,
                        values,
                        selection,
                        selectionArgs);
                break;
            case RESULTS_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ResultsTable.TABLE_RESULTS,
                            values,
                            ResultsTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ResultsTable.TABLE_RESULTS,
                            values,
                            ResultsTable.COLUMN_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            case RESULTS_CAT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ResultsTable.TABLE_RESULTS,
                            values,
                            ResultsTable.COLUMN_CATEGORY_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(ResultsTable.TABLE_RESULTS,
                            values,
                            ResultsTable.COLUMN_CATEGORY_ID + "=" + id
                                    + " and "
                                    + selection,
                            selectionArgs);
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }
    private void checkColumns(String[] projection) {
        String[] available = {
                ResultsTable.COLUMN_CATEGORY_ID, ResultsTable.COLUMN_ID,
                ResultsTable.COLUMN_RESULT,ResultsTable.COLUMN_DATE };
        if (projection != null) {
            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
            // check if all columns which are requested are available
            if (!availableColumns.containsAll(requestedColumns)) {
                throw new IllegalArgumentException("Unknown columns in projection");
            }
        }
    }
}