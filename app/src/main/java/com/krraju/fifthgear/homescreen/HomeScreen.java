package com.krraju.fifthgear.homescreen;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class HomeScreen extends AppCompatActivity {

    // == constants ==
    private static final String TAG = HomeScreen.class.getSimpleName();

    // == fields ==
    private TextView todayAmount;
    private TextView thisMonthAmount;
    private RecyclerView transactionRecyclerView;
    private RecyclerView expiringSoonRecyclerView;
    private LineChart lineChart;

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
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
        lineChart = findViewById(R.id.home_screen_chart);

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

            // == Getting the last Date of the Month
            LocalDate endDate = yearMonth.atEndOfMonth();
            List<Transaction> allTransaction = Database.getInstance(this).transactionDao().getAllTransaction();

            List<Transaction> transactions = new ArrayList<>();

            for (Transaction transaction : allTransaction) {
                if (transaction.getDate().equals(firstDate) || transaction.getDate().equals(endDate)) {
                    transactions.add(transaction);
                }
                if (transaction.getDate().isAfter(firstDate) && transaction.getDate().isBefore(endDate)) {
                    transactions.add(transaction);
                }
            }

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

        // == Displaying the Chart ==
        displayChart();

    }

    private void displayChart() {
        // == Fetching the data ==
        ArrayList<Entry> lineEntries = getEntries();

        // == Setup the Graph ==
        setUpGraph(lineEntries);
    }

    private void setUpGraph(ArrayList<Entry> lineEntries) {

        // == Creating the data set ==
        LineDataSet lineDataSet = new LineDataSet(lineEntries, "");

        // == Setting the width of the Line ==
        lineDataSet.setLineWidth(2f);

        // == Creating the Line data using data Set ==
        LineData lineData = new LineData(lineDataSet);

        // == Setting the data ==
        lineChart.setData(lineData);

        // == Removing the grid ==
        lineChart.getXAxis().setDrawGridLines(false);

        // == Setting the X axis position to bottom ==
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // == Removing the Right Axis ==
        lineChart.getAxisRight().setEnabled(false);

        // == Removing the Left Axis ==
        lineChart.getAxisLeft().setEnabled(false);

        // == Removing the Description ==
        lineChart.getDescription().setEnabled(false);

        // == Removing the Legend ==
        lineChart.getLegend().setEnabled(false);

        // == Adding the value formatter to Line chart ==
        lineChart.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                String v = Float.toString(value);
                switch (v) {
                    case "2.0":
                        return LocalDate.now().minusDays(6).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "4.0":
                        return LocalDate.now().minusDays(5).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "6.0":
                        return LocalDate.now().minusDays(4).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "8.0":
                        return LocalDate.now().minusDays(3).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "10.0":
                        return LocalDate.now().minusDays(2).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "12.0":
                        return LocalDate.now().minusDays(1).getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    case "14.0":
                        return LocalDate.now().getDayOfWeek().getDisplayName(TextStyle.SHORT,new Locale("en"));
                    default:
                        return "";
                }
            }
        });

        // == Setting the Value text size ==
        lineChart.getXAxis().setTextSize(12f);

        // == Changing the Line Color ==
        lineDataSet.setColors(ColorTemplate.rgb("#575757"));

        // == Changing the text Color ==
        lineDataSet.setValueTextColor(Color.parseColor("#D60831"));

        // == Changing the text Size ==
        lineDataSet.setValueTextSize(18f);

        // == Adding Animation ==
        lineChart.animateXY(3000,3000);


    }

    private ArrayList<Entry> getEntries() {

        // == Creating the ArrayList for Line Entries ==
        ArrayList<Entry> lineEntries = new ArrayList<>();

        // == Creating the Executor Service for running the Threads ==
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // == Creating Callable for Calculating the Amount ==
        Callable<ArrayList<Entry>> callable = () -> {

            // == Fetching data from database ==
            List<Transaction> allTransaction = Database.getInstance(HomeScreen.this).transactionDao().getAllTransaction();

            // == declaring the variables ==
            double dayOne = 0;
            double dayTwo = 0;
            double dayThree = 0;
            double dayFour = 0;
            double dayFive = 0;
            double daySix = 0;
            double daySeven = 0;

            // == Calculating the Amount for days ==
            for (Transaction transaction : allTransaction) {
                if (transaction.getDate().equals(LocalDate.now().minusDays(6))) {
                    daySeven += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now().minusDays(5))) {
                    daySix += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now().minusDays(4))) {
                    dayFive += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now().minusDays(3))) {
                    dayFour += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now().minusDays(2))) {
                    dayThree += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now().minusDays(1))) {
                    dayTwo += transaction.getAmount();
                } else if (transaction.getDate().equals(LocalDate.now())) {
                    dayOne += transaction.getAmount();
                }
            }

            // == Adding entry to the array List ==
            lineEntries.add(new Entry(2f, (float) daySeven));
            lineEntries.add(new Entry(4f, (float) daySix));
            lineEntries.add(new Entry(6f, (float) dayFive));
            lineEntries.add(new Entry(8f, (float) dayFour));
            lineEntries.add(new Entry(10f, (float) dayThree));
            lineEntries.add(new Entry(12f, (float) dayTwo));
            lineEntries.add(new Entry(14f, (float) dayOne));

            // == returning the entries ==
            return lineEntries;
        };

        // == Submitting the Callable to the Executor Service ==
        Future<ArrayList<Entry>> future = executor.submit(callable);

        // == Getting the data from service ==
        while (true){
            try {
                return future.get();
            } catch (ExecutionException | InterruptedException e) {
                try{
                    Thread.sleep(1000);
                }catch (InterruptedException ignored){

                }
            }
        }
    }

    @Override
    public Intent registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        return super.registerReceiver(receiver, filter);
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

            // == Getting the first Date of Month
            YearMonth yearMonth = YearMonth.now();
            LocalDate firstDate = yearMonth.atDay(1);

            // == Getting the last Date of the Month
            LocalDate endDate = yearMonth.atEndOfMonth();

            // == Database Query ==
            List<Transaction> allTransaction = Database.getInstance(this).transactionDao().getAllTransaction();

            List<Transaction> transactions = new ArrayList<>();

            for (Transaction transaction : allTransaction) {
                if (transaction.getDate().equals(firstDate) || transaction.getDate().equals(endDate)) {
                    transactions.add(transaction);
                }
                if (transaction.getDate().isAfter(firstDate) && transaction.getDate().isBefore(endDate)) {
                    transactions.add(transaction);
                }
            }

            Log.d(TAG, "onCreate: " + transactions);

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

        // == Displaying the Chart ==
        displayChart();
    }
}
