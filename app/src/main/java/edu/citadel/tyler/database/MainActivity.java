package edu.citadel.tyler.database;


import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Loader;
import android.content.Intent;
import android.content.CursorLoader;
import android.content.ContentUris;
import android.widget.*;
import android.view.*;
import android.util.Log;
import android.net.Uri;
import android.database.Cursor;
import android.provider.ContactsContract;

public class MainActivity extends ListActivity
        implements LoaderManager.LoaderCallbacks<Cursor>
{
    private static final boolean DEBUG = false;
    private static final String  LOG_TAG = "MainActivity";

    private static final String[] VIEW_COLUMNS = { "name",    "phone_num" };
    private static final int[]    VIEWS        = { R.id.name, R.id.phoneNum };


    private SimpleCursorAdapter adapter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button addButton = (Button)findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainActivity.this, AddEmergencyContact.class);
                startActivity(intent);
                updateContactList();
            }
        });

        // Create an empty adapter for displaying the loaded data
        adapter = new SimpleCursorAdapter(this, R.layout.emergency_contact, null, VIEW_COLUMNS, VIEWS, 0);
        setListAdapter(adapter);

        ListView lv = getListView();
        lv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage(R.string.confirmDelete)
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int buttonId)
                            {
                                Uri itemUri = ContentUris.withAppendedId(EmergencyContract.CONTENT_URI, id);
                                getContentResolver().delete(itemUri, null, null);
                            }
                        })
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener()
                        {
                            public void onClick(DialogInterface dialog, int buttonId)
                            {
                                // nothing to do - user cancelled the dialog
                            }
                        });

                AlertDialog alertDialog = builder.create();
                alertDialog.show();

                return true;
            }
        });

        // Initialize the loader manager.
        getLoaderManager().initLoader(0, null, this);
    }


    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        ViewGroup vg = (ViewGroup) v;
        TextView nameView = (TextView) vg.getChildAt(0);
        String name = nameView.getText().toString();
        TextView phoneNumView = (TextView) vg.getChildAt(1);
        String phoneNum = phoneNumView.getText().toString();

        if (DEBUG)
        {
            String message = "position = " + Integer.toString(position)
                    + ", id = " + Long.toString(id)
                    + ", name = " + name
                    + ", phoneNum = " + phoneNum;
            Toast toast = Toast.makeText(MainActivity.this,
                    message, Toast.LENGTH_SHORT);
            toast.show();
        }

        callContact(name, phoneNum);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args)
    {
        Uri uri = EmergencyContract.CONTENT_URI;
        return new CursorLoader(this, uri, EmergencyContract.COLUMNS, null, null, null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data)
    {
        if (DEBUG)
            toastDb(data);

        // swap in the cursor
        adapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader)
    {
        adapter.swapCursor(null);
    }


    /**
     * Updates the emergency contacts on the user screen.
     */
    private void updateContactList()
    {
        try
        {
            SimpleCursorAdapter adapter = (SimpleCursorAdapter) getListAdapter();
            adapter.notifyDataSetChanged();
        }
        catch (Exception ex)
        {
            String errorMsg = "Error updating emergency contacts";
            Log.e(LOG_TAG, errorMsg, ex);
        }
    }


    /**
     * Initiates a telephone call to the specified phone number.
     */
    private void callContact(String name, String phoneNum)
    {
        if (DEBUG)
        {
            String message = "Calling " + name;
            Toast toast = Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT);
            toast.show();
        }

        Uri uri = Uri.parse("tel:" + phoneNum);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        startActivity(intent);
    }


    public void toastDb(Cursor cursor)
    {
        while (cursor.moveToNext())
        {
            int    id       = cursor.getInt(0);
            String name     = cursor.getString(1);
            String phoneNum = cursor.getString(2);

            String message = "ID: " + id + ", Name: " + name
                    + ", Phone Number: " + phoneNum;
            Toast toast = Toast.makeText(MainActivity.this,
                    message, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
            return true;

        return super.onOptionsItemSelected(item);
    }
}