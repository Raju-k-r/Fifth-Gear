package com.krraju.fifthgear.addnewuser;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Occupation;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

public class AddNewUser extends AppCompatActivity {

    // == constants ==
    private static final String TAG = AddNewUser.class.getSimpleName();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 102;
    private static final int WRITE_TO_EXTERNAL_STORAGE_REQUEST_CODE = 103;
    private static final int READ_TO_EXTERNAL_STORAGE_REQUEST_CODE = 104;
    private static final String BASE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/FifthGear";
    private Database database;

    // == filed ==
    private EditText firstName;
    private EditText lastName;
    private EditText age;
    private EditText height;
    private EditText weight;
    private EditText address;
    private EditText mobileNumber;
    private EditText email;
    private EditText fees;
    private EditText healthIssues;
    private ImageView profilePhoto;
    private RadioGroup gender;
    private Bitmap bitmap;
    private Spinner planSpinner;
    private Spinner occupationSpinner;
    private Spinner statusSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_user);

        // == local fields ==
        Button registerButton;
        Toolbar toolbar;

        // == Creating the instance of the database ==
        database = Database.getInstance(this);

        // == finding view by id ==
        firstName = findViewById(R.id.first_name);
        lastName = findViewById(R.id.last_name);
        age = findViewById(R.id.age);
        height = findViewById(R.id.height);
        weight = findViewById(R.id.weight);
        address = findViewById(R.id.address);
        mobileNumber = findViewById(R.id.mobile_number);
        email = findViewById(R.id.email);
        fees = findViewById(R.id.fees);
        healthIssues = findViewById(R.id.health_issue);
        profilePhoto = findViewById(R.id.profile_photo);
        planSpinner = findViewById(R.id.plan);
        occupationSpinner = findViewById(R.id.occupation);
        gender = findViewById(R.id.gender);
        statusSpinner = findViewById(R.id.status);
        registerButton = findViewById(R.id.register);
        toolbar = findViewById(R.id.toolbar);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // == Setting Onclick Lister ==
        registerButton.setOnClickListener(v -> checkAndRegister());
        profilePhoto.setOnClickListener(v -> selectTheImage());

        // == Creating the Adapter for Plans ==
        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> planArrayAdapter = ArrayAdapter.createFromResource(this, R.array.plans, R.layout.adopter_list_view);

        // == Setting the Adapter for Plans spinner ==
        planSpinner.setAdapter(planArrayAdapter);

       if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
           ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_TO_EXTERNAL_STORAGE_REQUEST_CODE);
       }else{
           loadFeesFile();
       }

        // == Creating the Adaptor for Occupation Spinner ==
        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> occupationArrayAdapter = ArrayAdapter.createFromResource(this, R.array.occupations, R.layout.adopter_list_view);

        // == Setting the Adapter for Occupation spinner ==
        occupationSpinner.setAdapter(occupationArrayAdapter);

        // == Creating the Adaptor for Occupation Spinner ==
        @SuppressLint("ResourceType")
        ArrayAdapter<CharSequence> statusArrayAdapter = ArrayAdapter.createFromResource(this, R.array.status, R.layout.adopter_list_view);

        // == Setting the Adapter for Occupation spinner ==
        statusSpinner.setAdapter(statusArrayAdapter);

    }

    // == load the fees from properties file ==
    private void loadFeesFile() {

        // == Opening the Properties file to get the fees ==
        File file = new File("/storage/emulated/0/FifthGear/", "fees.properties");
        try(InputStream inputStream = new FileInputStream(file)){

            // == Creating the Property instance ==
            Properties properties = new Properties();

            // == Loading the from input stream ==
            properties.load(inputStream);

            // == Setting item selected listener for Plans spinner ==
            planSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @SuppressLint("DefaultLocale")
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    // == checking the item selected in spinner and Setting the fees field ==
                    switch (position) {

                        // == If Selected item is MONTHLY ==
                        case 0:
                            // == Setting the MONTHLY fee ==
                            fees.setText(properties.getProperty("Fees.MONTHLY"));
                            break;

                        // == If Selected item is QUARTERLY ==
                        case 1:
                            // == Setting the QUARTERLY fee ==
                            fees.setText(properties.getProperty("Fees.QUARTERLY"));
                            break;

                        // == If Selected item is HALF YEARLY ==
                        case 2:
                            // == Setting the HALF YEARLY fee ==
                            fees.setText(properties.getProperty("Fees.HALF_YEARLY"));
                            break;

                        // == If Selected item is ANNUAL ==
                        case 3:
                            // == Setting the ANNUAL fee ==
                            fees.setText(properties.getProperty("Fees.ANNUAL"));
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }catch (Exception e){
            // == Showing the error message ==
            Toast.makeText(this, "Can't Load the Fees please try editing the fees ..", Toast.LENGTH_SHORT).show();
        }
    }

    // == Capturing the User Photo From Camera ==
    private void selectTheImage() {
        // == Checking the user permission for Camera ==
        if (ContextCompat.checkSelfPermission(AddNewUser.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewUser.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            // == Starting the Activity For Capture the Image ==
            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
        }
    }

    // == Check The Result and Store the User ==
    private void checkAndRegister() {

        // == Checking The Image ==
        if (bitmap == null) {
            Toast.makeText(this, "Please Take User Image..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking 'first name' not less then 3 ==
        if (firstName.getText().toString().trim().length() < 3) {
            Toast.makeText(this, "First name is Short..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking 'last name' for not null  ==
        if (lastName.getText().toString().trim().length() <= 0) {
            Toast.makeText(this, "Last name is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the 'age' grater then 10 and less then 100 ==
        if (age.getText().toString().isEmpty()) {
            Toast.makeText(this, "Age is empty..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            int intAge = Integer.parseInt(age.getText().toString());
            if (intAge < 10 || intAge > 100) {
                Toast.makeText(this, "Invalid Age..", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // == Checking the 'height' for non negative ==
        if (height.getText().toString().isEmpty()) {
            Toast.makeText(this, "Height is empty..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            float floatHeight = Float.parseFloat(height.getText().toString());
            if (floatHeight <= 0.0) {
                Toast.makeText(this, "Invalid Height..", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // == Checking the 'weight' for non negative ==
        if (weight.getText().toString().isEmpty()) {
            Toast.makeText(this, "Weight is empty..", Toast.LENGTH_SHORT).show();
            return;
        } else {
            float floatWeight = Float.parseFloat(weight.getText().toString());
            if (floatWeight <= 0.0)
                Toast.makeText(this, "Invalid Weight..", Toast.LENGTH_SHORT).show();
        }

        // == Checking the 'address' not less then 6 ==
        if (address.getText().toString().trim().length() < 6) {
            Toast.makeText(this, "Address is too short", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the 'mobile number' length equals 10 ==
        if (mobileNumber.getText().toString().trim().length() != 10) {
            Toast.makeText(this, "Invalid Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the 'email' ==
        if (!(email.getText().toString().contains("@") && email.getText().toString().contains(".com"))) {
            Toast.makeText(this, "Invalid Email", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Checking the Fees ==
        int fees = Integer.parseInt(this.fees.getText().toString());
        if (fees <= 0) {
            Toast.makeText(this, "In valid Fees", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread(() -> {
            // == Checking the User Present in the database ==
            int result = Database.getInstance(this).userDao().isUserPresent(firstName.getText().toString().toUpperCase(), lastName.getText().toString().toUpperCase());

            // == If user is present then showing Error the Dialog ==
            if (result != 0) {
                runOnUiThread(() -> new AlertDialog.Builder(this)
                        .setTitle("Error")
                        .setMessage("User Already Present ..")
                        .setPositiveButton("OK", ((dialog, which) -> dialog.dismiss()))
                        .show());
            }else{

                // == Storing the User ==
                storeTheUser();
            }
        }).start();
    }

    // == Store the User to Database ==
    private void storeTheUser() {
        // == Checking the user permission for Storage ==
        if (ContextCompat.checkSelfPermission(AddNewUser.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddNewUser.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_TO_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            // == Save the Image in External Storage ==
            String path = storeTheImage();

            // == Checking the image stored or not ==
            if (path != null) {
                // == since the image stored adding the new user to database ==
                addUserToDatabase(path);
            }
        }
    }

    // == Add the user to database based on the given information ==
    private void addUserToDatabase(String imagePath) {

        // == Collecting the User information ==
        String firstName = this.firstName.getText().toString().toUpperCase();
        String lastName = this.lastName.getText().toString().toUpperCase();
        int age = Integer.parseInt(this.age.getText().toString());
        Occupation occupation = Occupation.fromString((String) occupationSpinner.getSelectedItem());
        float height = Float.parseFloat(this.height.getText().toString());
        float weight = Float.parseFloat(this.weight.getText().toString());
        String address = this.address.getText().toString();
        String mobileNumber = this.mobileNumber.getText().toString();
        String healthIssue = this.healthIssues.getText().toString();
        String email = this.email.getText().toString();
        float fees = Float.parseFloat(this.fees.getText().toString());
        Plan plan = Plan.fromString((String) planSpinner.getSelectedItem());
        int genderId = gender.getCheckedRadioButtonId();
        Status status = Status.valueOf(((String) statusSpinner.getSelectedItem()));
        String gender;
        if (genderId == R.id.male) {
            gender = "MALE";
        } else {
            gender = "FEMALE";
        }

        // == Creating the Thread for Running Database Query's ==
        new Thread(() -> {

            // == Creating the New User Instance ==
            assert plan != null;
            User newUser = new User(firstName, lastName, age, gender, height, weight, address, mobileNumber, email, plan, occupation, fees, healthIssue, imagePath,status);


            // == Adding the new User To database ==
            database.userDao().addNewUser(newUser);

            // == Showing the dialog of successfully added the user ==
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Success")
                    .setMessage("User Added Successfully ..")
                    .setPositiveButton("OK", ((dialog, which) -> {
                        // == When user click OK button ==

                        // == dismissing the dialog ==
                        dialog.dismiss();

                        // == finishing the current activity ==
                        finish();

                        // == restarting  the activity ==
                        startActivity(getIntent());
                    }))
                    .show());

            Log.d(TAG, "addUserToDatabase: All Users ->" + database.userDao().getAllUser());

        }).start();

    }

    // == Store the Image in External Storage ==
    private String storeTheImage() {

        // == Checking the Image for not null ==
        if (bitmap == null) {

            // == Showing the User error Message ==
            Toast.makeText(this, "Please take the Photo ..", Toast.LENGTH_SHORT).show();

            // == returning null because image is null ==
            return null;
        }

        // == Generating the File name ==
        String fileName = System.currentTimeMillis() +  ".jpeg";

        // == Creating the new File ==
        File imageFile = new File(BASE_PATH, fileName);

        // == Opening the File Output Stream for storing the image in the in file ==
        try (FileOutputStream imageOutputStream = new FileOutputStream(imageFile)) {

            // == Saving image using bitmap and file output stream ==
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOutputStream);

            // == returning the image or file path ==
            return imageFile.getAbsolutePath();

        } catch (Exception e) {
            // == Showing the User error Message ==
            Log.d(TAG, "storeTheImage: Exception ->\n" + e.getMessage());
            Toast.makeText(this, "Something is Wring File Storing the File", Toast.LENGTH_SHORT).show();
        }

        // == Returning null in case of error ==
        return null;
    }

    // == Saving the Captured Image ==
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        // == Validating the Request Code ==
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {

            // == Checking the Captured Image data for not null ==
            if (data != null && data.getExtras() != null) {

                // == Getting the Captured image ==
                bitmap = (Bitmap) data.getExtras().get("data");

                // == Updating the User Profile ==
                profilePhoto.setImageBitmap(bitmap);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    // == Checking the User Permission Result ==
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        // == Validating the Request Code ==
        switch (requestCode) {

            // == Validating the Request Code of CAMERA ==
            case CAMERA_PERMISSION_REQUEST_CODE:

                // == Checking the Permission Result ==
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // == Calling the Function for starting the camera for Image ==
                    selectTheImage();
                } else {
                    // == User has not Permission the permission ==
                    Toast.makeText(this, "The Permission Denied Please Give the permission", Toast.LENGTH_SHORT).show();
                }

                break;

            // == Validating the Request Code of  WRITE_TO_EXTERNAL_STORAGE ==
            case WRITE_TO_EXTERNAL_STORAGE_REQUEST_CODE:
                // == Checking the Permission Result ==
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // == Calling the Function for starting the camera for Image ==
                    storeTheUser();
                } else {
                    // == User has not Permission the permission ==
                    Toast.makeText(this, "The Permission Denied Please Give the permission", Toast.LENGTH_SHORT).show();
                }

                break;

            case READ_TO_EXTERNAL_STORAGE_REQUEST_CODE:
                // == Checking the Permission Result ==
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // == Calling the Function for starting the camera for Image ==
                    loadFeesFile();
                } else {
                    // == User has not Permission the permission ==
                    Toast.makeText(this, "The Permission Denied Please Give the permission", Toast.LENGTH_SHORT).show();
                }
                break;
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
}