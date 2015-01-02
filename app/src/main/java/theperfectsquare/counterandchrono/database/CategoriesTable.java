package theperfectsquare.counterandchrono.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tai on 28/12/14.
 */
public class CategoriesTable {
    // Database table
    public static final String TABLE_CATEGORIES = "categories";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TYPE = "type";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_DATE_CREATION = "date_creation";
    public static final String COLUMN_DATE_LASTUPDATE = "date_lastupdate";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_CATEGORIES
            + "("
            + COLUMN_ID + " integer primary key, "
            + COLUMN_TYPE + " text, "
            + COLUMN_NAME + " text,"
            + COLUMN_DATE_CREATION + " date"
            + COLUMN_DATE_LASTUPDATE + " date"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(CategoriesTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        onCreate(database);
    }
}
