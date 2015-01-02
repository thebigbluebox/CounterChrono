package theperfectsquare.counterandchrono.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tai on 28/12/14.
 */
public class CategoriesDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "categories.db";
    private static final int DATABASE_VERSION = 1;

    public CategoriesDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        CategoriesTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        CategoriesTable.onUpgrade(database, oldVersion, newVersion);
    }
}
