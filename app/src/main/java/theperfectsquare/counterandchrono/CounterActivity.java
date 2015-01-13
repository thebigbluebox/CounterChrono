package theperfectsquare.counterandchrono;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import theperfectsquare.counterandchrono.contentprovider.CategoriesContentProvider;
import theperfectsquare.counterandchrono.contentprovider.ResultsContentProvider;
import theperfectsquare.counterandchrono.database.CategoriesTable;
import theperfectsquare.counterandchrono.database.ResultsTable;


public class CounterActivity extends Activity {

    private EditText mTitleText;
    private TextView mCounterInt;
    private TextView mUpdatedText;
    private Uri CategoryUri;
    private Uri ResultsUri;
    private int count= 0;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_counter);

        mTitleText = (EditText) findViewById(R.id.title_editable);
        mCounterInt = (TextView) findViewById(R.id.counter);
        mUpdatedText = (TextView) findViewById((R.id.lastupdate));
        Button increase = (Button) findViewById(R.id.increase_button);
        final Button decrease = (Button) findViewById(R.id.decrease_button);

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
            count = Integer.parseInt(mCounterInt.getText().toString());
        }
        increase.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                count ++;
                mCounterInt.setText(Integer.toString(count));
            }

        });
        //decreasing the counter
        decrease.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                count --;
                mCounterInt.setText(Integer.toString(count));
            }

        });
        //long press of the decrease will reset the count to zero
        decrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                count = 0;
                mCounterInt.setText(Integer.toString(count));
                return false;
            }
        });
        //changing the decrease button text and color to show the extra things when long pressed
        decrease.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    // on release
                    decrease.setBackgroundColor(Color.WHITE);
                    decrease.setTextColor(Color.BLACK);
                    decrease.setText("-");
                }else{
                    // on touch
                    decrease.setBackgroundColor(Color.RED);
                    decrease.setTextColor(Color.WHITE);
                    decrease.setText("Hold to Reset");
                }
                return false;
            }
        });
    }
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    count ++;
                    mCounterInt.setText(Integer.toString(count));
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    //TODO
                    count --;
                    mCounterInt.setText(Integer.toString(count));
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
    private void fillData(Uri categoryUri, Uri resultsUri) {
        Log.i("Category URI", "Category URI " + categoryUri);
        Log.i("Results URI", "Results URI " + resultsUri);
        //all of the queried columns must be first defined here, you can only query the columns listed here
        String[] categoryProjection = { CategoriesTable.COLUMN_TYPE, CategoriesTable.COLUMN_NAME};
        Cursor categoryCursor = getContentResolver().query(categoryUri, categoryProjection, null, null, null);

        String[] resultsProjection = { ResultsTable.COLUMN_ID, ResultsTable.COLUMN_RESULT, ResultsTable.COLUMN_CATEGORY_ID, ResultsTable.COLUMN_DATE};
        Cursor resultsCursor = getContentResolver().query(resultsUri, resultsProjection, null, null, null);
        //the cursor will currently read the most recent data
        if (categoryCursor != null && resultsCursor != null) {
            categoryCursor.moveToFirst();
            mTitleText.setText(categoryCursor.getString(categoryCursor
                    .getColumnIndexOrThrow(CategoriesTable.COLUMN_NAME)));

            categoryCursor.close();

            resultsCursor.moveToFirst();
            mCounterInt.setText(resultsCursor.getString(resultsCursor
                    .getColumnIndexOrThrow(ResultsTable.COLUMN_RESULT)));

            long datesec = resultsCursor.getLong(resultsCursor.getColumnIndexOrThrow(ResultsTable.COLUMN_DATE));
            Date date = new Date(datesec*1000);
            SimpleDateFormat sdf = new SimpleDateFormat("EEEE,MMMM d,yyyy", Locale.ENGLISH);
            String formattedDate = sdf.format(date);
            mUpdatedText.setText(formattedDate);
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
        String data = mCounterInt.getText().toString();
        Calendar cal = Calendar.getInstance();
        //for saving the date value for the data
        long dateInSeconds = (long)((cal.getTimeInMillis()+cal.getTimeZone().getOffset(cal.getTimeInMillis()))/1000);
        Log.i("DATEDATE", "Date " + dateInSeconds);
        // only save if either summary or description
        // is available

        //category values
        ContentValues categoryValues = new ContentValues();
        categoryValues.put(CategoriesTable.COLUMN_NAME, summary);
        categoryValues.put(CategoriesTable.COLUMN_TYPE, "counter");

        //results values
        ContentValues resultsValues = new ContentValues();
        resultsValues.put(ResultsTable.COLUMN_RESULT,data);
        resultsValues.put(ResultsTable.COLUMN_DATE,dateInSeconds);

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
}