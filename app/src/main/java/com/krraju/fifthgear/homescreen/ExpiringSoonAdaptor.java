package com.krraju.fifthgear.homescreen;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExpiringSoonAdaptor extends RecyclerView.Adapter<ExpiringSoonAdaptor.ViewHolder> {

    // == constants ==
    private static final String TAG = ExpiringSoonAdaptor.class.getSimpleName();
    private static final int SEND_SMS_REQUEST_CODE = 111;


    // == fields ==
    private List<User> userList;
    private Context context;

    // == constructor ==

    public ExpiringSoonAdaptor(Context context) {
        this.userList = new ArrayList<>();
        this.context = context;

        // == Creating new Thread for performing database operation ==
        new Thread(() -> {
            // == Getting All Users ==
            List<User> userList = Database.getInstance(context).userDao().getUserOnDueDate();

            // == Iterating All user ==
            for (User user : userList) {
                // == Checking if the user status is Active and user due date not null ==
                if (user.getStatus() == Status.ACTIVE && user.getDueDate() != null) {

                    // == if User due date is tomorrow or day after tomorrow then adding that user to list ==
                    if (user.getDueDate().minusDays(1).equals(LocalDate.now()) || user.getDueDate().minusDays(2).equals(LocalDate.now())) {
                        // == Adding the user to list ==
                        this.userList.add(user);
                    }
                }
            }

            // == finding the view by id ==
            TextView isExpiringSoonEmpty = ((Activity) context).findViewById(R.id.is_expiring_soon_empty);

            // == checking the List and setting the visibility ==
            if (this.userList.isEmpty()) {
                isExpiringSoonEmpty.setVisibility(View.VISIBLE);
            } else {
                isExpiringSoonEmpty.setVisibility(View.GONE);
            }
            // == notifying data change ==
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.expiring_soon_recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = userList.get(position);
//        holder.userImage.setImageURI(Uri.parse(user.getImagePath()));
        holder.userImage.setImageBitmap(user.getImage());
        holder.userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        holder.expiringSoon.setBackgroundResource(R.drawable.expiring_soon_background_layout);
        holder.layout.setOnClickListener(v -> showDialog(user));
    }

    private void showDialog(User user) {
        // == Checking the Permission For SEND_SMS ==
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
            // == If Permission is Granted showing the alert dialog ==

            // == Creating the new Alert Dialog ==
            new AlertDialog.Builder(context, R.style.AlertDialog)
                    .setTitle("SEND MESSAGE")
                    .setCancelable(false)
                    .setMessage(String.format("Do you want to send expiring soon message to %s %s ?", user.getFirstName(), user.getLastName()))
                    .setPositiveButton("SEND", (dialog1, which) -> {
                        // == Creating the SMSManager ==
                        SmsManager smsManager = SmsManager.getDefault();

                        // == Sending the Text message ==
                        smsManager.sendTextMessage(user.getMobileNumber(), null,
                                // == Generating the Message ==
                                String.format("Hi %s %s, Your payment due date is going to expire on %s. Please Pay the fees.\nThank you. \n\nRegards Fifth Gear Fitness",
                                        user.getFirstName(), user.getLastName(), user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))),
                                null, null);
                        // == Showing the Toast message after sending the message ==
                        Toast.makeText(context, String.format("Message sent to %s %s ..", user.getFirstName(), user.getLastName()), Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss())
                    // == Showing the Alert Dialog ==
                    .show();
        } else {
            // == If Permission is not granted asking for the Permission ==
            ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView userName;
        private View expiringSoon;
        private View layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_photo);
            userName = itemView.findViewById(R.id.user_name);
            expiringSoon = itemView.findViewById(R.id.active_or_inactive);
            layout = itemView.findViewById(R.id.layout);
        }
    }
}