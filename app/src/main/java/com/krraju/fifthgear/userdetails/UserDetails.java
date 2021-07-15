package com.krraju.fifthgear.userdetails;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.edituser.EditUserDetailsActivity;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Plan;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class UserDetails extends AppCompatActivity {

    // == Constants ==
    private static final String TAG = UserDetails.class.getSimpleName();
    private static final int CALL_PHONE_PERMISSION_REQUEST_CODE = 321;
    private static final int SEND_SMS_REQUEST_CODE = 111;

    // == fields ==
    private int userId;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // == Preventing the Screen from taking screenshots ==
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        // == local Fields ==
        Toolbar toolbar = findViewById(R.id.tool_bar);
        Button addPaymentButton = findViewById(R.id.top_up);
        Button topUpButton = findViewById(R.id.top_up_button);
        RecyclerView recyclerView = findViewById(R.id.transaction_recycler_view);
        TextView isListEmpty = findViewById(R.id.list_is_empty);
        ImageView zoomInImage = findViewById(R.id.user_enlarged_image);
        ImageView zoomOutImage = findViewById(R.id.profile_photo);
        FrameLayout userImageFrame = findViewById(R.id.user_image_frame);
        ConstraintLayout outerLayout = findViewById(R.id.out_side_view);
        ImageButton callUser = findViewById(R.id.call_user);

        // == Setting the tool bar as action bar this activity ==
        setSupportActionBar(toolbar);

        // == setting back or up button for the tool bar ==
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        // == Getting the Intent ==
        Intent intent = getIntent();

        // == Checking the intent for not null ==
        if (intent == null) {
            Toast.makeText(this, "Something Went wrong..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Getting the User id passed by the intent ==
        userId = intent.getIntExtra("userId", -1);
        if (userId == -1) {
            Toast.makeText(this, "Invalid User ..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Creating the new thread for getting the User data ==
        new Thread(() -> {

            // == Collecting the data from the database ==
            User user = Database.getInstance(this).userDao().getUser(userId);

            // == Update the UI based on the data ==
            updateUI(user);

            // == Calling the User ==
            runOnUiThread(() -> callUser.setOnClickListener(v -> {
                // Creating the INTENT to call the user ==
                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + user.getMobileNumber()));

                // == Checking for the User Permission ==
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // == If Permission is not granted requesting for the permission ==
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);
                    return;
                }
                // == starting the activity with call intent ==
                startActivity(callIntent);
            }));

        }).start();

        // == Setting on click listener for buttons ==
        addPaymentButton.setOnClickListener(v -> {
            // == Checking the Permission For SEND_SMS ==
            if (ContextCompat.checkSelfPermission(UserDetails.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                // == Showing Payment dialog ==
                showPaymentDialog();
            } else {
                // == If permission is not granted asking permission ==
                ActivityCompat.requestPermissions(UserDetails.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
            }
        });
        topUpButton.setOnClickListener(v -> showTopUpDialog());
        zoomOutImage.setOnClickListener(v -> {
            zoomInImage.setVisibility(View.VISIBLE);
            zoomOutImage.setVisibility(View.INVISIBLE);
            userImageFrame.setVisibility(View.VISIBLE);
        });

        outerLayout.setOnClickListener(v -> {
            zoomInImage.setVisibility(View.INVISIBLE);
            userImageFrame.setVisibility(View.INVISIBLE);
            zoomOutImage.setVisibility(View.VISIBLE);
        });


        // == Creating the adopter for the Recyclerview ==
        recyclerView.setAdapter(new TransactionAdaptor(this, userId, isListEmpty));

        // == Setting the layout manager for the adaptor ==
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }


    // == Used to extend the validity of the User ==
    @SuppressLint("DefaultLocale")
    private void showTopUpDialog() {
        // == checking the user Id ==
        if (userId == -1 || userId == 0) {
            // == Showing the error dialog ==
            Toast.makeText(this, "In Valid User Id Please check..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Creating new Dialog ==
        Dialog dialog = new Dialog(this);

        // == Setting the view of the dialog ==
        dialog.setContentView(R.layout.top_up_dialog_layout);

        // == making the background transparent ==
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // == User can't cancel the dialog ==
        dialog.setCancelable(false);

        // == finding the dialog elements by id ==
        TextView name = dialog.findViewById(R.id.name);
        TextView dueAmount = dialog.findViewById(R.id.due_amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        EditText fees = dialog.findViewById(R.id.amount);
        Button topUpButton = dialog.findViewById(R.id.top_up);
        Spinner plan = dialog.findViewById(R.id.plan);

        // == Adding the Adaptor ==
        ArrayAdapter<CharSequence> planAdapter = ArrayAdapter.createFromResource(this, R.array.plans, android.R.layout.simple_list_item_1);
        plan.setAdapter(planAdapter);

        // == setting the plan spinner ==
        loadFeesFile(plan, fees);

        // == Getting user information ==
        new Thread(() -> {
            User user = Database.getInstance(this).userDao().getUser(userId);
            runOnUiThread(() -> {
                name.setText(String.format("%s: %s %s", "Name", user.getFirstName(), user.getLastName()));
                dueAmount.setText(String.format("%s: %.2f", "Due Amount", user.getDueAmount()));
            });
        }).start();

        // == setting the on click listener ==
        cancel.setOnClickListener(v -> dialog.dismiss());
        topUpButton.setOnClickListener(v -> {
            // == Checking the fees for empty ==
            if (fees.getText().toString().isEmpty()) {
                Toast.makeText(this, "Amount is Empty..", Toast.LENGTH_SHORT).show();
                return;
            }

            // == Checking the fees for negative or zero ==
            float newAmount = Float.parseFloat(fees.getText().toString());
            if (newAmount <= 0) {
                Toast.makeText(this, "Invalid Amount .. ", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {

                // == Collecting the plan ==
                Plan userPlan = Plan.fromString(((String) plan.getSelectedItem()));

                int result = Database.getInstance(this).topUpUser(userId, userPlan, newAmount);

                if (result == 1) {
                    runOnUiThread(() -> Toast.makeText(this, "Top Up was Successful...", Toast.LENGTH_SHORT).show());
                    dialog.dismiss();

                    // == restating the Activity ==
                    finish();
                    startActivity(getIntent());
                }
            }).start();
        });

        // == Showing the dialog to add the payment ==
        dialog.show();

    }

    // == load the fees from properties file ==
    private void loadFeesFile(Spinner planSpinner, EditText fees) {

        // == Constants ==
        final String FEES_MONTHLY = "Fees.MONTHLY";
        final String FEES_QUARTERLY = "Fees.QUARTERLY";
        final String FEES_HALF_YEARLY = "Fees.HALF_YEARLY";
        final String FEES_ANNUAL = "Fees.ANNUAL";
        final String FEES = "Fees";
        final SharedPreferences sharedPreferences = getSharedPreferences(FEES,MODE_PRIVATE);


//        // == Opening the Properties file to get the fees ==
//        File file = new File("/storage/emulated/0/FifthGear/", "fees.properties");
//        try (InputStream inputStream = new FileInputStream(file)) {
//
//            // == Creating the Property instance ==
//            Properties properties = new Properties();
//
//            // == Loading the from input stream ==
//            properties.load(inputStream);

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
                            fees.setText(String.valueOf(sharedPreferences.getInt(FEES_MONTHLY,0)));
                            break;

                        // == If Selected item is QUARTERLY ==
                        case 1:
                            // == Setting the QUARTERLY fee ==
                            fees.setText(String.valueOf(sharedPreferences.getInt(FEES_QUARTERLY,0)));
                            break;

                        // == If Selected item is HALF YEARLY ==
                        case 2:
                            // == Setting the HALF YEARLY fee ==
                            fees.setText(String.valueOf(sharedPreferences.getInt(FEES_HALF_YEARLY,0)));
                            break;

                        // == If Selected item is ANNUAL ==
                        case 3:
                            // == Setting the ANNUAL fee ==
                            fees.setText(String.valueOf(sharedPreferences.getInt(FEES_ANNUAL,0)));
                            break;
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

//        } catch (Exception e) {
//            // == Showing the error message ==
//            Toast.makeText(this, "Can't Load the Fees please try editing the fees ..", Toast.LENGTH_SHORT).show();
//        }
    }


    // == This method will add the payment for the user ==
    @SuppressLint("DefaultLocale")
    private void showPaymentDialog() {
        // == checking the user Id ==
        if (userId == -1 || userId == 0) {
            // == Showing the error dialog ==
            Toast.makeText(this, "In Valid User Id Please check..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Creating new Dialog ==
        Dialog dialog = new Dialog(this);

        // == Setting the view of the dialog ==
        dialog.setContentView(R.layout.add_patyment_dialog_layout);

        // == making the background transparent ==
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        // == User can't cancel the dialog ==
        dialog.setCancelable(false);

        // == finding the dialog elements by id ==
        TextView name = dialog.findViewById(R.id.name);
        TextView dueAmount = dialog.findViewById(R.id.due_amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        EditText amount = dialog.findViewById(R.id.amount);
        Button addPaymentButton = dialog.findViewById(R.id.top_up);

        // == Getting user information ==
        new Thread(() -> {
            user = Database.getInstance(this).userDao().getUser(userId);
            runOnUiThread(() -> {
                name.setText(String.format("%s: %s %s", "Name", user.getFirstName(), user.getLastName()));
                dueAmount.setText(String.format("%s: %.2f", "Due Amount", user.getDueAmount()));
            });
        }).start();

        // == setting the on click listener ==
        cancel.setOnClickListener(v -> dialog.dismiss());
        addPaymentButton.setOnClickListener(v -> {
            // == Checking the amount for empty ==
            if (amount.getText().toString().isEmpty()) {
                Toast.makeText(this, "Amount is Empty..", Toast.LENGTH_SHORT).show();
                return;
            }

            // == Checking the amount for negative or zero ==
            float newAmount = Float.parseFloat(amount.getText().toString());
            if (newAmount <= 0) {
                Toast.makeText(this, "Invalid Amount .. ", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Transaction transaction = new Transaction(LocalDate.now(), newAmount, userId);
                int result = Database.getInstance(this).addTransaction(transaction);
                if (result == 1) {
                    float remainingAmount = user.getDueAmount() - newAmount;

                    // == Checking the Permission For SEND_SMS ==
                    if (ContextCompat.checkSelfPermission(UserDetails.this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                        runOnUiThread(() -> {
                            // == Creating the SMSManager ==
                            SmsManager smsManager = SmsManager.getDefault();

                            if (remainingAmount > 0) {
                                // == Generating the Message ==
                                String message = String.format("Hi %s %s, Your payment of Rs %.2f was successful on %s, we request you to pay the due amount Rs %.2f as soon as possible. Your account is going to expire on %s.\nThank you. \n\nRegards Fifth Gear Fitness",
                                        user.getFirstName(), user.getLastName(), newAmount, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), remainingAmount, user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                                // == Dividing the message ==
                                ArrayList<String> parts = smsManager.divideMessage(message);

                                // == Sending the message ==
                                smsManager.sendMultipartTextMessage(user.getMobileNumber(), null, parts, null, null);

                            } else {
                                // == Generating the Message ==
                                String message = String.format("Hi %s %s, Your payment of Rs %.2f was successful on %s, Your account is going to expire on %s.\nThank you. \n\nRegards Fifth Gear Fitness",
                                        user.getFirstName(), user.getLastName(), newAmount, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                                // == Dividing the message ==
                                ArrayList<String> parts = smsManager.divideMessage(message);

                                // == Sending the Text message ==
                                smsManager.sendMultipartTextMessage(user.getMobileNumber(), null, parts, null, null);

                            }
                            // == Showing the message ==

                            Toast.makeText(UserDetails.this, "Payment added Successfully...", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            // == restating the Activity ==
                            finish();
                            startActivity(getIntent());
                        });

                    } else {
                        // == If Permission is not granted asking for the Permission ==
                        ActivityCompat.requestPermissions(UserDetails.this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                    }
                    runOnUiThread(() -> Toast.makeText(this, "Payment added Successfully...", Toast.LENGTH_SHORT).show());
                    dialog.dismiss();

                    // == restating the Activity ==
                    finish();
                    startActivity(getIntent());
                }
                Log.d(TAG, "showPaymentDialog: Transaction->" + Database.getInstance(this).transactionDao().getAllTransaction());
                Log.d(TAG, "showPaymentDialog: Transaction->" + Database.getInstance(this).userDao().getUser(userId));

            }).start();
        });

        // == Showing the dialog to add the payment ==
        dialog.show();

    }

    // == Updating the ui based on the data ==
    @SuppressLint("DefaultLocale")
    private void updateUI(User user) {

        // == finding the view and Setting the data ==
        ((TextView) findViewById(R.id.due_amount)).setText(String.format("%.2f", user.getDueAmount()));
        if (user.getDueDate() == null) {
            ((TextView) findViewById(R.id.due_date)).setText(String.format("%s", "00-00-0000"));
        } else {
            ((TextView) findViewById(R.id.due_date)).setText(user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        }
        ((TextView) findViewById(R.id.user_id)).setText(String.format("%s%05d", "FGF", user.getUser_id()));
        ((TextView) findViewById(R.id.first_name_text)).setText(String.format("%s", user.getFirstName()));
        ((TextView) findViewById(R.id.last_name_text)).setText(String.format("%s", user.getLastName()));
        ((TextView) findViewById(R.id.age)).setText(String.format("%d", user.getAge()));
        ((TextView) findViewById(R.id.gender)).setText(String.format("%s", user.getGender()));
        ((TextView) findViewById(R.id.height)).setText(String.format("%.2f", user.getHeight()));
        ((TextView) findViewById(R.id.weight)).setText(String.format("%.2f", user.getWeight()));
        ((TextView) findViewById(R.id.address)).setText(String.format("%s", user.getAddress()));
        ((TextView) findViewById(R.id.mobile_number)).setText(String.format("%s", user.getMobileNumber()));
        ((TextView) findViewById(R.id.email)).setText(String.format("%s", user.getEmail()));
        ((TextView) findViewById(R.id.fees)).setText(String.format("%.2f", user.getFees()));
        ((TextView) findViewById(R.id.plan)).setText(String.format("%s", user.getPlan()));
        ((TextView) findViewById(R.id.health_issue)).setText(String.format("%s", user.getHealthIssue()));
        ((TextView) findViewById(R.id.occupation)).setText(String.format("%s", user.getOccupation()));
        ((TextView) findViewById(R.id.joining_date)).setText(String.format("%s", user.getJoiningDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        ((TextView) findViewById(R.id.status)).setText(String.format("%s", user.getStatus()));
//        ((ImageView) findViewById(R.id.profile_photo)).setImageURI(Uri.parse(user.getImagePath()));
//        ((ImageView) findViewById(R.id.user_enlarged_image)).setImageURI(Uri.parse(user.getImagePath()));
        ((ImageView) findViewById(R.id.profile_photo)).setImageBitmap(user.getImage());
        ((ImageView) findViewById(R.id.user_enlarged_image)).setImageBitmap(user.getImage());
    }

    // == Adding functionality for the back or up button of tool bar ==
    @Override
    public boolean onSupportNavigateUp() {
        // == closing the activity ==
        finish();

        // == returning true because we have handled method ==
        return true;
    }

    // == Adding functionality on edit icon presses ==
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        // == Checking the id of the selected menu item ==
        if (item.getItemId() == R.id.edit_user) {

            // == Starting the Edit User Details Activity ==
            Intent intent = new Intent(this, EditUserDetailsActivity.class);
            intent.putExtra("UserId", userId);
            startActivity(intent);
        }

        // == returning false because we need the handler to call onSupportNavigateUp method  ==
        return false;
    }

    // == Adding the menu for the Tool bar ==

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // == Adding the menu for the tool bar ==
        getMenuInflater().inflate(R.menu.edit_user, menu);

        // == returning true because we have handled method ==
        return true;
    }

    // == Handling the restart method ==
    @Override
    protected void onRestart() {

        // == closing the current activity ==
        finish();

        // == starting the activity ==
        startActivity(getIntent());

        super.onRestart();
    }
}