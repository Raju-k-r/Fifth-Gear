package com.krraju.fifthgear.viewuser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.krraju.fifthgear.R;
import com.krraju.fifthgear.storage.database.Database;
import com.krraju.fifthgear.storage.entity.user.User;
import com.krraju.fifthgear.storage.entity.user.enums.Status;
import com.krraju.fifthgear.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ViewUserAdaptor extends RecyclerView.Adapter<ViewUserAdaptor.ViewHolder> implements Filterable {

    // == fields ==
    private Context context;
    private List<User> allUsersList;
    private List<User> usersList;
    private RadioGroup radioGroup;

    // == Constructors ==
    public ViewUserAdaptor(Context context){

        // == initializing the filed ==
        this.context = context;
        this.usersList = new ArrayList<>();
        this.radioGroup = ((Activity) context).findViewById(R.id.radioGroup2);

        // == Running the new Thread  to perform database operation ==
        new Thread(()->{

            // == Retrieving the data from the database ==
            allUsersList = Database.getInstance(context).userDao().getAllUser();

            // == Taking the copy all the user to other list ==
            usersList = new ArrayList<>(allUsersList);

            // == showing the text if list is empty ==
            if(usersList.isEmpty()){
                ((Activity) context).findViewById(R.id.is_list_empty).setVisibility(View.VISIBLE);
            }else{
                ((Activity) context).findViewById(R.id.is_list_empty).setVisibility(View.INVISIBLE);
            }

            // == notifying recycler view that the data has changed ==
            ((Activity) context).runOnUiThread(this::notifyDataSetChanged);

        }).start();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_user_recycler_view_layout, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        User user = usersList.get(position);
        holder.userName.setText(user.getFirstName() + " " + user.getLastName());
        holder.imageView.setImageURI(Uri.parse(user.getImagePath()));
        holder.linearLayout.setOnClickListener(v-> {
            Intent intent = new Intent(context, UserDetails.class);
            intent.putExtra("userId", user.getUser_id());
            context.startActivity(intent);
        });
        if(user.getStatus() == Status.INACTIVE){
            holder.activeOrInActive.setBackgroundResource(R.drawable.inactive_background_layout);
        }else{
            holder.activeOrInActive.setBackgroundResource(R.drawable.active_background_layout);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        private TextView userName;
        private ImageView imageView;
        private LinearLayout linearLayout;
        private View activeOrInActive;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.layout);
            userName = itemView.findViewById(R.id.user_name);
            imageView = itemView.findViewById(R.id.user_photo);
            activeOrInActive = itemView.findViewById(R.id.active_or_inactive);
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

        // == This method will filter the User based on the given char sequence ==
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            // == Creating a new List which holds the filtered Users based on the condition ==
            List<User> conditionalArray = new ArrayList<>();

            switch (radioGroup.getCheckedRadioButtonId()){
                case R.id.all:
                    conditionalArray.addAll(allUsersList);
                    break;
                case R.id.active:
                    for(User user : allUsersList){
                        if(user.getStatus() == Status.ACTIVE){
                            conditionalArray.add(user);
                        }
                    }
                    break;
                case R.id.inActive:
                    for(User user : allUsersList){
                        if(user.getStatus() == Status.INACTIVE){
                            conditionalArray.add(user);
                        }
                    }
                    break;
            }

            // == Creating a new List which holds the filtered Users ==
            List<User> filteredList = new ArrayList<>();

            // == checking if the char sequence is empty ==
            if(constraint.toString().isEmpty()){
                filteredList.addAll(conditionalArray);
            }else{

                // == Looping throw all the User to check for char sequence ==
                for(User user : conditionalArray){

                    // == Getting the user Full name and checking with char sequence ==
                    String fullName = user.getFirstName() + user.getLastName();
                    if(fullName.toUpperCase().contains(constraint.toString().toUpperCase())){

                        // == Since char sequence matched adding the user to filtered list ==
                        filteredList.add(user);
                    }
                }
            }

            // == Creating the Filter result Object which holds our result ==
            FilterResults filterResults = new FilterResults();

            // == Adding the Filtered list to filter result value ==
            filterResults.values = filteredList;

            return filterResults;
        }

        // == This method will update the UI after the filter finished ==
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            // == Clearing the current items in recycler view ==
            usersList.clear();

            // == adding the filtered list to the recycler view ==
            usersList.addAll((Collection<? extends User>) results.values);


            // == showing the text if list is empty ==
            if(usersList.isEmpty()){
                ((Activity) context).findViewById(R.id.is_list_empty).setVisibility(View.VISIBLE);
            }else{
                ((Activity) context).findViewById(R.id.is_list_empty).setVisibility(View.INVISIBLE);
            }

            // == notifying recycler view that the data has changed ==
            notifyDataSetChanged();
        }
    };
}