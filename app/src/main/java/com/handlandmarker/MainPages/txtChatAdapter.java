package com.handlandmarker.MainPages;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studify.R;
import com.google.common.base.Strings;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.Message;

import java.util.ArrayList;

public class txtChatAdapter extends RecyclerView.Adapter {

    ArrayList<Message> list;
    public txtChatAdapter(ArrayList<Message> l) {
        list=l;
    }

    @Override
    public int getItemViewType(int position) {
       if(list.get(position).getSender().contains(CurrentUser.globalVariable.getUserID()))
       {
           return 1;
       }
        return 2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.sent_msg_layout, parent, false);
            return new ViewHolder(v);
        }
        else
        {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rcv_msg_layout, parent, false);
            return new ViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        if(viewHolder.tvSenderName!= null)
        {
            String senderID = message.getSender();
            if (!senderID.isEmpty()) {
                // Fetch the sender's name from Firebase using the ID
                FirebaseHelper firebaseHelper = new FirebaseHelper();
                firebaseHelper.getName(senderID, new FirebaseHelper.OnNameFetchedListener() {
                    @Override
                    public void onNameFetched(String name) {
                        if (!Strings.isNullOrEmpty(name)) {
                            // Set the sender's name to tvSenderName
                            viewHolder.tvSenderName.setText(name);
                        } else {
                            // Set a default text or handle the case where the name is not available
                        }
                    }
                });
            } else {
                // Set a default text or handle the case where the sender's ID is not available
                viewHolder.tvSenderName.setText("Unknown Sender");
            }
            viewHolder.tvMessage.setText(message.getText());
            viewHolder.tvTimestamp.setText(message.getTime());
        }
        else {
            viewHolder.MyTime.setText(message.getTime());
            viewHolder.MyMessage.setText(message.getText());
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSenderName;
        TextView MyMessage;
        TextView MyTime;
        TextView tvMessage;
        TextView tvTimestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSenderName = itemView.findViewById(R.id.tv_sender_name); // Initialize the sender's name view
            tvMessage = itemView.findViewById(R.id.tv_msg_others); // Initialize the message view
            tvTimestamp = itemView.findViewById(R.id.tv_msg_time_Sender); // Initialize the timestamp view
            MyMessage = itemView.findViewById(R.id.tv_msg_me);
            MyTime = itemView.findViewById(R.id.tv_msg_time_me);
        }
    }
}
