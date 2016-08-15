package com.example.bryanjiang.focus;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    AHBottomNavigation bottomNavigation;

    private final int EDITOR_REQUEST_CODE = 1001;
    private CursorAdapter cursorAdapter;

    int[] TodayFilled = {0, 0, 0};
    int TodayRowSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MainActivity", "Started ");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initBar();
        initToday();

        cursorAdapter = new NotesCursorAdapter(this, null, 0);

        ListView list = (ListView) findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                Uri uri = Uri.parse(NotesProvider.CONTENT_URI + "/" + id);
                intent.putExtra(NotesProvider.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EDITOR_REQUEST_CODE);
            }
        });

        getLoaderManager().initLoader(0, null, this);
    }


    private void initBar() { // The bottom bar's height is 60dp remember to add 60dp as a bottom padding to all fragments
        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);

// Create items
        AHBottomNavigationItem item1 = new AHBottomNavigationItem("Today", R.drawable.goal, R.color.colorPrimary);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem("Master List", R.drawable.crown, R.color.colorAccent);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem("Finished", R.drawable.check, R.color.colorBottomNavigationAccent);
        AHBottomNavigationItem item4 = new AHBottomNavigationItem("Settings", R.drawable.settings, R.color.colorBottomNavigationAccent);

// Add items
        bottomNavigation.addItem(item1);
        bottomNavigation.addItem(item2);
        bottomNavigation.addItem(item3);
        bottomNavigation.addItem(item4);

// Set background color
        bottomNavigation.setDefaultBackgroundColor(Color.parseColor("#FEFEFE"));

// Disable the translation inside the CoordinatorLayout
        bottomNavigation.setBehaviorTranslationEnabled(false);

// Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#F63D2B"));
        bottomNavigation.setInactiveColor(Color.parseColor("#747474"));

// Force to tint the drawable (useful for font with icon for example)
        bottomNavigation.setForceTint(true);

// Force the titles to be displayed (against Material Design guidelines!)
        bottomNavigation.setForceTitlesDisplay(true);

// Use colored navigation with circle reveal effect
        bottomNavigation.setColored(true);

// Set current item programmatically
        bottomNavigation.setCurrentItem(1);

// Customize notification (title, background, typeface)
        bottomNavigation.setNotificationBackgroundColor(Color.parseColor("#F63D2B"));

// Add or remove notification for each item
        bottomNavigation.setNotification("4", 1);
        bottomNavigation.setNotification("", 1);

// Set listeners
        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                if (position == 0) {
                    TodayFragment todayFragment = new TodayFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, todayFragment).commit();
                } else if (position == 1) {
                    MasterListFragment masterListFragment = new MasterListFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, masterListFragment).commit();
                } else if (position == 2) {
                    FinishedFragment finishedFragment = new FinishedFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, finishedFragment).commit();
                } else if (position == 3) {
                    SettingsFragment settingsFragment = new SettingsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, settingsFragment).commit();
                }
                return true;
            }
        });
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
            @Override
            public void onPositionChange(int y) {
                // Manage the new y position
            }
        });
    }

    private void initToday() {
        NotesProvider n = new NotesProvider();
        n.getProfilesCount();
    }

    private void insertNote(String noteText, int usedToday) {
        ContentValues values = new ContentValues();
        values.put(DBOpenHelper.NOTE_TEXT, noteText);
        values.put(DBOpenHelper.USED_TODAY, usedToday);
        Uri noteUri = getContentResolver().insert(NotesProvider.CONTENT_URI,
                values);
        Log.d("MainActivity", "Inserted note " + noteUri.getLastPathSegment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_create_sample:
                insertSampleData();
                break;
            case R.id.action_delete_all:
                deleteAllNotes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllNotes() {

        DialogInterface.OnClickListener dialogClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            //Insert Data management code here
                            getContentResolver().delete(
                                    NotesProvider.CONTENT_URI, null, null
                            );
                            restartLoader();

                            Toast.makeText(MainActivity.this,
                                    "All notes are deleted",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure?")
                .setPositiveButton(getString(android.R.string.yes), dialogClickListener)
                .setNegativeButton(getString(android.R.string.no), dialogClickListener)
                .show();
    }

    private void insertSampleData() {
        insertNote("Simple note", 1);
        insertNote("Multi-line\nnote", 2);
        insertNote("Very long note with a lot of text that exceeds the width of the screen", 3);
        restartLoader();
    }

    public void openEditorForNewNote(View view) {
        Intent intent = new Intent(this, EditorActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

    public void addToToday(View view) {
        switch (TodayRowSelected) {

            case 0:
                Toast.makeText(MainActivity.this, "You cannot pick more than 3 tasks per day :)", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                TextView textView1 = (TextView) findViewById(R.id.PlaceHolder1);
                textView1.setText("Just a test");
                break;
            case 2:
                TextView textView2 = (TextView) findViewById(R.id.PlaceHolder2);
                textView2.setText("Just a test");
                break;
            case 3:
                TextView textView3 = (TextView) findViewById(R.id.PlaceHolder3);
                textView3.setText("Just a test");
                break;
        }
    }

    public void setEmpty(View view) {
        TodayFilled[0] = 0;
        TodayEmptyFragment todayEmptyFragment = new TodayEmptyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.today_container1, todayEmptyFragment).commit();

    }

    public void setEmpty2(View view) {
        TodayFilled[1] = 0;
        TodayEmptyFragment todayEmptyFragment = new TodayEmptyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.today_container2, todayEmptyFragment).commit();
    }

    public void setEmpty3(View view) {
        TodayFilled[2] = 0;
        TodayEmptyFragment todayEmptyFragment = new TodayEmptyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.today_container3, todayEmptyFragment).commit();
    }

    public void setToday1(View view) {
        TodayFilled[0] = 1;
        TodayRowSelected = 1;
        goToMaster(view);

//        TodayItemFragment todayItemFragment = new TodayItemFragment();
//        getSupportFragmentManager().beginTransaction().replace(R.id.today_container1, todayItemFragment).commit();

    }

    public void setToday2(View view) {
        TodayFilled[1] = 1;
        TodayRowSelected = 2;
        TodayItem2Fragment todayItem2Fragment = new TodayItem2Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.today_container2, todayItem2Fragment).commit();
    }

    public void setToday3(View view) {
        TodayFilled[2] = 1;
        TodayRowSelected = 3;
        TodayItem3Fragment todayItem3Fragment = new TodayItem3Fragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.today_container3, todayItem3Fragment).commit();
    }

    public void goToMaster(View view) {
        MasterListFragment masterListFragment = new MasterListFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_activity_id, masterListFragment).commit();
    }

    public void resetTodayPointer() {
        if (TodayFilled[0] == 0) {
            TodayRowSelected = 1;
        } else if (TodayFilled[1] == 0) {
            TodayRowSelected = 2;
        } else if (TodayFilled[3] == 0) {
            TodayRowSelected = 3;
        } else {
            TodayRowSelected = 0;
        }
    }

    //Loader methods
    private void restartLoader() {
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, NotesProvider.CONTENT_URI,
                null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }
}
