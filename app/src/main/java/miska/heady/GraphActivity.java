package miska.heady;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

class MonthFormatter extends ValueFormatter {
    private ArrayList<String> labels;

    public MonthFormatter(ArrayList<String> labels) {
        this.labels = labels;
    }

    @Override
    public String getAxisLabel(float value, AxisBase axis) {
        return this.labels.get((int)value);
    }
}

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);

        DBHandler db = new DBHandler(this);
        ArrayList<DaysPerMonthEntry> statisticValues = db.getDaysPerMonthStatistic();
        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>();
        for (int i = 0; i < statisticValues.size(); ++i) {
            entries.add(new BarEntry(i, statisticValues.get(i).getAmount()));
            labels.add(statisticValues.get(i).getMonthLabel());
        }

        BarChart daysPerMonthChart = (BarChart) findViewById(R.id.DaysPerMonthChart);

        BarDataSet dataSet = new BarDataSet(entries, "Number of days");
        BarData barData = new BarData(dataSet);

        daysPerMonthChart.setData(barData);
        MonthFormatter formatter = new MonthFormatter(labels);
        daysPerMonthChart.getXAxis().setValueFormatter(formatter);
    }
}
