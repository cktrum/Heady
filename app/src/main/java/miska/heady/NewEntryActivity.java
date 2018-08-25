package miska.heady;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TimePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewEntryActivity extends AppCompatActivity {

    private static final String TAG = "NewEntryActivity";

    private HeadacheEntry entry;

    private EditText dateTextField;
    private EditText startTimeTextField;
    private EditText endTimeTextField;

    private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy", Locale.UK);
    private Calendar calendar = Calendar.getInstance();

    private DBHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.new_entry);

        db = new DBHandler(this);

        assingLayoutElements();
        setupClickEvents();
        setDefaultValues();
        setupSpinnerValues();

        setupCounterMeasureAdding();
        setupSaveButton();

        fillWithDataIfUpdate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.entry_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId())
        {
            case R.id.DeleteEntry:
                if (entry.hasID()) {
                    db.deleteEntry(entry.getID());
                    Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(mainIntent);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void assingLayoutElements() {
        dateTextField = findViewById(R.id.DateTextField);
        startTimeTextField = findViewById(R.id.StartTimeTextField);
        endTimeTextField = findViewById(R.id.EndTimeTextField);
    }

    private void setupClickEvents() {
        dateTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDateDialog();
            }
        });

        startTimeTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(startTimeTextField);
            }
        });

        endTimeTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openTimeDialog(endTimeTextField);
            }
        });
    }

    private void openDateDialog() {
        DatePickerDialog.OnDateSetListener dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar userDate = Calendar.getInstance();
                userDate.set(Calendar.YEAR, year);
                userDate.set(Calendar.MONTH, month);
                userDate.set(Calendar.DAY_OF_MONTH, day);
                dateTextField.setText(dateFormatter.format(userDate.getTime()));
            }
        };
        DatePickerDialog datePicker = new DatePickerDialog(this, dateListener,
                calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePicker.show();
    }

    private void openTimeDialog(final EditText textField) {
        TimePickerDialog.OnTimeSetListener timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                if (minutes > 30) {
                    hour += 1;
                }
                textField.setText(hour + " : 00");
            }
        };
        TimePickerDialog picker = new TimePickerDialog(this, timeListener, calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE), true);
        picker.show();
    }

    private void setDefaultValues() {
        dateTextField.setText(dateFormatter.format(calendar.getTime()));
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (calendar.get(Calendar.MINUTE) > 30) {
            hour += 1;
        }
        startTimeTextField.setText(hour + " : 00");
        endTimeTextField.setText("-- : --");
    }

    private void setupSpinnerValues() {
        Spinner sideSpinner = findViewById(R.id.SideSpinner);
        sideSpinner.setAdapter(new ArrayAdapter<Side>(this,
                R.layout.spinner_row, Side.values()));
        sideSpinner.setPrompt(Side.values()[0].toString());

        Spinner typeSpinner = findViewById(R.id.TypeSpinner);
        typeSpinner.setAdapter(new ArrayAdapter<PainType>(this,
                R.layout.spinner_row, PainType.values()));
        typeSpinner.setPrompt(PainType.values()[0].toString());

        Spinner sideEffectSpinner = findViewById(R.id.SideEffectSpinner);
        sideEffectSpinner.setAdapter(new ArrayAdapter<SideEffect>(this,
                R.layout.spinner_row, SideEffect.values()));
        sideEffectSpinner.setPrompt(SideEffect.values()[0].toString());
    }

    private void setupCounterMeasureAdding() {
        Button btn = (Button) findViewById(R.id.CounterMeasureAddBtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                AddCounterMeasureFragment fragment = AddCounterMeasureFragment.newInstance(null);
                fragmentTransaction.add(R.id.CounterMeasureList, fragment);
                fragmentTransaction.commit();
            }
        });
    }

    private void setupSaveButton() {
        Button saveBtn = findViewById(R.id.SaveButton);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (entry == null) {
                    entry = createNewEntry();
                } else {
                    updateEntry();
                }
                if (entry != null) {
                    db.addOrUpdateHeadacheEntry(entry);
                }

                Intent mainIntent = new Intent(getBaseContext(), MainActivity.class);
                startActivity(mainIntent);
            }
        });
    }

    private HeadacheEntry createNewEntry() {
        Date startDate = extractStartDate();
        Date endDate = extractEndDate();
        int intensity = extractIntensity();
        Side side = extractSide();
        PainType type = extractType();
        String notes = extractNotes();
        SideEffect sideEffect = extractSideEffect();
        ArrayList<CounterMeasure> measures = extractCounterMeasures();

        HeadacheEntry entry = new HeadacheEntry(startDate, intensity, side, type);
        entry.setNotes(notes);
        if (endDate != null) {
            entry.setEndTime(endDate);
        }
        if (sideEffect != SideEffect.NONE) {
            entry.setSideEffect(sideEffect);
        }
        if (!measures.isEmpty()) {
            entry.setCounterMeasures(measures);
        }

        return entry;
    }

    private Date extractStartDate() {
        Date startDate = null;
        EditText textField = findViewById(R.id.DateTextField);
        EditText startTimeField = findViewById(R.id.StartTimeTextField);
        String dateString = textField.getText().toString();
        String startTimeString = startTimeField.getText().toString();
        String startDateString = dateString + "-" + startTimeString;
        try {
            SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd/MM/yyyy-HH : mm", Locale.UK);
            startDate = fullDateFormatter.parse(startDateString);
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return startDate;
    }

    private Date extractEndDate() {
        Date endDate = null;
        EditText textField = findViewById(R.id.DateTextField);
        EditText endTimeField = findViewById(R.id.EndTimeTextField);
        String dateString = textField.getText().toString();
        String endTimeString = endTimeField.getText().toString();
        String endDateString = dateString + "-" + endTimeString;
        try {
            if (!endTimeString.contains("--")) {
                SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd/MM/yyyy-HH : mm", Locale.UK);
                endDate = fullDateFormatter.parse(endDateString);
            }
        } catch (ParseException e) {
            System.err.println(e.getMessage());
        }
        return endDate;
    }

    private int extractIntensity() {
        SeekBar intensityField = findViewById(R.id.IntensityBar);
        return intensityField.getProgress() + 1;
    }

    private Side extractSide() {
        Spinner sideField = findViewById(R.id.SideSpinner);
        String sideString = sideField.getSelectedItem().toString();
        return Side.fromString(sideString);
    }

    private PainType extractType() {
        Spinner typeField = findViewById(R.id.TypeSpinner);
        String typeString = typeField.getSelectedItem().toString();
        return PainType.fromString(typeString);
    }

    private String extractNotes() {
        EditText notesField = findViewById(R.id.TriggersField);
        return notesField.getText().toString();
    }

    private SideEffect extractSideEffect() {
        Spinner sideEffectField = findViewById(R.id.SideEffectSpinner);
        String sideEffectString = sideEffectField.getSelectedItem().toString();
        return SideEffect.fromString(sideEffectString);
    }

    private ArrayList<CounterMeasure> extractCounterMeasures() {
        ArrayList<CounterMeasure> measures = new ArrayList<>();
        SimpleDateFormat fullDateFormatter = new SimpleDateFormat("dd/MM/yyyy-HH : mm", Locale.UK);


        EditText textField = findViewById(R.id.DateTextField);
        String dateString = textField.getText().toString();

        TableLayout counterMeasureLayout = (TableLayout) findViewById(R.id.CounterMeasureList);
        for (int i = 0; i < counterMeasureLayout.getChildCount(); i++) {
            View v = counterMeasureLayout.getChildAt(i);

            CounterMeasureName measureName = null;
            Date timeOfMeasure = new Date();

            if (v instanceof ViewGroup) {
                ViewGroup row = (ViewGroup) v;
                for (int j = 0; j < row.getChildCount(); j++) {
                    View element = row.getChildAt(j);
                    if (element instanceof Spinner) {
                        String nameString = ((Spinner) element).getSelectedItem().toString();
                        measureName = CounterMeasureName.fromString(nameString);
                    } else if (element instanceof EditText) {
                        String timeString = ((EditText) element).getText().toString();
                        try {
                            timeOfMeasure = fullDateFormatter.parse(dateString + "-" + timeString);
                        } catch (ParseException e) {
                            Log.w(TAG, "Could not parse date. " + e.getMessage());
                        }
                    }
                }

                if (measureName != null) {
                    CounterMeasure measure = new CounterMeasure(measureName, timeOfMeasure);
                    measures.add(measure);
                }
            }
        }
        return measures;
    }

    private void updateEntry() {
        entry.setStartTime(extractStartDate());
        Date endDate = extractEndDate();
        if (endDate != null) {
            entry.setEndTime(endDate);
        }
        entry.setIntensity(extractIntensity());
        entry.setSide(extractSide());
        entry.setPainType(extractType());
        entry.setNotes(extractNotes());
        entry.setSideEffect(extractSideEffect());
        entry.setCounterMeasures(extractCounterMeasures());
    }

    private void fillWithDataIfUpdate() {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH : mm", Locale.UK);
        entry = (HeadacheEntry) getIntent().getSerializableExtra("Entry");
        if (entry != null) {
            dateTextField.setText(dateFormatter.format(entry.getStartTime()));
            startTimeTextField.setText(timeFormatter.format(entry.getStartTime()));
            if (entry.hasEndTime()) {
                endTimeTextField.setText(timeFormatter.format(entry.getEndTime()));
            }
            SeekBar intensityBar = findViewById(R.id.IntensityBar);
            intensityBar.setProgress(entry.getIntensity() - 1);
            Spinner sideSpinner = findViewById(R.id.SideSpinner);
            sideSpinner.setSelection(entry.getSide().toInt());
            Spinner typeSpinner = findViewById(R.id.TypeSpinner);
            typeSpinner.setSelection(entry.getPainType().toInt());
            EditText notesField = findViewById(R.id.TriggersField);
            notesField.setText(entry.getNotes());
            Spinner sideEffectSpinner = findViewById(R.id.SideEffectSpinner);
            if (entry.hasSideEffect()) {
                sideEffectSpinner.setSelection(entry.getSideEffect().toInt());
            }
            ArrayList<CounterMeasure> measures = entry.getCounterMeasures();
            if (!measures.isEmpty()) {
                for (int i = 0; i < measures.size(); i++) {
                    CounterMeasure measure = measures.get(i);
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                    AddCounterMeasureFragment fragment = AddCounterMeasureFragment.newInstance(measure);
                    fragmentTransaction.add(R.id.CounterMeasureList, fragment);
                    fragmentTransaction.commit();
                }
            }
        }
    }

}