package miska.heady;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = "MainActivity";
    ArrayList<String> fragmentTags = new ArrayList<>();
    private static int numberOfEntriesShown = 10;
    private static final int noLimit = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_main);

        removeFragements();
        setNumberOfEntriesToShow();
        displayHeadacheEntries();
        setupAddEntryButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        removeFragements();
        displayHeadacheEntries();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.ExportItem:
                exportAllEntriesToCSV();
                return true;
            case R.id.SettingsItem:
                Intent settingsIntent = new Intent(getBaseContext(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void removeFragements() {
        for (int i = 0; i < fragmentTags.size(); i++) {
            String tag = fragmentTags.get(i);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Fragment fragment = fragmentManager.findFragmentByTag(tag);
            fragmentTransaction.remove(fragment);
            fragmentTransaction.commit();
        }
    }

    private void setNumberOfEntriesToShow() {
        SharedPreferences settings = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int numberOfEntries = settings.getInt("numberOfEntries", 0);
        if (numberOfEntries == 0) {
            DBHandler db = new DBHandler(this);
            numberOfEntriesShown = db.getNumberOfEntries();
        }
        numberOfEntriesShown = numberOfEntries;
    }

    private void displayHeadacheEntries() {
        DBHandler db = new DBHandler(this);
        ArrayList<HeadacheEntry> entries = db.getLatestHeadacheEntries(numberOfEntriesShown);

        for (int i = 0; i < entries.size(); i++) {
            HeadacheEntry entry = entries.get(i);

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            ListItemFragment fragment = ListItemFragment.newInstance(entry);
            String tag = String.valueOf(i);
            fragmentTags.add(tag);
            fragmentTransaction.add(R.id.ListView, fragment, tag);
            fragmentTransaction.commit();
        }
    }

    private void setupAddEntryButton() {
        ImageButton btn = findViewById(R.id.AddEntryBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEntryIntent = new Intent(getBaseContext(), NewEntryActivity.class);
                startActivity(addEntryIntent);
            }
        });
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    private File getPublicStorageDir(String folderName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), folderName);
        if (!file.mkdirs()) {
            file.mkdirs();
        }
        return file;
    }

    private void exportAllEntriesToCSV() {
        if (isExternalStorageWritable()) {
            String folderName = "/Heady";
            File folderFile = getPublicStorageDir(folderName);
            File exportFile = new File(folderFile, getFileName());
            try {
                exportFile.createNewFile();
                FileOutputStream fOut = new FileOutputStream(exportFile);
                OutputStreamWriter writer = new OutputStreamWriter(fOut);

                DBHandler db = new DBHandler(this);
                ArrayList<HeadacheEntry> allEntries = db.getLatestHeadacheEntries(noLimit);
                for (int i = 0; i < allEntries.size(); i++) {
                    String entryAsString = allEntries.get(i).toString();
                    writer.append(entryAsString);
                    writer.append("\n\r");
                }

                writer.close();
                fOut.flush();
                fOut.close();
            } catch (IOException e) {
                Log.w(LOG_TAG, "Could not export to file. " + e.getMessage());
            }
        }
    }

    private String getFileName() {
        Calendar today = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd", Locale.UK);
        return formatter.format(today.getTime()) + ".csv";
    }
}
