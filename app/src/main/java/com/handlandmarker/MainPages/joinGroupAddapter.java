package com.handlandmarker.MainPages;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studify.R;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.My_Group;

import java.util.ArrayList;



public class joinGroupAddapter extends RecyclerView.Adapter<joinGroupAddapter.MyNewViewHolder> {

    ArrayList<String>  IDs;

    joinGroupAddapter(ArrayList<String> is)
    {
        IDs = is;
    }

    @NonNull
    @Override
    public MyNewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.join_grp_single_item_layout, parent, false);
        return new MyNewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyNewViewHolder holder, int position) {


        TextView t1 = holder.itemView.findViewById(R.id.tv_grpName);
        FirebaseHelper firebaseHelper = new FirebaseHelper();
        // Replace with your group ID
            firebaseHelper.getGroupNameByID(IDs.get(position), new FirebaseHelper.OnGroupNameFetchedListener() {
                @Override
                public void onGroupNameFetched(String groupName) {
                    if (groupName != null) {
                        // Handle the group name
                        t1.setText(groupName);
                    } else {
                        // Handle the error case
                        Log.e("Firebase", "Failed to fetch group name");
                    }
                }
            });
        Button b1 = holder.itemView.findViewById(R.id.btn_join);
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseHelper.addUserToGroup(IDs.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return IDs.size();
    }

    protected class MyNewViewHolder extends  RecyclerView.ViewHolder{

        public MyNewViewHolder(@NonNull View itemView) {
            super(itemView);

        }
    }
}
