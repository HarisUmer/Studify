package com.handlandmarker.MainPages;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.studify.R;
import com.handlandmarker.Channels;
import com.handlandmarker.accets.CurrentUser;
import com.handlandmarker.accets.My_Group;

import java.nio.channels.Channel;
import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter<ChatsAdapter.ViewHolder> {

        ArrayList<My_Group> list;
        Context c1 ;
        public ChatsAdapter(ArrayList<My_Group> l,Context c) {
            list=l;
            c1=c;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_chat_item,parent,false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.tvName.setText(CurrentUser.Groups.get(position).getGroupName());
            holder.tvMsg.setText(CurrentUser.Groups.get(position).getGroupID());
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(c1, Channels.class);
                    CurrentUser.CurrentGroup = CurrentUser.Groups.get(position);
                    i.putExtra("groupName", CurrentUser.Groups.get(position).getGroupName());
                    i.putExtra("groupID", CurrentUser.Groups.get(position).getGroupID());
                    c1.startActivity(i);
                }
            });
        }

        @Override
        public int getItemCount() {
            return CurrentUser.Groups.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {
            TextView tvMsg, tvName, tvCount, tvTime;
            Button b1 ;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMsg = itemView.findViewById(R.id.tvLastMsg);
                tvTime = itemView.findViewById(R.id.tvTime);
                tvName = itemView.findViewById(R.id.tvContactName);
                tvCount = itemView.findViewById(R.id.tvMsgCount);

            }
        }
    }

