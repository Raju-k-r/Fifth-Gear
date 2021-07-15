package com.krraju.fifthgear.dueamount;

import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;

public class DueAmountActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_due_amount);

        // == Preventing the Screen from taking screenshots ==
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // == Local Variables ==
        Toolbar toolbar = findViewById(R.id.tool_bar);
        RecyclerView dueAmountRecyclerView = findViewById(R.id.due_amount_recycler_view);

        // == Creating the RecyclerView Adaptor and adding it to recycler view ==
        dueAmountRecyclerView.setAdapter(new DueAmountAdaptor(this));

        // == Adding the layout manager ==
        dueAmountRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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