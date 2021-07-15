package com.krraju.fifthgear.expenditure;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.expenditure.Expenditure;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class ExpenditureAdaptor extends RecyclerView.Adapter<ExpenditureAdaptor.ViewHolder> implements Filterable {

    // == fields ==
    private List<Expenditure> allExpenditure;
    private final List<Expenditure> expenditure;
    private final Context context;

    // == constructor ==
    public ExpenditureAdaptor(Context context) {
        // == initializing the fields ==
        this.allExpenditure = new ArrayList<>();
        this.expenditure = new ArrayList<>();
        this.context = context;

        // == creating new thread for performing database operation ==
        new Thread(() -> {
            // == Getting all Expenditure ==
            allExpenditure = Database.getInstance(context).expenditureDao().getAllExpenditure();

            Log.d("Expenditure", "ExpenditureAdaptor: " + allExpenditure);

            // == adding all transaction ==
            getFilter().filter("ALL");

            // == Notifying the the data change ==
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_screen_recycler_view_trasaction_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Expenditure expenditure = this.expenditure.get(position);

        holder.slNumber.setText(String.format("%d", position+1));
        holder.shortDescription.setText(expenditure.getShortDescription());
        holder.date.setText(expenditure.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        holder.amount.setText(String.format("%.2f",expenditure.getAmount()));
        holder.layout.setOnClickListener(v-> showDialog(expenditure));
    }

    @SuppressLint("DefaultLocale")
    private void showDialog(Expenditure expenditure) {
        // == Creating new Dialog ==
        Dialog dialog = new Dialog(context);

        // == Setting the view of the dialog ==
        dialog.setContentView(R.layout.details_of_expenditure_dialog);

        // == making the background transparent ==
        assert dialog.getWindow() != null;
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);


        // == finding the dialog elements by id ==
        TextView shortDescription = dialog.findViewById(R.id.short_description);
        TextView amount = dialog.findViewById(R.id.amount);
        TextView detailedDescription = dialog.findViewById(R.id.detailed_description);
        Button closeButton = dialog.findViewById(R.id.close_button);

        shortDescription.setText(expenditure.getShortDescription());
        detailedDescription.setText(expenditure.getDetailedDescription());
        amount.setText(String.format("%.2f",expenditure.getAmount()));
        closeButton.setOnClickListener(v-> dialog.dismiss());

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return expenditure.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView slNumber;
        private final TextView shortDescription;
        private final TextView date;
        private final TextView amount;
        private final LinearLayout layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slNumber = itemView.findViewById(R.id.sl_no);
            shortDescription = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
            layout = itemView.findViewById(R.id.layout);
        }
    }

    // == Adding Filter to Recycler View ==
    @Override
    public Filter getFilter() {
        // == Returning the filter which is created ==
        return filter;
    }

    // == Creating Custom filter class for Recycler View Filter ==
    private final Filter filter = new Filter() {

        private double total;

        // == This method will filter the User based on the given char sequence ==
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // == Resetting the total amount ==
            total = 0;

            // == Creating a new List which holds the filtered Users ==
            List<Expenditure> filteredList = new ArrayList<>();

            // == Checking for the constraint ==
            switch (constraint.toString()) {
                case "ALL":
                    // == Showing all the transaction ==
                    filteredList.addAll(allExpenditure);
                    // == Counting the Amount ==
                    for (Expenditure transaction : allExpenditure) {
                        total += transaction.getAmount();
                    }
                    break;
                case "TODAY":
                    // == Looping throw all the User to check for char sequence ==
                    for (Expenditure transaction : allExpenditure) {
                        // == Checking the constraint ==
                        if (transaction.getDate().equals(LocalDate.now())) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "YESTERDAY":
                    // == Looping throw all the User to check for char sequence ==
                    for (Expenditure transaction : allExpenditure) {
                        // == Checking the constraint ==
                        if (transaction.getDate().plusDays(1).equals(LocalDate.now())) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "THIS WEEK":
                    // == Looping throw all the User to check for char sequence ==
                    for (Expenditure transaction : allExpenditure) {

                        // == Checking the constraint ==
                        if (transaction.getDate().isAfter(LocalDate.now().minusDays(7)) && transaction.getDate().isBefore(LocalDate.now().plusDays(1))) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "THIS MONTH":
                    // == Looping throw all the User to check for char sequence ==
                    LocalDate thisMonthFirstDate = YearMonth.now().atDay(1);
                    LocalDate thisMonthLastDate = YearMonth.now().atEndOfMonth();

                    for (Expenditure transaction : allExpenditure) {

                        // == Checking the constraint ==
                        if (transaction.getDate().equals(thisMonthFirstDate) || transaction.getDate().equals(thisMonthLastDate)) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        } else if (transaction.getDate().isBefore(thisMonthLastDate) && transaction.getDate().isAfter(thisMonthFirstDate)) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }

                    }
                    break;

                case "LAST MONTH":
                    // == Looping throw all the User to check for char sequence ==
                    LocalDate lastMonthFirstDate = YearMonth.now().minusMonths(1).atDay(1);
                    LocalDate lastMonthLastDate = YearMonth.now().minusMonths(1).atEndOfMonth();


                    for (Expenditure transaction : allExpenditure) {

                        // == Checking the constraint ==
                        if (transaction.getDate().equals(lastMonthFirstDate) || transaction.getDate().equals(lastMonthLastDate)) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        } else if (transaction.getDate().isBefore(lastMonthLastDate) && transaction.getDate().isAfter(lastMonthFirstDate)) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "LAST 6 MONTHS":
                    // == Looping throw all the User to check for char sequence ==

                    LocalDate firstDay = YearMonth.now().atDay(1).minusMonths(6);

                    for (Expenditure transaction : allExpenditure) {
                        // == Checking the constraint ==
                        if (transaction.getDate().isAfter(firstDay)) {
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                default:
                    expenditure.addAll(allExpenditure);
                    // == Counting the Amount ==
                    for (Expenditure transaction : allExpenditure) {
                        total += transaction.getAmount();
                    }
                    break;
            }


            // == Creating the Filter result Object which holds our result ==
            FilterResults filterResults = new FilterResults();


            // == Adding the Filtered list to filter result value ==
            filterResults.values = filteredList;


            return filterResults;
        }

        // == This method will update the UI after the filter finished ==
        @SuppressLint("DefaultLocale")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // == Clearing the current items in recycler view ==
            expenditure.clear();

            // == adding the filtered list to the recycler view ==
            expenditure.addAll((Collection<? extends Expenditure>) results.values);

            Log.d("Expenditure", "bind: =>" + expenditure);

            // == Finding the List is Empty TextView by id  and setting the Visibility ==
            TextView isListEmpty = ((Activity) context).findViewById(R.id.empty_list_text_view);
            if (expenditure.isEmpty()) {
                isListEmpty.setVisibility(View.VISIBLE);
            } else {
                isListEmpty.setVisibility(View.GONE);
            }

            // == Updating the Amount ==
            TextView totalAmount = ((Activity) context).findViewById(R.id.grand_total_text_view);
            totalAmount.setText(String.format("%s : %.2f", "GRAND TOTAL", total));

            // == notifying recycler view that the data has changed ==
            notifyDataSetChanged();
        }
    };

}
