package theperfectsquare.counterandchrono;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import theperfectsquare.counterandchrono.contentprovider.CategoriesContentProvider;
import theperfectsquare.counterandchrono.contentprovider.ResultsContentProvider;
import theperfectsquare.counterandchrono.database.CategoriesTable;
import theperfectsquare.counterandchrono.database.ResultsTable;


public class TimerActivity extends Activity {

    private EditText mTitleText;
    private Chronometer mDataText;
    private boolean running = false;
    private long startTime = 0L;
    private long stopTime = 0L;
    private Uri CategoryUri;
    private Uri ResultsUri;
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_timer);

        mTitleText = (EditText) findViewById(R.id.title_editable);
        mDataText = (Chronometer) findViewById(R.id.chronometer);
        Button startButton = (Button) findViewById(R.id.start_reset_button);
        final Button pauseButton = (Button) findViewById((R.id.pause_button));

        mDataText.setBase(SystemClock.elapsedRealtime());

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        CategoryUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE);
        ResultsUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(ResultsContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            CategoryUri = extras
                    .getParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE);
            ResultsUri = extras
                    .getParcelable(ResultsContentProvider.CONTENT_ITEM_TYPE);
            fillData(CategoryUri, ResultsUri);
        }

        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!running) {
                    mDataText.setBase(SystemClock.elapsedRealtime() - stopTime);
                    ((Chronometer) findViewById(R.id.chronometer)).start();
                    startTime = System.currentTimeMillis();
                    running = true;
                }
            }
        });
        pauseButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mDataText.setBase(SystemClock.elapsedRealtime());
                mDataText.stop();
                stopTime = 0;
                running = false;
                return false;
            }
        });

        pauseButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                if(running == true){
                    ((Chronometer) findViewById(R.id.chronometer)).stop();
                    stopTime=SystemClock.elapsedRealtime() - mDataText.getBase();
                    running = false;
                }

            }
        });
        pauseButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // on release
                    pauseButton.setBackgroundColor(Color.WHITE);
                    pauseButton.setTextColor(Color.BLACK);
                    pauseButton.setText("Pause");
                }else{
                    // on touch
                    pauseButton.setBackgroundColor(Color.RED);
                    pauseButton.setTextColor(Color.WHITE);
                    pauseButton.setText("Hold to Reset");
                }
                return false;
            }
        });
    }

    private void fillData(Uri categoryUri, Uri resultsUri) {
        Log.i("Category URI", "Category URI " + categoryUri);
        Log.i("Results URI", "Results URI " + resultsUri);

        String[] categoryProjection = { CategoriesTable.COLUMN_TYPE, CategoriesTable.COLUMN_NAME};
        Cursor categoryCursor = getContentResolver().query(categoryUri, categoryProjection, null, null, null);

        String[] resultsProjection = { ResultsTable.COLUMN_ID, ResultsTable.COLUMN_RESULT, ResultsTable.COLUMN_CATEGORY_ID};
        Cursor resultsCursor = getContentResolver().query(resultsUri, resultsProjection, null, null, null);

        if (categoryCursor != null && resultsCursor != null) {
            categoryCursor.moveToFirst();
            mTitleText.setText(categoryCursor.getString(categoryCursor
                    .getColumnIndexOrThrow(CategoriesTable.COLUMN_NAME)));
            // always close the cursor
            categoryCursor.close();

            resultsCursor.moveToFirst();
            //mDataText.setBase(SystemClock.elapsedRealtime() - stopTime);
            mDataText.setBase(SystemClock.elapsedRealtime() - Long.parseLong(resultsCursor.getString(resultsCursor
                    .getColumnIndexOrThrow(ResultsTable.COLUMN_RESULT))));
            stopTime =Long.parseLong(resultsCursor.getString(resultsCursor
                            .getColumnIndexOrThrow(ResultsTable.COLUMN_RESULT)));
            // always close the cursor
            resultsCursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE, CategoryUri);
        outState.putParcelable(ResultsContentProvider.CONTENT_ITEM_TYPE, ResultsUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String summary = mTitleText.getText().toString();
        //pauses the clock so data can be saved
        ((Chronometer) findViewById(R.id.chronometer)).stop();
        stopTime=SystemClock.elapsedRealtime() - mDataText.getBase();
        String data = Long.toString(stopTime);

        // only save if either summary or description
        // is available

        //category values
        ContentValues categoryValues = new ContentValues();
        categoryValues.put(CategoriesTable.COLUMN_NAME, summary);
        categoryValues.put(CategoriesTable.COLUMN_TYPE, "timer");

        //results values
        ContentValues resultsValues = new ContentValues();
        resultsValues.put(ResultsTable.COLUMN_RESULT,data);



        if (CategoryUri == null) {
            // New cagetory which means zero data or new data
            CategoryUri = getContentResolver().insert(CategoriesContentProvider.CONTENT_URI, categoryValues);
            //gets the new id from the given gategory uri
            resultsValues.put(ResultsTable.COLUMN_CATEGORY_ID, CategoryUri.getLastPathSegment());
            ResultsUri = getContentResolver().insert(ResultsContentProvider.CONTENT_URI, resultsValues);
        } else {
            // Update category
            getContentResolver().update(CategoryUri, categoryValues, null, null);
            getContentResolver().update(ResultsUri, resultsValues, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(TimerActivity.this, "Please maintain a summary",
                Toast.LENGTH_LONG).show();
    }
}
