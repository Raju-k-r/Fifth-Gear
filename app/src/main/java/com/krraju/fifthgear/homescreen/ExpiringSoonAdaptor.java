package com.krraju.fifthgear.homescreen;

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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpiringSoonAdaptor extends RecyclerView.Adapter<ExpiringSoonAdaptor.ViewHolder> {

    // == constants ==
    private static final String TAG = ExpiringSoonAdaptor.class.getSimpleName();
    // == fields ==
    private List<User> userList;

    // == constructor ==

    public ExpiringSoonAdaptor(Context context) {
        this.userList = new ArrayList<>();

        // == Creating new Thread for performing database operation ==
        new Thread(()->{
            // == Getting All Users ==
            List<User> userList = Database.getInstance(context).userDao().getUserOnDueDate();

            // == Iterating All user ==
            for(User user : userList){
                if(user.getStatus() == Status.ACTIVE){
                    if(user.getDueDate().minusDays(1).equals(LocalDate.now()) || user.getDueDate().minusDays(2).equals(LocalDate.now())){
                        this.userList.add(user);
                    }
                }
            }

            // == finding the view by id ==
            TextView isExpiringSoonEmpty = ((Activity) context).findViewById(R.id.is_expiring_soon_empty);

            // == checking the List and setting the visibility ==
            if(this.userList.isEmpty()){
                isExpiringSoonEmpty.setVisibility(View.VISIBLE);
            }else{
                isExpiringSoonEmpty.setVisibility(View.INVISIBLE);
            }
            // == notifying data change ==
            notifyDataSetChanged();
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
        holder.userImage.setImageURI(Uri.parse(user.getImagePath()));
        holder.userName.setText(String.format("%s %s", user.getFirstName(), user.getLastName()));
        holder.expiringSoon.setBackgroundResource(R.drawable.expiring_soon_background_layout);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView userImage;
        private TextView userName;
        private View expiringSoon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.user_photo);
            userName = itemView.findViewById(R.id.user_name);
            expiringSoon = itemView.findViewById(R.id.active_or_inactive);
        }
    }
}