package theperfectsquare.counterandchrono.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tai on 28/12/14.
 */
public class ResultsDataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "results.db";
    private static final int DATABASE_VERSION = 1;

    public ResultsDataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Method is called during creation of the database
    @Override
    public void onCreate(SQLiteDatabase database) {
        ResultsTable.onCreate(database);
    }

    // Method is called during an upgrade of the database,
    // e.g. if you increase the database version
    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion,
                          int newVersion) {
        ResultsTable.onUpgrade(database, oldVersion, newVersion);
    }
}
