package theperfectsquare.counterandchrono;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import theperfectsquare.counterandchrono.contentprovider.CategoriesContentProvider;
import theperfectsquare.counterandchrono.database.CategoriesTable;


public class RecordingActivity extends Activity {
    private EditText mTitleText;

    private Uri todoUri;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.categories_edit);

        mTitleText = (EditText) findViewById(R.id.categories_edit_title);
        Button confirmButton = (Button) findViewById(R.id.categories_edit_button);

        Bundle extras = getIntent().getExtras();

        // check from the saved Instance
        todoUri = (bundle == null) ? null : (Uri) bundle
                .getParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE);

        // Or passed from the other activity
        if (extras != null) {
            todoUri = extras
                    .getParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE);

            fillData(todoUri);
        }

        confirmButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (TextUtils.isEmpty(mTitleText.getText().toString())) {
                    makeToast();
                } else {
                    setResult(RESULT_OK);
                    finish();
                }
            }

        });
    }

    private void fillData(Uri uri) {
        String[] projection = { CategoriesTable.COLUMN_TYPE,
                CategoriesTable.COLUMN_NAME, CategoriesTable.COLUMN_DATE_CREATION };
        Cursor cursor = getContentResolver().query(uri, projection, null, null,
                null);
        if (cursor != null) {
            cursor.moveToFirst();
            mTitleText.setText(cursor.getString(cursor
                    .getColumnIndexOrThrow(CategoriesTable.COLUMN_NAME)));
            // always close the cursor
            cursor.close();
        }
    }

    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putParcelable(CategoriesContentProvider.CONTENT_ITEM_TYPE, todoUri);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    private void saveState() {
        String summary = mTitleText.getText().toString();

        // only save if either summary or description
        // is available

        ContentValues values = new ContentValues();
        values.put(CategoriesTable.COLUMN_NAME, summary);
        values.put(CategoriesTable.COLUMN_TYPE, "timer");
        if (todoUri == null) {
            // New todo
            todoUri = getContentResolver().insert(CategoriesContentProvider.CONTENT_URI, values);
        } else {
            // Update todo
            getContentResolver().update(todoUri, values, null, null);
        }
    }

    private void makeToast() {
        Toast.makeText(RecordingActivity.this, "Please maintain a summary",
                Toast.LENGTH_LONG).show();
    }
}
