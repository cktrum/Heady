package miska.heady;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddCounterMeasureFragment extends Fragment {
    private EditText timeTextField;
    private Calendar calendar;

    public static AddCounterMeasureFragment newInstance(CounterMeasure measure) {
        AddCounterMeasureFragment fragment = new AddCounterMeasureFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("CounterMeasure", (Serializable) measure);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {

        calendar = Calendar.getInstance();

        View view = inflater.inflate(R.layout.countermeasure_entry, container, false);
        timeTextField = view.findViewById(R.id.CounterMeasureTime);

        timeTextField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPickerDialog();
            }
        });

        setupNameSpinner(view);
        fillWithData(view);

        return view;
    }

    private void setupNameSpinner(View view) {
        Spinner spinner = view.findViewById(R.id.CounterMeasureSpinner);
        spinner.setAdapter(new ArrayAdapter<CounterMeasureName>(view.getContext(),
                R.layout.spinner_row, CounterMeasureName.values()));
        spinner.setPrompt(CounterMeasureName.values()[0].toString());
    }

    private void fillWithData(View view) {
        CounterMeasure measure = (CounterMeasure) getArguments().getSerializable("CounterMeasure");
        if (measure != null) {
            Date time = new Date(measure.getTimeOfIntake());
            SimpleDateFormat timeFormatter = new SimpleDateFormat("HH : mm", Locale.UK);
            timeTextField.setText(timeFormatter.format(time));

            Spinner nameSpinner = view.findViewById(R.id.CounterMeasureSpinner);
            nameSpinner.setSelection(measure.getName().toInt());
        } else {
            timeTextField.setText(calendar.get(Calendar.HOUR_OF_DAY) + " : 00");
        }
    }

    private void openPickerDialog() {
        TimePickerDialog.OnTimeSetListener listener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int minutes) {
                if (minutes > 30) {
                    hour += 1;
                }
                timeTextField.setText(hour + " : 00");
            }
        };

        TimePickerDialog pickerDialog = new TimePickerDialog(getContext(), listener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        pickerDialog.show();
    }

}
