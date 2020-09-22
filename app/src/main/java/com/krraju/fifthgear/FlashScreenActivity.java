package com.krraju.fifthgear;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.krraju.fifthgear.homescreen.HomeScreen;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;
import java.util.List;

public class FlashScreenActivity extends AppCompatActivity {

    // == Constants ==
    private static final int TIME_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);

        // == Updating the user Expiry date and User state ==
        new Thread(() -> {

            // == Getting the database instance ==
            Database database = Database.getInstance(this);

            // == Getting all users ==
            List<User> users = database.userDao().getAllUser();
            for (User user : users) {
                if (user.getDueDate() != null && user.getDueDate().isBefore(LocalDate.now())) {
                    database.userDao().updateDueDateAndStatus(null, Status.INACTIVE, user.getUser_id());
                }
            }

            runOnUiThread(() -> {
                // == Creating the Handler for start the Activity after TIME_DELAY ==
                new Handler().postDelayed(() -> {

                    // == Creating the Intent For Stating Home Activity ==
                    Intent intent = new Intent(FlashScreenActivity.this, HomeScreen.class);

                    // == Setting the Flags To clear current task and Start the new Task ==
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                    // == Starting the Intent ==
                    startActivity(intent);

                }, TIME_DELAY);
            });
        }).start();
    }

}