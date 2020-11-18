package com.krraju.fifthgear.homescreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;
import com.krraju.fifthgear.storage.entity.utils.HomeScreenTransaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdaptor extends RecyclerView.Adapter<TransactionAdaptor.ViewHolder> {

    // == Constants ==
    private static final String TAG = TransactionAdaptor.class.getSimpleName();

    // == filed ==
    private List<HomeScreenTransaction> transactions;

    // == Constructor ==
    public TransactionAdaptor(Context context) {

        this.transactions = new ArrayList<>();

        // == Collecting the data from database ==
        new Thread(()->{
            List<Transaction> transactions = Database.getInstance(context).transactionDao().getLastTenTransaction();
            for(Transaction transaction : transactions){
                this.transactions.add(new HomeScreenTransaction(transaction, context));
            }
            Log.d(TAG, "TransactionAdaptor: transactions->" + this.transactions);
            if(!this.transactions.isEmpty()){
                // == finding the text view using context and setting visibility to gone ==
                ((Activity) context).findViewById(R.id.is_transaction_empty).setVisibility(View.GONE);
                Log.d(TAG, "TransactionAdaptor: inside if");
            }else{
                // == finding the text view using context and setting visibility to visible ==
                ((Activity) context).findViewById(R.id.is_transaction_empty).setVisibility(View.VISIBLE);
                Log.d(TAG, "TransactionAdaptor: inside else");
            }
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
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
        holder.date.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
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
}