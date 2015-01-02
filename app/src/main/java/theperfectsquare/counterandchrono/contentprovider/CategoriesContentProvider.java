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

import theperfectsquare.counterandchrono.database.CategoriesDataBaseHelper;
import theperfectsquare.counterandchrono.database.CategoriesTable;
import theperfectsquare.counterandchrono.database.ResultsDataBaseHelper;

/**
 * Created by tai on 28/12/14.
 */
public class CategoriesContentProvider extends ContentProvider {
    // database
    private CategoriesDataBaseHelper categorydatabase;

    // used for the UriMacher
    private static final int CATEGORIES = 10;
    private static final int CATEGORIES_ID = 20;

    private static final String AUTHORITY = "theperfectsquare.counterandchrono.contentprovider.categories";

    private static final String BASE_PATH = "categories";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/" + BASE_PATH);

    public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/counterandchrono";
    public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
            + "/categories";

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, BASE_PATH, CATEGORIES);
        sURIMatcher.addURI(AUTHORITY, BASE_PATH + "/#", CATEGORIES_ID);
    }



    @Override
    public boolean onCreate() {
        categorydatabase = new CategoriesDataBaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        checkColumns(projection);

        // Set the table
        queryBuilder.setTables(CategoriesTable.TABLE_CATEGORIES);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case CATEGORIES:
                break;
            case CATEGORIES_ID:
                // adding the ID to the original query
                queryBuilder.appendWhere(CategoriesTable.COLUMN_ID + "="
                        + uri.getLastPathSegment());
                break;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = categorydatabase.getWritableDatabase();
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
        SQLiteDatabase sqlDB = categorydatabase.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case CATEGORIES:
                id = sqlDB.insert(CategoriesTable.TABLE_CATEGORIES, null, values);
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
        SQLiteDatabase sqlDB = categorydatabase.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {
            case CATEGORIES:
                rowsDeleted = sqlDB.delete(CategoriesTable.TABLE_CATEGORIES, selection,
                        selectionArgs);
                break;
            case CATEGORIES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = sqlDB.delete(CategoriesTable.TABLE_CATEGORIES,
                            CategoriesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsDeleted = sqlDB.delete(CategoriesTable.TABLE_CATEGORIES,
                            CategoriesTable.COLUMN_ID + "=" + id
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
        SQLiteDatabase sqlDB = categorydatabase.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {
            case CATEGORIES:
                rowsUpdated = sqlDB.update(CategoriesTable.TABLE_CATEGORIES,
                        values,
                        selection,
                        selectionArgs);
                break;
            case CATEGORIES_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(CategoriesTable.TABLE_CATEGORIES,
                            values,
                            CategoriesTable.COLUMN_ID + "=" + id,
                            null);
                } else {
                    rowsUpdated = sqlDB.update(CategoriesTable.TABLE_CATEGORIES,
                            values,
                            CategoriesTable.COLUMN_ID + "=" + id
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
        String[] available = { CategoriesTable.COLUMN_DATE_LASTUPDATE,
                CategoriesTable.COLUMN_DATE_CREATION, CategoriesTable.COLUMN_ID,
                CategoriesTable. COLUMN_NAME,CategoriesTable.COLUMN_TYPE };
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
