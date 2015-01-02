package theperfectsquare.counterandchrono.database;

import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by tai on 28/12/14.
 */
public class ResultsTable {
    // Database table
    public static final String TABLE_RESULTS = "results";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CATEGORY_ID = "cat_id";
    public static final String COLUMN_RESULT = "result";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_CATEGORIES_ID = "_id";

    // Database creation SQL statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_RESULTS
            + "("
            + COLUMN_ID + " integer primary key not null, "
            + COLUMN_DATE + " date not null,"
            + COLUMN_CATEGORY_ID + " text not null,"
            + COLUMN_RESULT + " text not null,"
            + "foreign key (" + COLUMN_CATEGORY_ID + ") references " + TABLE_CATEGORIES + "(" + TABLE_CATEGORIES_ID + ")"
            + ");";

    public static void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    public static void onUpgrade(SQLiteDatabase database, int oldVersion,
                                 int newVersion) {
        Log.w(ResultsTable.class.getName(), "Upgrading database from version "
                + oldVersion + " to " + newVersion
                + ", which will destroy all old data");
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_RESULTS);
        onCreate(database);
    }
}
