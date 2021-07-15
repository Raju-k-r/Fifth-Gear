package com.krraju.fifthgear.expenditure;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;

import java.time.LocalDate;

public class Expenditure extends AppCompatActivity {
    
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenditure);

        // == Preventing the Screen from taking screenshots ==
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // == local Fields ==
        Toolbar toolbar = findViewById(R.id.toolbar);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == finding view by id ==
        Spinner spinner = findViewById(R.id.filter_spinner);
        FloatingActionButton floatingActionButton = findViewById(R.id.add_new_expenditure_fab);
        recyclerView = findViewById(R.id.recyclerView);
        
        // == setting adaptor for recycler view ==
        recyclerView.setAdapter(new ExpenditureAdaptor(Expenditure.this));

        // == Setting onclick listener ==
        floatingActionButton.setOnClickListener(v -> showDialog());

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
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("ALL");
                        break;
                    case "TODAY":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("TODAY");
                        break;
                    case "YESTERDAY":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("YESTERDAY");
                        break;
                    case "THIS WEEK":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("THIS WEEK");
                        break;
                    case "THIS MONTH":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("THIS MONTH");
                        break;
                    case "LAST MONTH":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("LAST MONTH");
                        break;
                    case "LAST 6 MONTHS":
                        assert recyclerView.getAdapter() != null;
                        ((ExpenditureAdaptor) recyclerView.getAdapter()).getFilter().filter("LAST 6 MONTHS");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void showDialog() {

        Dialog dialog = new Dialog(Expenditure.this);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.add_new_expenditure_dialog);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        Button addButton = dialog.findViewById(R.id.add);
        TextView cancelButton = dialog.findViewById(R.id.cancel_text_view);

        addButton.setOnClickListener(view->{

            String shortDescription = ((EditText)dialog.findViewById(R.id.short_description)).getText().toString();
            String detailedDescription = ((EditText)dialog.findViewById(R.id.detailed_description)).getText().toString();
            String amount = ((EditText)dialog.findViewById(R.id.amount_edit_text)).getText().toString();

            if(shortDescription.length() <= 0){
                Toast.makeText(this, "Short Description should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if(detailedDescription.length() <= 0){
                Toast.makeText(this, "Detailed Description should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            if(amount.length() <= 0){
                Toast.makeText(this, "Amount should not be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(()->{
                com.krraju.fifthgear.storage.entity.expenditure.Expenditure expenditure = new com.krraju.fifthgear.storage.entity.expenditure.Expenditure();
                expenditure.setDate(LocalDate.now());
                expenditure.setShortDescription(shortDescription);
                expenditure.setDetailedDescription(detailedDescription);
                expenditure.setAmount(Float.parseFloat(amount));
                Database.getInstance(this).expenditureDao().addNewExpenditure(expenditure);

                runOnUiThread(()->{
                    dialog.dismiss();
                    Toast.makeText(this, "Expenditure added Successfully....", Toast.LENGTH_SHORT).show();
                    startActivity(getIntent());
                    finish();
                });

            }).start();
        });
        cancelButton.setOnClickListener(view-> dialog.dismiss());
        dialog.show();
    }

    // == Adding functionality for the back or up button of tool bar ==
    @Override
    public boolean onSupportNavigateUp() {
        // == closing the activity ==
        finish();

        // == returning true because we have handled method ==
        return true;
    }
}