package theperfectsquare.counterandchrono;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import theperfectsquare.counterandchrono.contentprovider.CategoriesContentProvider;
import theperfectsquare.counterandchrono.database.CategoriesTable;
import theperfectsquare.counterandchrono.contentprovider.ResultsContentProvider;

public class MainOverviewActivity extends ListActivity implements
        LoaderManager.LoaderCallbacks<Cursor>  {

    private static final int ACTIVITY_CREATE = 0;
    private static final int ACTIVITY_EDIT = 1;
    private static final int DELETE_ID = Menu.FIRST + 1;
    // private Cursor cursor;
    private SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        setContentView(R.layout.activity_main_overview);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categories_list);
        this.getListView().setDividerHeight(2);
        fillData();
        registerForContextMenu(getListView());
    }
    // create the menu based on the XML defintion
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.categories_listmenu, menu);
        return true;
    }

    // Reaction to the menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.NewCounter:
                createCounter();
                return true;
            case R.id.NewTimer:
                createTimer();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case DELETE_ID:
                AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                Uri categoryUri = Uri.parse(CategoriesContentProvider.CONTENT_URI + "/"
                        + info.id);
                Uri resultUri = Uri.parse(ResultsContentProvider.CONTENT_URI + "/category/"
                        + info.id);
                getContentResolver().delete(categoryUri, null, null);
                getContentResolver().delete(resultUri, null, null);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createTimer() {
        Intent i = new Intent(this, TimerActivity.class);
        startActivity(i);
    }

    private void createCounter() {
        Intent i = new Intent(this, CounterActivity.class);
        startActivity(i);
    }

    // Opens the second activity if an entry is clicked
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        String type = ((TextView) v.findViewById(R.id.type)).getText().toString();
        Intent i;
        Uri resultsUri;
        Uri categoryUri;
        switch(type){
            case "timer":
                i = new Intent(this, TimerActivity.class);
                resultsUri = Uri.parse(ResultsContentProvider.CONTENT_URI + "/category/" + id);
                categoryUri = Uri.parse(CategoriesContentProvider.CONTENT_URI + "/" + id);

                i.putExtra(ResultsContentProvider.CONTENT_ITEM_TYPE, resultsUri);
                i.putExtra(CategoriesContentProvider.CONTENT_ITEM_TYPE, categoryUri);
                startActivity(i);
                return;
            case "counter":
                i = new Intent(this, CounterActivity.class);
                resultsUri = Uri.parse(ResultsContentProvider.CONTENT_URI + "/category/" + id);
                categoryUri = Uri.parse(CategoriesContentProvider.CONTENT_URI + "/" + id);

                i.putExtra(ResultsContentProvider.CONTENT_ITEM_TYPE, resultsUri);
                i.putExtra(CategoriesContentProvider.CONTENT_ITEM_TYPE, categoryUri);
                startActivity(i);
                return;
            default:
                break;
        }
    }

    private void fillData() {
        // Fields from the database (projection)
        // Must include the _id column for the adapter to work
        String[] from = new String[] { CategoriesTable.COLUMN_NAME , CategoriesTable.COLUMN_TYPE};
        // Fields on the UI to which we map
        int[] to = new int[] { R.id.label , R.id.type};

        getLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(this, R.layout.categories_row, null, from,
                to, 0);

        setListAdapter(adapter);
    }

    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }
    // creates a new loader after the initLoader () call
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = { CategoriesTable.COLUMN_ID, CategoriesTable.COLUMN_NAME, CategoriesTable.COLUMN_TYPE};
        CursorLoader cursorLoader = new CursorLoader(this,
                CategoriesContentProvider.CONTENT_URI, projection, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // data is not available anymore, delete reference
        adapter.swapCursor(null);
    }
}
