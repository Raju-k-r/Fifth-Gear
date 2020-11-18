package com.krraju.fifthgear;

import android.content.Intent;
import android.hardware.biometrics.BiometricPrompt;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Handler;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.krraju.fifthgear.homescreen.HomeScreen;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FlashScreenActivity extends AppCompatActivity {

    // == Constants ==
    private static final int TIME_DELAY = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);

        // == Checking for the android SDK version and Prompting the Biometric Authentication ==
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            // == Creating the executor which is used by BiometricPrompt ==
            Executor executor = Executors.newSingleThreadExecutor();

            // == Building the BiometricPrompt ==
            BiometricPrompt biometricPrompt = new BiometricPrompt.Builder(this)
                    .setTitle("Fifth Gear Fitness")
                    .setSubtitle("Admin Authentication")
                    .setDescription("Please provide your fingerprint for enter into the application")
                    .setNegativeButton("Cancel", executor, (dialog, which) -> finish())
                    .build();

            // == Authenticating the User ==
            biometricPrompt.authenticate(new CancellationSignal(), executor, new BiometricPrompt.AuthenticationCallback() {
                @Override
                public void onAuthenticationError(int errorCode, CharSequence errString) {
                    runOnUiThread(() -> {
                        // == Showing the error message ==
                        Toast.makeText(FlashScreenActivity.this, "  User Dismissed the Dialog..  ", Toast.LENGTH_SHORT).show();
                        finish();
                    });
                }

                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    // == Showing the success message ==
                    runOnUiThread(() -> Toast.makeText(FlashScreenActivity.this, "  Authenticated..  ", Toast.LENGTH_SHORT).show());

                    // == On Authentication Successful loading the activity ==
                    loadAndStartActivity();
                }

                @Override
                public void onAuthenticationFailed() {
                    runOnUiThread(() -> {
                        // == Showing the Fail message ==
                        Toast.makeText(FlashScreenActivity.this, "Authentication Failed Please try again.", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            // == If Device is not comparable with Biometric Authentication loading the activity ==
            loadAndStartActivity();
        }
    }

    public void loadAndStartActivity() {
        // == Updating the user Expiry date and User state ==
        new Thread(() -> {

            // == Getting the database instance ==
            Database database = Database.getInstance(this);

            // == Getting all users ==
            List<User> users = database.userDao().getAllUser();

            for (User user : users) {
                // == Checking the user due date and updating the user status ==
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