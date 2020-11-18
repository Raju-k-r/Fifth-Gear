package com.krraju.fifthgear.userdetails;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.transation.Transaction;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionAdaptor extends RecyclerView.Adapter<TransactionAdaptor.ViewHolder> {

    // == fields ==
    private List<Transaction> transactions;

    // == Constructor ==
    public TransactionAdaptor(Context context, int userId, TextView isListEmpty) {

        this.transactions = new ArrayList<>();
        // == Starting new Thread for getting data from  database ==
        new Thread(()-> {

            // == Collecting the current user Transaction based on userId ==
            transactions = Database.getInstance(context).transactionDao().getUserTransaction(userId);

            if(transactions.isEmpty()){
                isListEmpty.setVisibility(View.VISIBLE);
            }else{
                isListEmpty.setVisibility(View.GONE);
            }
            // == notifying the data change ==
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);
        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_transaction_details, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.slNo.setText(String.format("%d", position + 1));
        holder.date.setText(transaction.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        holder.amount.setText(String.format("%.2f", transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView slNo;
        private TextView date;
        private TextView amount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slNo = itemView.findViewById(R.id.sl_no);
            date = itemView.findViewById(R.id.date);
            amount = itemView.findViewById(R.id.amount);
        }
    }
}