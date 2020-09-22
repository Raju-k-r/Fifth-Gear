package com.krraju.fifthgear.homescreen;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.krraju.fifthgear.R;
import com.krraju.fifthgear.addnewuser.AddNewUser;
import com.krraju.fifthgear.dueamount.DueAmountActivity;
import com.krraju.fifthgear.editfees.EditFees;
import com.krraju.fifthgear.statistics.StatisticActivity;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.viewuser.ViewUser;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class HomeScreen extends AppCompatActivity {

    // == constants ==
    private static final String TAG = HomeScreen.class.getSimpleName();

    // == fields ==
    private TextView todayAmount;
    private TextView thisMonthAmount;
    private RecyclerView transactionRecyclerView;
    private RecyclerView expiringSoonRecyclerView;

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        // == fields ==
        Toolbar toolbar;
        DrawerLayout drawerLayout;
        NavigationView navigationView;

        // == finding view by id ==
        toolbar = findViewById(R.id.tool_bar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        todayAmount = findViewById(R.id.today_amount);
        thisMonthAmount = findViewById(R.id.this_month_amount);
        transactionRecyclerView = findViewById(R.id.transaction_recycler_view);
        expiringSoonRecyclerView = findViewById(R.id.expiry_recycler_view);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == Creating the toggle button ==
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer);

        // == adding the drawer listener to drawer layout ==
        drawerLayout.addDrawerListener(toggle);

        // == Used to sync state with drawer ==
        toggle.syncState();

        // == adding item select listener to navigation view ==
        navigationView.setNavigationItemSelectedListener(menuItem -> {

            // == Checking the item selected using id ==
            switch (menuItem.getItemId()) {

                // == checking for Add new User Item ==
                case R.id.add_new_user:
                    Log.d(TAG, "onCreate: add User");
                    startActivity(new Intent(HomeScreen.this, AddNewUser.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                // == checking for edit fee item ==
                case R.id.edit_fees:
                    Log.d(TAG, "onCreate: Edit Fee");
                    startActivity(new Intent(HomeScreen.this, EditFees.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.view_users:
                    Log.d(TAG, "onCreate: View User");
                    startActivity(new Intent(HomeScreen.this, ViewUser.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.statistic:
                    Log.d(TAG, "onCreate: Statistic");
                    startActivity(new Intent(HomeScreen.this, StatisticActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;

                case R.id.due_amount:
                    Log.d(TAG, "onCreate: Due Amount");
                    startActivity(new Intent(HomeScreen.this, DueAmountActivity.class));
                    drawerLayout.closeDrawer(GravityCompat.START);
                    break;
            }

            return true;
        });

        // == Updating the Dash Bord amount ==

        // == getting today's data ==
        new Thread(() -> {

            // == Database Query ==
            List<Transaction> transactions = Database.getInstance(this).transactionDao().getTodaysTransaction(LocalDate.now());

            // == Updating the UI ==
            runOnUiThread(() -> {
                float totalAmount = 0.0f;
                for (Transaction transaction : transactions) {
                    totalAmount += transaction.getAmount();
                }
                todayAmount.setText(String.format("%.2f", totalAmount));
            });
        }).start();

        // == getting this month data ==
        new Thread(() -> {

            // == Database Query ==

            // == Getting the first Date of Month
            YearMonth yearMonth = YearMonth.now();
            LocalDate firstDate = yearMonth.atDay(1);
            Log.d(TAG, "onRestart: First Date ->" + firstDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            // == Getting the last Date of the Month
            LocalDate endDate = yearMonth.atEndOfMonth();
            Log.d(TAG, "onRestart: Last Date ->" + endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            List<Transaction> transactions = Database.getInstance(this).transactionDao().getThisMonthTransaction(firstDate, endDate);

            // == Updating the UI ==
            runOnUiThread(() -> {
                float totalAmount = 0.0f;
                for (Transaction transaction : transactions) {
                    totalAmount += transaction.getAmount();
                }
                thisMonthAmount.setText(String.format("%.2f", totalAmount));
            });
        }).start();

        // == Adding Adaptor to Transaction Recycler View ==
        transactionRecyclerView.setAdapter(new TransactionAdaptor(this));

        // == Adding the layout manager ==
        transactionRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // == Adding Adaptor to ExpiringSoon Recycler View ==
        expiringSoonRecyclerView.setAdapter(new ExpiringSoonAdaptor(this));

        // == Adding the layout manager ==
        expiringSoonRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onRestart() {
        super.onRestart();

        // == getting today's data ==
        new Thread(() -> {

            // == Database Query ==
            List<Transaction> transactions = Database.getInstance(this).transactionDao().getTodaysTransaction(LocalDate.now());

            // == Updating the UI ==
            runOnUiThread(() -> {
                float totalAmount = 0.0f;
                for (Transaction transaction : transactions) {
                    totalAmount += transaction.getAmount();
                }
                todayAmount.setText(String.format("%.2f", totalAmount));
            });
        }).start();

        // == getting this month data ==
        new Thread(() -> {

            // == Database Query ==

            // == Getting the first Date of Month
            YearMonth yearMonth = YearMonth.now();
            LocalDate firstDate = yearMonth.atDay(1);
            Log.d(TAG, "onRestart: First Date ->" + firstDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

            // == Getting the last Date of the Month
            LocalDate endDate = yearMonth.atEndOfMonth();
            Log.d(TAG, "onRestart: Last Date ->" + endDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
            List<Transaction> transactions = Database.getInstance(this).transactionDao().getThisMonthTransaction(firstDate, endDate);

            // == Updating the UI ==
            runOnUiThread(() -> {
                float totalAmount = 0.0f;
                for (Transaction transaction : transactions) {
                    totalAmount += transaction.getAmount();
                }
                thisMonthAmount.setText(String.format("%.2f", totalAmount));
            });
        }).start();

        // == Adding Adaptor to Transaction Recycler View ==
        transactionRecyclerView.setAdapter(new TransactionAdaptor(this));
        expiringSoonRecyclerView.setAdapter(new ExpiringSoonAdaptor(this));
    }
}
