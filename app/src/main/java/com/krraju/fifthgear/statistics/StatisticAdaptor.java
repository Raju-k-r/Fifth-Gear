package com.krraju.fifthgear.statistics;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.utils.HomeScreenTransaction;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class StatisticAdaptor extends RecyclerView.Adapter<StatisticAdaptor.ViewHolder> implements Filterable {

    // == fields ==
    private List<HomeScreenTransaction> allTransaction;
    private List<HomeScreenTransaction> transactions;
    private Context context;

    // == constructor ==
    public StatisticAdaptor(Context context) {

        // == initializing the fields ==
        this.allTransaction = new ArrayList<>();
        this.transactions = new ArrayList<>();
        this.context = context;

        // == creating new thread for performing database operation ==
        new Thread(()->{
            // == Getting all Transaction ==
            List<Transaction> allTransaction = Database.getInstance(context).transactionDao().getAllTransactionInDesc();

            for(Transaction transaction : allTransaction){
                this.allTransaction.add(new HomeScreenTransaction(transaction, context));
            }

            // == adding all transaction ==
            getFilter().filter("ALL");

            // == Notifying the the data change ==
            ((Activity)context).runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_recycler_view_trasaction_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HomeScreenTransaction transaction = transactions.get(position);
        holder.slNo.setText(String.format("%d", position+1));
        holder.name.setText(String.format("%s", transaction.getName()));
        holder.date.setText(String.format("%s", transaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))));
        holder.amount.setText(String.format("%.2f", transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView slNo;
        private TextView name;
        private TextView date;
        private TextView amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            slNo = itemView.findViewById(R.id.sl_no);
            name = itemView.findViewById(R.id.name);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }

    // == Adding Filter to Recycler View ==

    @Override
    public Filter getFilter() {

        // == Returning the filter which is created ==
        return filter;
    }

    // == Creating Custom filter class for Recycler View Filter ==

    private Filter filter = new Filter() {

        private double total;

        // == This method will filter the User based on the given char sequence ==
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // == Resetting the total amount ==
            total = 0;

            // == Creating a new List which holds the filtered Users ==
            List<HomeScreenTransaction> filteredList = new ArrayList<>();

            // == Checking for the constraint ==
            switch (constraint.toString()){
                case "ALL":
                    // == Showing all the transaction ==
                    filteredList.addAll(allTransaction);
                    // == Counting the Amount ==
                    for(HomeScreenTransaction transaction : allTransaction){
                        total += transaction.getAmount();
                    }
                    break;
                case "TODAY":
                    // == Looping throw all the User to check for char sequence ==
                    for(HomeScreenTransaction transaction : allTransaction){
                        // == Checking the constraint ==
                        if(transaction.getDate().equals(LocalDate.now())){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "YESTERDAY":
                    // == Looping throw all the User to check for char sequence ==
                    for(HomeScreenTransaction transaction : allTransaction){
                        // == Checking the constraint ==
                        if(transaction.getDate().plusDays(1).equals(LocalDate.now())){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "THIS WEEK":
                    // == Looping throw all the User to check for char sequence ==
                    for(HomeScreenTransaction transaction : allTransaction){
                        // == Checking the constraint ==
                        if(transaction.getDate().isAfter(LocalDate.now().minusDays(7)) && transaction.getDate().isBefore(LocalDate.now())){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "THIS MONTH":
                    // == Looping throw all the User to check for char sequence ==
                    LocalDate thisMonthFirstDate = YearMonth.now().atDay(1);
                    LocalDate thisMonthLastDate = YearMonth.now().atEndOfMonth();

                    for(HomeScreenTransaction transaction : allTransaction){

                        // == Checking the constraint ==
                        if(transaction. getDate().equals(thisMonthFirstDate) || transaction.getDate().equals(thisMonthLastDate)){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                        else if(transaction.getDate().isBefore(thisMonthLastDate) && transaction.getDate().isAfter(thisMonthFirstDate)){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }

                    }
                    break;

                case "LAST MONTH":
                    // == Looping throw all the User to check for char sequence ==
                    LocalDate lastMonthFirstDate = YearMonth.now().minusMonths(1).atDay(1);
                    LocalDate lastMonthLastDate = YearMonth.now().minusMonths(1).atEndOfMonth();

                    Log.d("Statistic Adaptor", "performFiltering: firstDate->" + lastMonthFirstDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
                    Log.d("Statistic Adaptor", "performFiltering: lastDate->" + lastMonthLastDate.format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));

                    for(HomeScreenTransaction transaction : allTransaction){

                        // == Checking the constraint ==
                        if(transaction. getDate().equals(lastMonthFirstDate) || transaction.getDate().equals(lastMonthLastDate)){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                        else if(transaction.getDate().isBefore(lastMonthLastDate) && transaction.getDate().isAfter(lastMonthFirstDate)){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                case "LAST 6 MONTHS":
                    // == Looping throw all the User to check for char sequence ==

                    LocalDate firstDay = YearMonth.now().atDay(1).minusMonths(6);

                    for(HomeScreenTransaction transaction : allTransaction){
                        // == Checking the constraint ==
                        if(transaction.getDate().isAfter(firstDay)){
                            filteredList.add(transaction);
                            total += transaction.getAmount();
                        }
                    }
                    break;
                default:
                    transactions.addAll(allTransaction);
                    // == Counting the Amount ==
                    for(HomeScreenTransaction transaction : allTransaction){
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
            transactions.clear();

            // == adding the filtered list to the recycler view ==
            transactions.addAll((Collection<? extends HomeScreenTransaction>) results.values);

            // == Finding the List is Empty TextView by id  and setting the Visibility ==
            TextView isListEmpty = ((Activity) context).findViewById(R.id.is_list_empty);
            if(transactions.isEmpty()){
                isListEmpty.setVisibility(View.VISIBLE);
            }else{
                isListEmpty.setVisibility(View.GONE);
            }

            // == Updating the Amount ==
            TextView totalAmount = ((Activity) context).findViewById(R.id.amount);
            totalAmount.setText(String.format("%s : %.2f", "GRAND TOTAL", total));

            // == notifying recycler view that the data has changed ==
            notifyDataSetChanged();
        }
    };
}