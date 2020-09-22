package com.krraju.fifthgear.editfees;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.krraju.fifthgear.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EditFees extends AppCompatActivity {

    // == Constants ==
    private static final String FEES_MONTHLY = "Fees.MONTHLY";
    private static final String FEES_QUARTERLY = "Fees.QUARTERLY";
    private static final String FEES_HALF_YEARLY = "Fees.HALF_YEARLY";
    private static final String FEES_ANNUAL = "Fees.ANNUAL";
    private static final String TAG = EditFees.class.getSimpleName();
    private static final int READ_WRITE_PERMISSION_REQUEST_CODE = 1001;

    // == fields ==
    private EditText monthlyFees;
    private EditText quarterlyFees;
    private EditText halfYearlyFees;
    private EditText annualFees;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_fees);

        // == local Fields ==
        Toolbar toolbar = findViewById(R.id.tool_bar);
        saveButton = findViewById(R.id.save_button);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // == finding the view by id ==
        monthlyFees = findViewById(R.id.monthly_fees);
        quarterlyFees = findViewById(R.id.quarterly_fees);
        halfYearlyFees = findViewById(R.id.half_yearly_fees);
        annualFees = findViewById(R.id.annual_fees);


        // == Checking for the permission ==
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, READ_WRITE_PERMISSION_REQUEST_CODE);
        }else{
            // == if we have permission then loading the file ==
            loadFile();
        }
    }

    private void loadFile() {
        // == Opening the Properties file to get the fees ==
        File file = new File("/storage/emulated/0/FifthGear/", "fees.properties");

        // == Checking the file for the existence ==
        if (!file.exists()) {
            try {
                // == Creating a new File ==
                boolean result = file.createNewFile();

                // == Checking the Result ==
                if(result)
                    Log.d(TAG, "onCreate: New Properties File Created ..");

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "File Creation Failed ..", Toast.LENGTH_SHORT).show();
            }
        }

        try (InputStream inputStream = new FileInputStream(file)) {

            // == Creating the Property instance ==
            Properties properties = new Properties();

            // == Loading the from input stream ==
            properties.load(inputStream);

            // == finding the view and updating the View or adding the data ==
            monthlyFees.setText(properties.getProperty(FEES_MONTHLY,"0"));
            quarterlyFees.setText(properties.getProperty(FEES_QUARTERLY,"0"));
            halfYearlyFees.setText(properties.getProperty(FEES_HALF_YEARLY,"0"));
            annualFees.setText(properties.getProperty(FEES_ANNUAL,"0"));

        } catch (Exception e) {
            // == Showing the error message ==
            Toast.makeText(this, "Can't Load the Fees please try after sometime..", Toast.LENGTH_SHORT).show();
            return;
        }

        saveButton.setOnClickListener(v -> saveFees());
    }

    private void saveFees() {

        // == Checking the monthly fee for empty ==
        if (monthlyFees.getText().toString().isEmpty()) {
            Toast.makeText(this, "Monthly Fee is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee for negative ==
        float monthlyFees = Float.parseFloat(this.monthlyFees.getText().toString());
        if(monthlyFees <= 0){
            Toast.makeText(this, "Monthly Fee is Negative ..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee ==
        if (quarterlyFees.getText().toString().isEmpty()) {
            Toast.makeText(this, "Quarterly Fee is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee for negative ==
        float quarterlyFees = Float.parseFloat(this.quarterlyFees.getText().toString());
        if(quarterlyFees <= 0){
            Toast.makeText(this, "Quarterly Fee is Negative ..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee ==
        if (halfYearlyFees.getText().toString().isEmpty()) {
            Toast.makeText(this, "Half Yearly Fee is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee for negative ==
        float halfYearlyFees = Float.parseFloat(this.halfYearlyFees.getText().toString());
        if(halfYearlyFees <= 0){
            Toast.makeText(this, "Half Yearly Fee is Negative ..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee ==
        if (annualFees.getText().toString().isEmpty()) {
            Toast.makeText(this, "Annual Fee is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the monthly fee for negative ==
        float annualFees = Float.parseFloat(this.annualFees.getText().toString());
        if(annualFees <= 0){
            Toast.makeText(this, "Annual Fee is Negative ..", Toast.LENGTH_SHORT).show();
            return;
        }

        saveFile();
    }

    // == Save the new Updated fees to properties file ==
    private void saveFile() {

        // == Opening the Properties file to save the fees ==
        File file = new File("/storage/emulated/0/FifthGear/", "fees.properties");
        try (InputStream inputStream = new FileInputStream(file)) {

            // == Creating the Property Instance ==
            Properties properties = new Properties();

            // == Loading the from input stream ==
            properties.load(inputStream);

            // == Updating the Properties ==
            properties.setProperty(FEES_MONTHLY, monthlyFees.getText().toString());
            properties.setProperty(FEES_QUARTERLY, quarterlyFees.getText().toString());
            properties.setProperty(FEES_HALF_YEARLY, halfYearlyFees.getText().toString());
            properties.setProperty(FEES_ANNUAL, annualFees.getText().toString());

            properties.store(new FileOutputStream(file), "Fees");

            Log.d(TAG, "saveFile: " + getApplicationContext().getFilesDir().getAbsolutePath());
            Toast.makeText(this, "Updated the Fees", Toast.LENGTH_SHORT).show();

            Log.d(TAG, "saveFile: " + properties.getProperty(FEES_MONTHLY));

        } catch (Exception e) {
            // == Showing the error message ==
            Toast.makeText(this, "Can't Load the Fees please try after sometime..", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    // == Adding functionality for the back or up button of tool bar ==
    @Override
    public boolean onSupportNavigateUp() {
        // == closing the activity ==
        finish();

        // == returning true because we have handled method ==
        return true;
    }

    // == Checking the User Permission Result ==
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // == Validating the Request Code of  READ_WRITE_PERMISSION_REQUEST_CODE ==
        if (requestCode == READ_WRITE_PERMISSION_REQUEST_CODE) {// == Checking the Permission Result ==
            if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // == Calling the Function for loading the file ==
                loadFile();
            } else {
                // == User has not Permission the permission ==
                Toast.makeText(this, "The Permission Denied Please Give the permission", Toast.LENGTH_SHORT).show();
            }
        }
    }
}