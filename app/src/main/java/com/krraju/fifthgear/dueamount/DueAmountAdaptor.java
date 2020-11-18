package com.krraju.fifthgear.dueamount;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class DueAmountAdaptor extends RecyclerView.Adapter<DueAmountAdaptor.ViewHolder> {

    // == Constants ==
    private static final int SEND_SMS_REQUEST_CODE = 111;

    // == fields ==
    private List<User> users;
    private Context context;

    // == constructor ==
    public DueAmountAdaptor(Context context) {

        this.context = context;
        users = new ArrayList<>();

        new Thread(() -> {
            // == Collecting the data from database ==
            users = Database.getInstance(context).userDao().getUsersHavingDueAmount();

            ((Activity) context).runOnUiThread(() -> {

                // == Finding the text view by id ==
                TextView isListEmpty = ((Activity) context).findViewById(R.id.is_list_empty);

                if (users == null || users.isEmpty()) {
                    // == Showing the text view if the list is empty ==
                    isListEmpty.setVisibility(View.VISIBLE);
                } else {
                    // == Hiding the text view if the list is not empty ==
                    isListEmpty.setVisibility(View.GONE);
                }
            });

            // == Updating the ui on ui thread ==
            // == notifying the data change ==
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.due_amount_recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"DefaultLocale", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = users.get(position);
        holder.dueAmount.setText(String.format("%.2f", user.getDueAmount()));
        holder.userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        holder.imageView.setImageURI(Uri.parse(user.getImagePath()));
        if (user.getStatus() == Status.ACTIVE) {
            holder.view.setBackgroundResource(R.drawable.active_background_layout);
        } else {
            holder.view.setBackgroundResource(R.drawable.inactive_background_layout);
        }
        holder.layout.setOnClickListener(v -> showPaymentDialog(user));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView userName;
        private TextView dueAmount;
        private ImageView imageView;
        private LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_photo);
            view = itemView.findViewById(R.id.active_or_inactive);
            userName = itemView.findViewById(R.id.user_name);
            dueAmount = itemView.findViewById(R.id.due_amount);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    // == This method will add the payment for the user ==
    @SuppressLint("DefaultLocale")
    private void showPaymentDialog(User user) {

        int userId = user.getUser_id();

        // == checking the user Id ==
        if (userId == -1 || userId == 0) {
            // == Showing the error dialog ==
            Toast.makeText(context, "In Valid User Id Please check..", Toast.LENGTH_SHORT).show();
            return;
        }

        // == Creating new Dialog ==
        Dialog dialog = new Dialog(context);

        // == Setting the view of the dialog ==
        dialog.setContentView(R.layout.due_clear_add_amount_dialog);

        // == making the background transparent ==
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        // == finding the dialog elements by id ==
        TextView name = dialog.findViewById(R.id.name);
        TextView dueAmount = dialog.findViewById(R.id.due_amount);
        TextView cancel = dialog.findViewById(R.id.cancel);
        EditText amount = dialog.findViewById(R.id.amount);
        Button addPaymentButton = dialog.findViewById(R.id.top_up);

        // == Getting user information ==
        name.setText(String.format("%s: %s %s", "Name", user.getFirstName(), user.getLastName()));
        dueAmount.setText(String.format("%s: %.2f", "Due Amount", user.getDueAmount()));

        // == setting the on click listener ==
        cancel.setOnClickListener(v -> ((Activity) context).runOnUiThread(() -> new AlertDialog.Builder(context)
                .setTitle("INFO")
                .setMessage(String.format("%s%.2f%s%s", "Are You sure you want to Clear the Due amount ", user.getDueAmount(), " of the user ", user.getFirstName()))
                .setPositiveButton("Yes", (dialog1, which) -> {
                    if (user.getDueAmount() != 0) {
                        new Thread(() -> {
                            Database.getInstance(context).userDao().clearAllDueAmount(userId);
                            ((Activity) context).runOnUiThread(() -> {
                                Toast.makeText(context, "Due amount was cleared..", Toast.LENGTH_SHORT).show();
                                dialog1.dismiss();
                                dialog.dismiss();

                                ((Activity) context).finish();
                                context.startActivity(new Intent(((Activity) context).getIntent()));
                            });
                        }).start();
                    }
                })
                .setNegativeButton("NO", (dialog1, which) -> {
                    dialog1.dismiss();
                    dialog.dismiss();
                })
                .setCancelable(false)
                .show()));

        addPaymentButton.setOnClickListener(v -> {
            // == Checking the amount for empty ==
            if (amount.getText().toString().isEmpty()) {
                Toast.makeText(context, "Amount is Empty..", Toast.LENGTH_SHORT).show();
                return;
            }

            // == Checking the amount for negative or zero ==
            float newAmount = Float.parseFloat(amount.getText().toString());
            if (newAmount <= 0) {
                Toast.makeText(context, "Invalid Amount .. ", Toast.LENGTH_SHORT).show();
                return;
            }

            new Thread(() -> {
                Transaction transaction = new Transaction(LocalDate.now(), newAmount, userId);
                int result = Database.getInstance(context).addTransaction(transaction);
                if (result == 1) {
                    float remainingAmount = user.getDueAmount() - newAmount;

                    // == Checking the Permission For SEND_SMS ==
                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {

                        ((Activity) context).runOnUiThread(() -> {
                            // == Creating the SMSManager ==
                            SmsManager smsManager = SmsManager.getDefault();

                            if (remainingAmount > 0) {
                                // == Generating the Message ==
                                String message = String.format("Hi %s %s, Your payment of %.2f Rs was successful on %s, we request you to pay the due amount %.2f Rs as soon as possible. Your account is going to expire on %s.\nThankyou. \n\nRegardes Fifth Gear Fitness",
                                        user.getFirstName(), user.getLastName(), newAmount, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), remainingAmount, user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                                // == Dividing the message ==
                                ArrayList<String> pasts = smsManager.divideMessage(message);

                                // == Sending the message ==
                                smsManager.sendMultipartTextMessage(user.getMobileNumber(), null, pasts, null, null);

                            } else {
                                // == Sending the Text message ==
                                smsManager.sendTextMessage(user.getMobileNumber(), null,
                                        // == Generating the Message ==
                                        String.format("Hi %s %s, Your payment of %.2f Rs was successful on %s, Your account is going to expire on %s.\nThankyou. \n\nRegardes Fifth Gear Fitness",
                                                user.getFirstName(), user.getLastName(), newAmount, LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")), user.getDueDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))),
                                        null, null);
                            }
                            // == Showing the message ==

                            Toast.makeText(context, "Payment added Successfully...", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                            // == restating the Activity ==
                            ((Activity) context).finish();
                            context.startActivity(((Activity) context).getIntent());
                        });

                    } else {
                        // == If Permission is not granted asking for the Permission ==
                        ActivityCompat.requestPermissions(((Activity) context), new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_REQUEST_CODE);
                    }
                }

            }).start();
        });

        // == Showing the dialog to add the payment ==
        dialog.show();

    }

}