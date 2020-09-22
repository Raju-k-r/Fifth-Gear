package com.krraju.fifthgear.dueamount;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;

import java.util.List;

public class DueAmountAdaptor extends RecyclerView.Adapter<DueAmountAdaptor.ViewHolder> {

    // == fields ==
    private List<User> users;

    // == constructor ==
    public DueAmountAdaptor(Context context) {

        new Thread(()->{
            // == Collecting the data from database ==
            users = Database.getInstance(context).userDao().getUsersHavingDueAmount();

            ((Activity) context).runOnUiThread(()->{
                TextView isListEmpty = ((Activity)context).findViewById(R.id.is_list_empty);
                if(users == null || users.isEmpty()){
                    isListEmpty.setVisibility(View.VISIBLE);
                }else{
                    isListEmpty.setVisibility(View.GONE);
                }
            });
            // == notifying the data change ==
            notifyDataSetChanged();
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
        holder.userName.setText(String.format("%s %s",user.getFirstName(), user.getLastName()));
        holder.imageView.setImageURI(Uri.parse(user.getImagePath()));
        if(user.getStatus() == Status.ACTIVE){
            holder.view.setBackgroundResource(R.drawable.active_background_layout);
        }else{
            holder.view.setBackgroundResource(R.drawable.inactive_background_layout);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView userName;
        private TextView dueAmount;
        private ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.user_photo);
            view = itemView.findViewById(R.id.active_or_inactive);
            userName = itemView.findViewById(R.id.user_name);
            dueAmount = itemView.findViewById(R.id.due_amount);
        }
    }
}