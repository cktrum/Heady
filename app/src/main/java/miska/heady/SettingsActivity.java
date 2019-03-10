package miska.heady;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
    private Calendar calendar = Calendar.getInstance();
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);

        final EditText fromDateField = findViewById(R.id.FromDateTextField);
        fromDateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog(fromDateField);
            }
        });
        fillWithData();
    }

    private void fillWithData() {
        int numberOfEntries = sharedPreferences.getInt("numberOfEntries", 0);
        EditText entriesTextField = findViewById(R.id.NumberEntriesTextField);
        entriesTextField.setText(String.valueOf(numberOfEntries));

        String fromDateString = sharedPreferences.getString("fromDate", null);
        EditText fromDateTextField = findViewById(R.id.FromDateTextField);
        if (fromDateString != null && !fromDateString.isEmpty()) {
            fromDateTextField.setText(dateFormatter.format(fromDateString));
        } else {
            fromDateTextField.setText(dateFormatter.format(new Date()));
        }
    }

    private void openDateDialog(final EditText fromDateField) {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar userDate = Calendar.getInstance();
                userDate.set(Calendar.YEAR, year);
                userDate.set(Calendar.MONTH, month);
                userDate.set(Calendar.DAY_OF_MONTH, day);
                fromDateField.setText(dateFormatter.format(userDate.getTime()));
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(this, dateListener,
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.SaveSettingsBtn) {
            saveSettings();
            Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(mainIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void saveSettings() {
        EditText textField = findViewById(R.id.FromDateTextField);
        String dateString = textField.getText().toString();
        Date fromDate = new Date();
        try {
            fromDate = dateFormatter.parse(dateString);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }

        EditText numberField = findViewById(R.id.NumberEntriesTextField);
        int numberOfEntries = Integer.parseInt(numberField.getText().toString());
        Setting settings = new Setting(numberOfEntries, fromDate);
        DBHandler db = new DBHandler(this);
        db.saveSettings(settings);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("fromDate", dateString);
        editor.putInt("numberOfEntries", numberOfEntries);
        editor.commit();
    }
}
