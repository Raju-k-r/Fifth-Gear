package com.krraju.fifthgear.edituser;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;

public class EditUserDetailsActivity extends AppCompatActivity {

    // == fields ==
    private int userId;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private TextView userIdTextView;
    private ImageView userImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);

        // == local Fields ==
        Toolbar toolbar = findViewById(R.id.tool_bar);
        Button saveButton = findViewById(R.id.save_button);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // == finding view by id ==
        firstName = findViewById(R.id.first_name_text);
        lastName = findViewById(R.id.last_name_text);
        phoneNumber = findViewById(R.id.phone_number);
        userIdTextView = findViewById(R.id.user_id);
        userImage = findViewById(R.id.user_photo);

        // == Getting the Intent ==
        Intent intent = getIntent();

        // == Checking the intent for not null ==
        if(intent == null){
            Toast.makeText(this, "Something Went wrong..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Getting the User id passed by the intent ==
        userId = intent.getIntExtra("UserId", -1);
        if(userId == -1){
            Toast.makeText(this, "Invalid User ..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Creating the new thread for getting the User data ==
        new Thread(()->{

            // == Collecting the data from the database ==
            User user = Database.getInstance(this).userDao().getUser(userId);

            // == Update the UI based on the data ==
            updateUI(user);

        }).start();

        // == setting on click listener ==
        saveButton.setOnClickListener(v-> updateUserData());
    }

    private void updateUserData() {

        // == validating the user data ==
        if(firstName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "First Name is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        if(lastName.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Last Name is Empty..", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phoneNumber.getText().toString().trim().length() != 10){
            Toast.makeText(this, "Invalid Phone number..", Toast.LENGTH_SHORT).show();
            return;
        }

        String firstName = this.firstName.getText().toString().toUpperCase();
        String lastName = this.lastName.getText().toString().toUpperCase();
        String phoneNumber = this.phoneNumber.getText().toString().toLowerCase();

        // == Creating new Thread for Database Operation ==
        new Thread(()->{
            int result = Database.getInstance(this).userDao().updateUserDetails(firstName, lastName, phoneNumber, userId);
            if(result == 1){
                runOnUiThread(()-> {
                    Toast.makeText(this, "User data updated ..", Toast.LENGTH_SHORT).show();

                    // == Closing the activity ==
                    finish();
                });
            }else{
                runOnUiThread(()-> {
                    Toast.makeText(this, "Something went wrong ..", Toast.LENGTH_SHORT).show();

                    // == Closing the activity ==
                    finish();
                });
            }
        }).start();
    }

    @SuppressLint("DefaultLocale")
    private void updateUI(User user) {
        firstName.setText(user.getFirstName());
        lastName.setText(user.getLastName());
        phoneNumber.setText(user.getMobileNumber());
        userIdTextView.setText(String.format("%s%05d","FGF", userId));
        userImage.setImageURI(Uri.parse(user.getImagePath()));
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