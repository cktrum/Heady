package miska.heady;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListItemFragment extends Fragment {

    private HeadacheEntry entry;

    public static ListItemFragment newInstance(HeadacheEntry entry) {
        ListItemFragment fragment = new ListItemFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("HeadacheEntry", (Serializable) entry);
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
        View view = inflater.inflate(R.layout.list_item, container, false);
        view.setId(View.generateViewId());

        entry = (HeadacheEntry) getArguments().getSerializable("HeadacheEntry");
        showHeadacheEntryDetails(view);
        configureEditButton(view);
        configureLongPress(view);

        return view;
    }

    private void showHeadacheEntryDetails(View view) {
        TextView dateView = (TextView) view.findViewById(R.id.DateView);
        Date startDate = new Date(entry.getStartTime());
        SimpleDateFormat dateFormatter = new SimpleDateFormat("E, dd/MM/yyyy", Locale.UK);
        dateView.setText(dateFormatter.format(startDate));

        TextView startTimeView = (TextView) view.findViewById(R.id.StartTimeView);
        SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm", Locale.UK);
        startTimeView.setText(timeFormatter.format(startDate));

        TextView intensityView = (TextView) view.findViewById(R.id.IntensityView);
        String label = "+";
        String intensity = new String(new char[entry.getIntensity()]).replace("\0", label);
        intensityView.setText(intensity);
    }

    private void configureEditButton(View view) {
        ImageButton editBtn = (ImageButton) view.findViewById(R.id.EditBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addEntryIntent = new Intent(getActivity(), NewEntryActivity.class);
                addEntryIntent.putExtra("Entry", entry);
                startActivity(addEntryIntent);
            }
        });
    }

    private void configureLongPress(View view) {
        TableRow item = view.findViewById(R.id.EntryItemRow);
        /*
        item.setOnContextClickListener(new View.OnContextClickListener() {
            @Override
            public boolean onContextClick(View view) {
                DBHandler db = new DBHandler(getContext());
                db.deleteEntry(entry.getID());
                return true;
            }
        });
        */
    }
}
