package com.krraju.fifthgear.statistics;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;

public class StatisticActivity extends AppCompatActivity {

    // == fields ==
    private Spinner spinner;
    private RecyclerView statisticRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        // == local Fields ==
        Toolbar toolbar = findViewById(R.id.tool_bar);

        // == finding view by id ==
        spinner = findViewById(R.id.spinner);
        statisticRecyclerView = findViewById(R.id.statistic_recycler_view);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // == Creating the Spinner Adaptor ==
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.filters, android.R.layout.simple_list_item_1);

        // Setting the Adaptor to spinner ==
        spinner.setAdapter(adapter);

        // == adding on item selected listener for the spinner ==
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // == getting the selected item ==
                String selectedItem = (String) parent.getItemAtPosition(position);

                // == checking the selected item ==
                switch (selectedItem){
                    case "ALL":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("ALL");
                        break;
                    case "TODAY":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("TODAY");
                        break;
                    case "YESTERDAY":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("YESTERDAY");
                        break;
                    case "THIS WEEK":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("THIS WEEK");
                        break;
                    case "THIS MONTH":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("THIS MONTH");
                        break;
                    case "LAST 6 MONTHS":
                        assert statisticRecyclerView.getAdapter() != null;
                        ((StatisticAdaptor) statisticRecyclerView.getAdapter()).getFilter().filter("LAST 6 MONTHS");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // == Creating the Recycler view Adaptor and setting it to Recycler View ==
        statisticRecyclerView.setAdapter(new StatisticAdaptor(this));

        // == Adding the layout manager ==
        statisticRecyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // == setting the functionality for the back button clicked ==
    @Override
    public boolean onSupportNavigateUp() {

        // == Closing the activity ==
        finish();

        // == returning true because we have handled method ==
        return true;
    }
}