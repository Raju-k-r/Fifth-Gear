package com.krraju.fifthgear.viewuser;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;

public class ViewUser extends AppCompatActivity {

    // == contents ==
    private static final String TAG = ViewUser.class.getSimpleName();

    // == fields ==
    private ViewUserAdaptor viewUserAdaptor;
    private RecyclerView usersRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);

        // == local fields ==
        Toolbar toolbar;

        // == finding view by id ==
        toolbar = findViewById(R.id.toolbar);
        usersRecyclerView = findViewById(R.id.users_recycler_view);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // == Creating new Adopter ==
        viewUserAdaptor = new ViewUserAdaptor(this);

        // == Adding the Recycler View Adopter ==
        usersRecyclerView.setAdapter(viewUserAdaptor);

        // == Setting the Layout Manager for Recycler view ==
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    // == Adding the menu to toolbar ==
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // == inflating the menu item and adding to menu item==
        getMenuInflater().inflate(R.menu.search_menu,menu);

        // == Getting the menu item i.e Search item from menu ==
        MenuItem menuItem = menu.findItem(R.id.search);

        // == Getting the Search View From the Search menu item ==
        SearchView searchView = ((SearchView) menuItem.getActionView());

        // == Adding the textChange listener of the search bar ==
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            // == This method will be called when user click ok or submit button ==
            @Override
            public boolean onQueryTextChange(String newText) {

                // == getting the filter from the recycler view and passing the char sequence ==
                viewUserAdaptor.getFilter().filter(newText);

                // == Since we have handled the method we are returning true ==
                return true;
            }

            // == This method will be called when user type or remove text from search view ==
            @Override
            public boolean onQueryTextSubmit(String query) {

                // == getting the filter from the recycler view and passing the char sequence ==
                viewUserAdaptor.getFilter().filter(query);

                // == Since we have handled the method we are returning true ==
                return true;
            }
        });

        // == returning true because we have handled method ==
        return true;
    }


    // == setting the functionality for the back button clicked ==
    @Override
    public boolean onSupportNavigateUp() {

        // == Closing the activity ==
        finish();

        // == returning true because we have handled method ==
        return true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // == resetting the Adaptor ==
        viewUserAdaptor = new ViewUserAdaptor(this);
        usersRecyclerView.setAdapter(viewUserAdaptor);
        Log.d(TAG, "onRestart: Inside On Restart.. ");
    }
}